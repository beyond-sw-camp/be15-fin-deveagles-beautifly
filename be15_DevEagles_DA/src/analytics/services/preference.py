"""Customer Service Preference Analysis Service."""

import logging
from datetime import datetime, timedelta
from typing import Dict, List, Optional, Tuple
from decimal import Decimal

import pandas as pd
from analytics.core.database import get_analytics_db
from analytics.core.logging import get_logger


class CustomerServicePreferenceService:
    """고객 선호 시술 분석 서비스."""
    
    def __init__(self):
        self.analytics_db = get_analytics_db()
        self.logger = get_logger(__name__)
    
    def get_customer_preferences(self, customer_id: int, limit: int = 10) -> List[Dict]:
        """특정 고객의 선호 시술 목록 조회."""
        try:
            query = """
            SELECT * FROM customer_service_preferences 
            WHERE customer_id = ?
            ORDER BY preference_rank ASC
            LIMIT ?
            """
            
            result = self.analytics_db.execute(query, [customer_id, limit]).fetchall()
            
            preferences = []
            for row in result:
                pref = {
                    'customer_id': row[0],
                    'service_id': row[1],
                    'service_name': row[2],
                    'service_category': row[3],
                    'total_visits': row[4],
                    'total_amount': float(row[5]),
                    'avg_amount': float(row[6]),
                    'first_service_date': row[7],
                    'last_service_date': row[8],
                    'preference_rank': row[9],
                    'visit_ratio': row[10],
                    'amount_ratio': row[11],
                    'recent_visits_3m': row[12],
                    'days_since_last_service': row[13],
                    'is_frequent_service': row[9] <= 3,
                    'is_high_value_service': row[11] >= 0.2
                }
                preferences.append(pref)
            
            return preferences
            
        except Exception as e:
            self.logger.error(f"Failed to get customer preferences for {customer_id}: {e}")
            return []
    
    def get_customer_service_tags(self, customer_id: int) -> Optional[Dict]:
        """특정 고객의 서비스 태그 조회."""
        try:
            query = """
            SELECT * FROM customer_service_tags 
            WHERE customer_id = ?
            """
            
            result = self.analytics_db.execute(query, [customer_id]).fetchone()
            
            if not result:
                return None
            
            preferred_categories = []
            if result[4]:
                try:
                    import ast
                    preferred_categories = ast.literal_eval(result[4])
                except:
                    preferred_categories = []
            
            loyalty_services = []
            if result[6]:
                try:
                    import ast
                    loyalty_services = ast.literal_eval(result[6])
                except:
                    loyalty_services = []
            
            tags = {
                'customer_id': result[0],
                'top_service_1': result[1],
                'top_service_2': result[2],
                'top_service_3': result[3],
                'preferred_categories': preferred_categories,
                'service_variety_score': result[5],
                'loyalty_services': loyalty_services,
                'avg_service_price': float(result[7]),
                'preferred_price_range': result[8],
                'updated_at': result[9]
            }
            
            # 태그 생성
            tags['service_tags'] = self._generate_service_tags(tags)
            tags['category_tags'] = self._generate_category_tags(tags['preferred_categories'])
            tags['all_preference_tags'] = self._generate_all_tags(tags)
            
            return tags
            
        except Exception as e:
            self.logger.error(f"Failed to get service tags for {customer_id}: {e}")
            return None
    
    def get_service_popularity_ranking(self, limit: int = 20) -> List[Dict]:
        """서비스별 인기도 순위 조회."""
        try:
            query = """
            SELECT 
                service_name,
                service_category,
                COUNT(*) as customer_count,
                SUM(total_visits) as total_visits,
                SUM(total_amount) as total_revenue,
                AVG(avg_amount) as avg_price,
                AVG(visit_ratio) as avg_visit_ratio
            FROM customer_service_preferences
            GROUP BY service_name, service_category
            ORDER BY customer_count DESC, total_revenue DESC
            LIMIT ?
            """
            
            result = self.analytics_db.execute(query, [limit]).fetchall()
            
            rankings = []
            for i, row in enumerate(result, 1):
                ranking = {
                    'rank': i,
                    'service_name': row[0],
                    'service_category': row[1],
                    'customer_count': row[2],
                    'total_visits': row[3],
                    'total_revenue': float(row[4]),
                    'avg_price': float(row[5]),
                    'avg_visit_ratio': row[6],
                    'popularity_score': self._calculate_popularity_score(row[2], row[3], row[4])
                }
                rankings.append(ranking)
            
            return rankings
            
        except Exception as e:
            self.logger.error(f"Failed to get service popularity ranking: {e}")
            return []
    
    def get_category_preferences_distribution(self) -> Dict:
        """카테고리별 선호도 분포 분석."""
        try:
            query = """
            SELECT 
                service_category,
                COUNT(DISTINCT customer_id) as customer_count,
                SUM(total_visits) as total_visits,
                SUM(total_amount) as total_revenue,
                AVG(avg_amount) as avg_price
            FROM customer_service_preferences
            GROUP BY service_category
            ORDER BY customer_count DESC
            """
            
            result = self.analytics_db.execute(query).fetchall()
            
            categories = {}
            total_customers = sum(row[1] for row in result)
            total_revenue = sum(row[3] for row in result)
            
            for row in result:
                category = row[0]
                categories[category] = {
                    'customer_count': row[1],
                    'customer_ratio': row[1] / total_customers if total_customers > 0 else 0,
                    'total_visits': row[2],
                    'total_revenue': float(row[3]),
                    'revenue_ratio': float(row[3]) / total_revenue if total_revenue > 0 else 0,
                    'avg_price': float(row[4])
                }
            
            return {
                'categories': categories,
                'total_customers': total_customers,
                'total_revenue': total_revenue
            }
            
        except Exception as e:
            self.logger.error(f"Failed to get category preferences distribution: {e}")
            return {}
    
    def find_similar_customers(self, customer_id: int, limit: int = 10) -> List[Dict]:
        """유사한 선호도를 가진 고객 찾기."""
        try:
            # 대상 고객의 선호 서비스 조회
            target_preferences = self.get_customer_preferences(customer_id, limit=5)
            if not target_preferences:
                return []
            
            target_services = [pref['service_name'] for pref in target_preferences]
            
            # 유사한 서비스를 선호하는 고객들 찾기
            placeholders = ','.join(['?' for _ in target_services])
            query = f"""
            SELECT 
                customer_id,
                COUNT(*) as common_services,
                AVG(preference_rank) as avg_rank,
                GROUP_CONCAT(service_name) as common_service_names
            FROM customer_service_preferences 
            WHERE service_name IN ({placeholders})
                AND customer_id != ?
            GROUP BY customer_id
            HAVING common_services >= 2
            ORDER BY common_services DESC, avg_rank ASC
            LIMIT ?
            """
            
            params = target_services + [customer_id, limit]
            result = self.analytics_db.execute(query, params).fetchall()
            
            similar_customers = []
            for row in result:
                similarity = {
                    'customer_id': row[0],
                    'common_services_count': row[1],
                    'avg_preference_rank': row[2],
                    'common_services': row[3].split(',') if row[3] else [],
                    'similarity_score': self._calculate_similarity_score(row[1], row[2])
                }
                similar_customers.append(similarity)
            
            return similar_customers
            
        except Exception as e:
            self.logger.error(f"Failed to find similar customers for {customer_id}: {e}")
            return []
    
    def get_service_recommendations(self, customer_id: int, limit: int = 5) -> List[Dict]:
        """고객 맞춤 서비스 추천."""
        try:
            # 유사한 고객들 찾기
            similar_customers = self.find_similar_customers(customer_id, limit=20)
            if not similar_customers:
                return []
            
            similar_customer_ids = [c['customer_id'] for c in similar_customers]
            
            # 대상 고객이 아직 이용하지 않은 서비스 중 유사 고객들이 선호하는 서비스
            placeholders = ','.join(['?' for _ in similar_customer_ids])
            query = f"""
            SELECT 
                p.service_name,
                p.service_category,
                COUNT(*) as recommended_by_count,
                AVG(p.preference_rank) as avg_rank,
                AVG(p.avg_amount) as avg_price,
                AVG(p.visit_ratio) as avg_visit_ratio
            FROM customer_service_preferences p
            WHERE p.customer_id IN ({placeholders})
                AND p.service_name NOT IN (
                    SELECT service_name 
                    FROM customer_service_preferences 
                    WHERE customer_id = ?
                )
            GROUP BY p.service_name, p.service_category
            ORDER BY recommended_by_count DESC, avg_rank ASC
            LIMIT ?
            """
            
            params = similar_customer_ids + [customer_id, limit]
            result = self.analytics_db.execute(query, params).fetchall()
            
            recommendations = []
            for row in result:
                recommendation = {
                    'service_name': row[0],
                    'service_category': row[1],
                    'recommended_by_count': row[2],
                    'avg_preference_rank': row[3],
                    'avg_price': float(row[4]),
                    'avg_visit_ratio': row[5],
                    'recommendation_score': self._calculate_recommendation_score(row[2], row[3], row[5]),
                    'reason': f"{row[2]}명의 유사한 고객이 선호하는 서비스"
                }
                recommendations.append(recommendation)
            
            return recommendations
            
        except Exception as e:
            self.logger.error(f"Failed to get service recommendations for {customer_id}: {e}")
            return []
    
    def analyze_service_trends(self, days: int = 30) -> Dict:
        """서비스 트렌드 분석."""
        try:
            # 최근 기간 동안의 서비스 선호도 변화
            recent_date = datetime.now() - timedelta(days=days)
            
            query = """
            SELECT 
                service_name,
                service_category,
                COUNT(*) as current_customers,
                AVG(recent_visits_3m) as avg_recent_visits,
                SUM(CASE WHEN days_since_last_service <= 30 THEN 1 ELSE 0 END) as active_customers
            FROM customer_service_preferences
            WHERE updated_at >= ?
            GROUP BY service_name, service_category
            ORDER BY current_customers DESC
            """
            
            result = self.analytics_db.execute(query, [recent_date]).fetchall()
            
            trends = []
            for row in result:
                trend = {
                    'service_name': row[0],
                    'service_category': row[1],
                    'current_customers': row[2],
                    'avg_recent_visits': row[3],
                    'active_customers': row[4],
                    'activity_rate': row[4] / row[2] if row[2] > 0 else 0,
                    'trend_score': self._calculate_trend_score(row[2], row[3], row[4])
                }
                trends.append(trend)
            
            return {
                'trends': trends,
                'analysis_period_days': days,
                'total_services': len(trends)
            }
            
        except Exception as e:
            self.logger.error(f"Failed to analyze service trends: {e}")
            return {}
    
    def get_customer_journey_analysis(self, customer_id: int) -> Dict:
        """고객 서비스 이용 여정 분석."""
        try:
            preferences = self.get_customer_preferences(customer_id)
            if not preferences:
                return {}
            
            # 시간순으로 정렬
            sorted_prefs = sorted(preferences, key=lambda x: x['first_service_date'])
            
            journey = {
                'customer_id': customer_id,
                'total_services_tried': len(preferences),
                'service_journey': [],
                'preference_evolution': self._analyze_preference_evolution(sorted_prefs),
                'loyalty_pattern': self._analyze_loyalty_pattern(preferences),
                'spending_pattern': self._analyze_spending_pattern(preferences)
            }
            
            for pref in sorted_prefs:
                journey_step = {
                    'service_name': pref['service_name'],
                    'service_category': pref['service_category'],
                    'first_try_date': pref['first_service_date'],
                    'last_visit_date': pref['last_service_date'],
                    'total_visits': pref['total_visits'],
                    'current_rank': pref['preference_rank'],
                    'visit_ratio': pref['visit_ratio'],
                    'is_active': pref['days_since_last_service'] <= 60 if pref['days_since_last_service'] else False
                }
                journey['service_journey'].append(journey_step)
            
            return journey
            
        except Exception as e:
            self.logger.error(f"Failed to analyze customer journey for {customer_id}: {e}")
            return {}
    
    # Private helper methods
    def _generate_service_tags(self, tags_data: Dict) -> List[str]:
        """서비스 태그 생성."""
        service_tags = []
        for i in range(1, 4):
            service = tags_data.get(f'top_service_{i}')
            if service:
                service_tags.append(f"선호_{service}")
        return service_tags
    
    def _generate_category_tags(self, categories: List[str]) -> List[str]:
        """카테고리 태그 생성."""
        return [f"카테고리_{cat}" for cat in categories]
    
    def _generate_all_tags(self, tags_data: Dict) -> List[str]:
        """모든 태그 통합 생성."""
        all_tags = tags_data['service_tags'] + tags_data['category_tags']
        
        # 다양성 태그
        variety_score = tags_data['service_variety_score']
        if variety_score >= 0.7:
            all_tags.append("다양한_시술_선호")
        elif variety_score <= 0.3:
            all_tags.append("특정_시술_집중")
        
        # 가격대 태그
        price_range = tags_data['preferred_price_range']
        all_tags.append(f"{price_range}_가격대_선호")
        
        return all_tags
    
    def _calculate_popularity_score(self, customer_count: int, total_visits: int, total_revenue: float) -> float:
        """인기도 점수 계산."""
        # 고객 수, 방문 횟수, 매출을 종합한 점수
        revenue_float = float(total_revenue) if total_revenue else 0.0
        return (customer_count * 0.4) + (total_visits * 0.3) + (revenue_float / 100000 * 0.3)
    
    def _calculate_similarity_score(self, common_services: int, avg_rank: float) -> float:
        """유사도 점수 계산."""
        # 공통 서비스 수와 평균 선호도 순위를 고려
        return common_services * (5 - min(avg_rank, 5)) / 5
    
    def _calculate_recommendation_score(self, recommended_count: int, avg_rank: float, avg_ratio: float) -> float:
        """추천 점수 계산."""
        return (recommended_count * 0.4) + ((5 - min(avg_rank, 5)) * 0.3) + (avg_ratio * 0.3)
    
    def _calculate_trend_score(self, customers: int, recent_visits: float, active_customers: int) -> float:
        """트렌드 점수 계산."""
        activity_rate = active_customers / customers if customers > 0 else 0
        return (customers * 0.3) + (recent_visits * 0.3) + (activity_rate * 0.4)
    
    def _analyze_preference_evolution(self, sorted_preferences: List[Dict]) -> Dict:
        """선호도 변화 분석."""
        if len(sorted_preferences) < 2:
            return {'evolution_type': 'stable', 'description': '변화 분석 불가'}
        
        # 첫 번째와 마지막 서비스 비교
        first_service = sorted_preferences[0]
        last_service = sorted_preferences[-1]
        
        if first_service['service_category'] == last_service['service_category']:
            return {'evolution_type': 'consistent', 'description': '일관된 카테고리 선호'}
        else:
            return {'evolution_type': 'exploratory', 'description': '다양한 카테고리 탐색'}
    
    def _analyze_loyalty_pattern(self, preferences: List[Dict]) -> Dict:
        """충성도 패턴 분석."""
        high_loyalty_services = [p for p in preferences if p['total_visits'] >= 5]
        loyalty_score = len(high_loyalty_services) / len(preferences) if preferences else 0
        
        if loyalty_score >= 0.6:
            return {'pattern': 'high_loyalty', 'score': loyalty_score, 'description': '특정 서비스에 충성도 높음'}
        elif loyalty_score >= 0.3:
            return {'pattern': 'moderate_loyalty', 'score': loyalty_score, 'description': '적당한 충성도'}
        else:
            return {'pattern': 'low_loyalty', 'score': loyalty_score, 'description': '다양한 서비스 시도'}
    
    def _analyze_spending_pattern(self, preferences: List[Dict]) -> Dict:
        """지출 패턴 분석."""
        if not preferences:
            return {'pattern': 'no_data'}
        
        avg_amounts = [p['avg_amount'] for p in preferences]
        min_amount = min(avg_amounts)
        max_amount = max(avg_amounts)
        avg_amount = sum(avg_amounts) / len(avg_amounts)
        
        if max_amount / min_amount > 3:
            pattern = 'varied_spending'
            description = '가격대 다양하게 이용'
        elif avg_amount >= 100000:
            pattern = 'premium_focused'
            description = '고가 서비스 선호'
        elif avg_amount <= 50000:
            pattern = 'budget_conscious'
            description = '합리적 가격대 선호'
        else:
            pattern = 'moderate_spending'
            description = '중간 가격대 선호'
        
        return {
            'pattern': pattern,
            'description': description,
            'avg_amount': avg_amount,
            'price_range': max_amount - min_amount
        }