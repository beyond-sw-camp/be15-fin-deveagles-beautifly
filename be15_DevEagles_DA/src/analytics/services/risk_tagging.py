"""Customer Risk Tagging Service."""

import logging
from datetime import datetime, timedelta
from typing import Dict, List, Optional, Tuple
from enum import Enum
from decimal import Decimal

import pandas as pd
from analytics.core.database import get_analytics_db
from analytics.core.config import get_settings
from analytics.core.logging import get_logger
from analytics.models.base import RiskTag


class RiskLevel(str, Enum):
    """위험 수준 정의."""
    LOW = "low"
    MEDIUM = "medium"
    HIGH = "high"
    CRITICAL = "critical"


class CustomerRiskTaggingService:
    """고객 이탈위험 태깅 서비스."""
    
    def __init__(self):
        self.analytics_db = get_analytics_db()
        self.logger = get_logger(__name__)
        self.settings = get_settings()
    
    def analyze_customer_risk(self, customer_id: int) -> Dict:
        """특정 고객의 이탈위험 분석."""
        try:
            # 고객 기본 정보 조회
            customer_data = self._get_customer_analytics(customer_id)
            if not customer_data:
                return {'error': f'고객 {customer_id} 데이터 없음'}
            
            # 위험 요인 분석
            risk_factors = self._analyze_risk_factors(customer_data)
            
            # 위험 점수 계산
            risk_score = self._calculate_risk_score(risk_factors)
            
            # 위험 수준 결정
            risk_level = self._determine_risk_level(risk_score)
            
            # 추천 태그 생성
            recommended_tags = self._generate_risk_tags(customer_data, risk_factors, risk_level)
            
            # 추천 액션 생성
            recommended_actions = self._generate_recommended_actions(customer_data, risk_factors, risk_level)
            
            return {
                'customer_id': customer_id,
                'risk_score': risk_score,
                'risk_level': risk_level,
                'risk_factors': risk_factors,
                'recommended_tags': recommended_tags,
                'recommended_actions': recommended_actions,
                'assessment_date': datetime.now()
            }
            
        except Exception as e:
            self.logger.error(f"Failed to analyze risk for customer {customer_id}: {e}")
            return {'error': str(e)}
    
    def tag_all_customers(self, dry_run: bool = False) -> Dict:
        """모든 고객의 이탈위험 태깅."""
        try:
            # 모든 고객 조회
            customers = self._get_all_customers()
            
            results = {
                'total_customers': len(customers),
                'tagged_customers': 0,
                'high_risk_customers': 0,
                'medium_risk_customers': 0,
                'low_risk_customers': 0,
                'tags_created': 0,
                'errors': []
            }
            
            for customer in customers:
                try:
                    # 위험 분석
                    risk_analysis = self.analyze_customer_risk(customer['customer_id'])
                    
                    if 'error' in risk_analysis:
                        results['errors'].append(f"고객 {customer['customer_id']}: {risk_analysis['error']}")
                        continue
                    
                    # 위험 수준별 카운트
                    risk_level = risk_analysis['risk_level']
                    if risk_level == RiskLevel.HIGH or risk_level == RiskLevel.CRITICAL:
                        results['high_risk_customers'] += 1
                    elif risk_level == RiskLevel.MEDIUM:
                        results['medium_risk_customers'] += 1
                    else:
                        results['low_risk_customers'] += 1
                    
                    # 태그 적용 (dry_run이 아닌 경우)
                    if not dry_run:
                        tags_applied = self._apply_risk_tags(
                            customer['customer_id'], 
                            risk_analysis['recommended_tags']
                        )
                        results['tags_created'] += tags_applied
                        
                        # 고객 분석 데이터 업데이트
                        self._update_customer_risk_data(customer['customer_id'], risk_analysis)
                    
                    results['tagged_customers'] += 1
                    
                except Exception as e:
                    error_msg = f"고객 {customer['customer_id']} 태깅 실패: {str(e)}"
                    results['errors'].append(error_msg)
                    self.logger.error(error_msg)
            
            return results
            
        except Exception as e:
            self.logger.error(f"Failed to tag all customers: {e}")
            return {'error': str(e)}
    
    def get_risk_distribution(self) -> Dict:
        """위험 수준별 고객 분포 조회."""
        try:
            query = """
            SELECT 
                churn_risk_level,
                COUNT(*) as customer_count,
                AVG(churn_risk_score) as avg_risk_score,
                AVG(total_visits) as avg_visits,
                AVG(total_amount) as avg_amount,
                AVG(days_since_last_visit) as avg_days_since_visit
            FROM customer_analytics
            GROUP BY churn_risk_level
            ORDER BY 
                CASE churn_risk_level 
                    WHEN 'critical' THEN 4
                    WHEN 'high' THEN 3
                    WHEN 'medium' THEN 2
                    ELSE 1
                END DESC
            """
            
            result = self.analytics_db.execute(query).fetchall()
            
            distribution = {}
            total_customers = 0
            
            for row in result:
                risk_level = row[0]
                customer_count = row[1]
                total_customers += customer_count
                
                distribution[risk_level] = {
                    'customer_count': customer_count,
                    'avg_risk_score': float(row[2]) if row[2] else 0.0,
                    'avg_visits': float(row[3]) if row[3] else 0.0,
                    'avg_amount': float(row[4]) if row[4] else 0.0,
                    'avg_days_since_visit': float(row[5]) if row[5] else 0.0
                }
            
            # 비율 계산
            for risk_level in distribution:
                distribution[risk_level]['percentage'] = (
                    distribution[risk_level]['customer_count'] / total_customers * 100 
                    if total_customers > 0 else 0
                )
            
            return {
                'distribution': distribution,
                'total_customers': total_customers
            }
            
        except Exception as e:
            self.logger.error(f"Failed to get risk distribution: {e}")
            return {}
    
    def get_high_risk_customers(self, limit: int = 50) -> List[Dict]:
        """고위험 고객 목록 조회."""
        try:
            query = """
            SELECT 
                customer_id, name, phone, email,
                churn_risk_score, churn_risk_level,
                total_visits, total_amount, days_since_last_visit,
                segment, last_visit_date
            FROM customer_analytics
            WHERE churn_risk_level IN ('high', 'critical')
            ORDER BY churn_risk_score DESC, days_since_last_visit DESC
            LIMIT ?
            """
            
            result = self.analytics_db.execute(query, [limit]).fetchall()
            
            high_risk_customers = []
            for row in result:
                customer = {
                    'customer_id': row[0],
                    'name': row[1],
                    'phone': row[2],
                    'email': row[3],
                    'churn_risk_score': float(row[4]),
                    'churn_risk_level': row[5],
                    'total_visits': row[6],
                    'total_amount': float(row[7]),
                    'days_since_last_visit': row[8],
                    'segment': row[9],
                    'last_visit_date': row[10],
                    'urgency_score': self._calculate_urgency_score(row[4], row[8])
                }
                high_risk_customers.append(customer)
            
            return high_risk_customers
            
        except Exception as e:
            self.logger.error(f"Failed to get high risk customers: {e}")
            return []
    
    def get_customer_risk_tags(self, customer_id: int) -> List[Dict]:
        """특정 고객의 위험 태그 조회."""
        try:
            # CRM 데이터베이스에서 태그 조회 (실제 구현에서는 CRM DB 연결 필요)
            # 여기서는 Analytics DB에서 시뮬레이션
            query = """
            SELECT 
                customer_id, churn_risk_level, churn_risk_score,
                days_since_last_visit, segment, total_visits
            FROM customer_analytics
            WHERE customer_id = ?
            """
            
            result = self.analytics_db.execute(query, [customer_id]).fetchone()
            
            if not result:
                return []
            
            # 현재 상태 기반으로 태그 생성
            risk_level = result[1]
            risk_score = result[2]
            days_since_visit = result[3]
            segment = result[4]
            total_visits = result[5]
            
            tags = []
            
            # 위험 수준 태그
            if risk_level == 'critical':
                tags.append({
                    'tag_type': RiskTag.CHURN_RISK_HIGH.value,
                    'tag_value': f'위험점수: {risk_score:.1f}',
                    'priority': 10,
                    'expires_at': datetime.now() + timedelta(days=7)
                })
            elif risk_level == 'high':
                tags.append({
                    'tag_type': RiskTag.CHURN_RISK_HIGH.value,
                    'tag_value': f'위험점수: {risk_score:.1f}',
                    'priority': 8,
                    'expires_at': datetime.now() + timedelta(days=14)
                })
            elif risk_level == 'medium':
                tags.append({
                    'tag_type': RiskTag.CHURN_RISK_MEDIUM.value,
                    'tag_value': f'위험점수: {risk_score:.1f}',
                    'priority': 5,
                    'expires_at': datetime.now() + timedelta(days=30)
                })
            
            # 세그먼트별 특수 태그
            if segment == 'new' and total_visits == 1 and days_since_visit > 7:
                tags.append({
                    'tag_type': RiskTag.FIRST_VISIT_FOLLOW_UP.value,
                    'tag_value': f'{days_since_visit}일 경과',
                    'priority': 9,
                    'expires_at': datetime.now() + timedelta(days=3)
                })
            
            if segment in ['growing', 'loyal'] and days_since_visit > 30:
                tags.append({
                    'tag_type': RiskTag.PATTERN_BREAK_DETECTED.value,
                    'tag_value': f'{days_since_visit}일 미방문',
                    'priority': 7,
                    'expires_at': datetime.now() + timedelta(days=14)
                })
            
            if segment == 'vip' and days_since_visit > 21:
                tags.append({
                    'tag_type': RiskTag.VIP_ATTENTION_NEEDED.value,
                    'tag_value': f'VIP 고객 {days_since_visit}일 미방문',
                    'priority': 10,
                    'expires_at': datetime.now() + timedelta(days=7)
                })
            
            if days_since_visit > 60:
                tags.append({
                    'tag_type': RiskTag.REACTIVATION_NEEDED.value,
                    'tag_value': f'{days_since_visit}일 미방문',
                    'priority': 6,
                    'expires_at': datetime.now() + timedelta(days=30)
                })
            
            return tags
            
        except Exception as e:
            self.logger.error(f"Failed to get risk tags for customer {customer_id}: {e}")
            return []
    
    def get_risk_trends(self, days: int = 30) -> Dict:
        """위험 수준 변화 트렌드 분석."""
        try:
            # 최근 기간 동안의 위험 수준 변화
            recent_date = datetime.now() - timedelta(days=days)
            
            query = """
            SELECT 
                DATE(segment_updated_at) as date,
                churn_risk_level,
                COUNT(*) as customer_count,
                AVG(churn_risk_score) as avg_risk_score
            FROM customer_analytics
            WHERE segment_updated_at >= ?
            GROUP BY DATE(segment_updated_at), churn_risk_level
            ORDER BY date DESC, churn_risk_level
            """
            
            result = self.analytics_db.execute(query, [recent_date]).fetchall()
            
            trends = {}
            for row in result:
                date_str = str(row[0])
                risk_level = row[1]
                
                if date_str not in trends:
                    trends[date_str] = {}
                
                trends[date_str][risk_level] = {
                    'customer_count': row[2],
                    'avg_risk_score': float(row[3])
                }
            
            return {
                'trends': trends,
                'analysis_period_days': days
            }
            
        except Exception as e:
            self.logger.error(f"Failed to analyze risk trends: {e}")
            return {}
    
    # Private helper methods
    def _get_customer_analytics(self, customer_id: int) -> Optional[Dict]:
        """고객 분석 데이터 조회."""
        try:
            query = """
            SELECT * FROM customer_analytics WHERE customer_id = ?
            """
            
            result = self.analytics_db.execute(query, [customer_id]).fetchone()
            
            if not result:
                return None
            
            # 컬럼명과 매핑 (실제 테이블 구조에 맞게 조정)
            columns = [
                'customer_id', 'name', 'phone', 'email', 'birth_date', 'gender',
                'first_visit_date', 'last_visit_date', 'total_visits', 'total_amount',
                'avg_visit_amount', 'lifecycle_days', 'days_since_last_visit',
                'visit_frequency', 'preferred_services', 'preferred_employees',
                'visits_3m', 'amount_3m', 'segment', 'segment_updated_at',
                'churn_risk_score', 'churn_risk_level', 'updated_at'
            ]
            
            return dict(zip(columns, result))
            
        except Exception as e:
            self.logger.error(f"Failed to get customer analytics for {customer_id}: {e}")
            return None
    
    def _get_all_customers(self) -> List[Dict]:
        """모든 고객 목록 조회."""
        try:
            query = """
            SELECT customer_id, name, segment FROM customer_analytics
            ORDER BY customer_id
            """
            
            result = self.analytics_db.execute(query).fetchall()
            
            return [
                {'customer_id': row[0], 'name': row[1], 'segment': row[2]}
                for row in result
            ]
            
        except Exception as e:
            self.logger.error(f"Failed to get all customers: {e}")
            return []
    
    def _analyze_risk_factors(self, customer_data: Dict) -> Dict:
        """위험 요인 분석."""
        risk_factors = {}
        
        # 1. 방문 패턴 분석
        days_since_visit = customer_data.get('days_since_last_visit', 0)
        total_visits = customer_data.get('total_visits', 0)
        visit_frequency = customer_data.get('visit_frequency', 0.0)
        
        risk_factors['visit_pattern'] = {
            'days_since_last_visit': days_since_visit,
            'is_overdue': days_since_visit > 30,
            'visit_frequency_decline': visit_frequency < 1.0,
            'total_visits': total_visits
        }
        
        # 2. 세그먼트별 위험 요인
        segment = customer_data.get('segment', 'new')
        risk_factors['segment_risk'] = {
            'segment': segment,
            'is_new_customer_at_risk': segment == 'new' and days_since_visit > 14,
            'is_loyal_customer_at_risk': segment in ['loyal', 'vip'] and days_since_visit > 21,
            'is_growing_customer_stalled': segment == 'growing' and days_since_visit > 30
        }
        
        # 3. 금액 패턴 분석
        total_amount = float(customer_data.get('total_amount', 0))
        avg_visit_amount = float(customer_data.get('avg_visit_amount', 0))
        amount_3m = float(customer_data.get('amount_3m', 0))
        
        risk_factors['spending_pattern'] = {
            'total_amount': total_amount,
            'avg_visit_amount': avg_visit_amount,
            'amount_3m': amount_3m,
            'low_value_customer': total_amount < 100000,
            'declining_spend': amount_3m < (total_amount * 0.3)  # 최근 3개월이 전체의 30% 미만
        }
        
        # 4. 라이프사이클 분석
        lifecycle_days = customer_data.get('lifecycle_days', 0)
        first_visit_date = customer_data.get('first_visit_date')
        
        risk_factors['lifecycle'] = {
            'lifecycle_days': lifecycle_days,
            'is_early_stage': lifecycle_days < 90,
            'is_mature_customer': lifecycle_days > 365,
            'first_visit_date': first_visit_date
        }
        
        return risk_factors
    
    def _calculate_risk_score(self, risk_factors: Dict) -> float:
        """위험 점수 계산 (0-100)."""
        score = 0.0
        
        # 1. 방문 패턴 점수 (40점)
        visit_pattern = risk_factors.get('visit_pattern', {})
        days_since_visit = visit_pattern.get('days_since_last_visit', 0)
        
        if days_since_visit > 90:
            score += 40
        elif days_since_visit > 60:
            score += 30
        elif days_since_visit > 30:
            score += 20
        elif days_since_visit > 14:
            score += 10
        
        # 2. 세그먼트별 위험 점수 (25점)
        segment_risk = risk_factors.get('segment_risk', {})
        if segment_risk.get('is_new_customer_at_risk'):
            score += 20
        if segment_risk.get('is_loyal_customer_at_risk'):
            score += 25
        if segment_risk.get('is_growing_customer_stalled'):
            score += 15
        
        # 3. 지출 패턴 점수 (20점)
        spending_pattern = risk_factors.get('spending_pattern', {})
        if spending_pattern.get('low_value_customer'):
            score += 10
        if spending_pattern.get('declining_spend'):
            score += 10
        
        # 4. 방문 빈도 점수 (15점)
        if visit_pattern.get('visit_frequency_decline'):
            score += 15
        
        return min(score, 100.0)  # 최대 100점
    
    def _determine_risk_level(self, risk_score: float) -> str:
        """위험 점수에 따른 위험 수준 결정."""
        if risk_score >= 80:
            return RiskLevel.CRITICAL
        elif risk_score >= 60:
            return RiskLevel.HIGH
        elif risk_score >= 30:
            return RiskLevel.MEDIUM
        else:
            return RiskLevel.LOW
    
    def _generate_risk_tags(self, customer_data: Dict, risk_factors: Dict, risk_level: str) -> List[str]:
        """위험 태그 생성."""
        tags = []
        
        # 위험 수준별 기본 태그
        if risk_level == RiskLevel.CRITICAL:
            tags.append(RiskTag.CHURN_RISK_HIGH.value)
        elif risk_level == RiskLevel.HIGH:
            tags.append(RiskTag.CHURN_RISK_HIGH.value)
        elif risk_level == RiskLevel.MEDIUM:
            tags.append(RiskTag.CHURN_RISK_MEDIUM.value)
        
        # 세그먼트별 특수 태그
        segment = customer_data.get('segment', 'new')
        days_since_visit = customer_data.get('days_since_last_visit', 0)
        total_visits = customer_data.get('total_visits', 0)
        
        if segment == 'new' and total_visits == 1 and days_since_visit > 7:
            tags.append(RiskTag.FIRST_VISIT_FOLLOW_UP.value)
        
        if segment in ['growing', 'loyal'] and days_since_visit > 30:
            tags.append(RiskTag.PATTERN_BREAK_DETECTED.value)
        
        if segment == 'vip' and days_since_visit > 21:
            tags.append(RiskTag.VIP_ATTENTION_NEEDED.value)
        
        if days_since_visit > 60:
            tags.append(RiskTag.REACTIVATION_NEEDED.value)
        
        return tags
    
    def _generate_recommended_actions(self, customer_data: Dict, risk_factors: Dict, risk_level: str) -> List[str]:
        """추천 액션 생성."""
        actions = []
        
        segment = customer_data.get('segment', 'new')
        days_since_visit = customer_data.get('days_since_last_visit', 0)
        total_visits = customer_data.get('total_visits', 0)
        
        # 위험 수준별 기본 액션
        if risk_level == RiskLevel.CRITICAL:
            actions.append("즉시 개인 연락 및 특별 할인 제공")
            actions.append("매니저 직접 상담 예약")
        elif risk_level == RiskLevel.HIGH:
            actions.append("개인화된 재방문 유도 메시지 발송")
            actions.append("선호 서비스 할인 쿠폰 제공")
        elif risk_level == RiskLevel.MEDIUM:
            actions.append("정기 안부 메시지 발송")
            actions.append("신규 서비스 소개")
        
        # 세그먼트별 맞춤 액션
        if segment == 'new':
            actions.append("신규 고객 웰컴 콜")
            actions.append("첫 방문 후기 문의")
        elif segment == 'vip':
            actions.append("VIP 전용 서비스 안내")
            actions.append("개인 담당자 배정")
        elif segment == 'loyal':
            actions.append("충성 고객 감사 이벤트 초대")
            actions.append("프리미엄 서비스 체험 기회 제공")
        
        # 기간별 액션
        if days_since_visit > 90:
            actions.append("장기 미방문 고객 특별 이벤트 안내")
        elif days_since_visit > 30:
            actions.append("재방문 유도 프로모션 제공")
        
        return actions
    
    def _apply_risk_tags(self, customer_id: int, tags: List[str]) -> int:
        """위험 태그 적용 (실제로는 CRM DB에 저장)."""
        # 실제 구현에서는 CRM 데이터베이스의 customer_tags 테이블에 저장
        # 여기서는 시뮬레이션
        return len(tags)
    
    def _update_customer_risk_data(self, customer_id: int, risk_analysis: Dict):
        """고객 위험 데이터 업데이트."""
        try:
            query = """
            UPDATE customer_analytics 
            SET churn_risk_score = ?, churn_risk_level = ?, updated_at = ?
            WHERE customer_id = ?
            """
            
            self.analytics_db.execute(query, [
                risk_analysis['risk_score'],
                risk_analysis['risk_level'],
                datetime.now(),
                customer_id
            ])
            
        except Exception as e:
            self.logger.error(f"Failed to update risk data for customer {customer_id}: {e}")
    
    def _calculate_urgency_score(self, risk_score: float, days_since_visit: Optional[int]) -> float:
        """긴급도 점수 계산."""
        urgency = risk_score
        
        if days_since_visit:
            # 미방문 일수에 따른 가중치
            if days_since_visit > 90:
                urgency += 20
            elif days_since_visit > 60:
                urgency += 15
            elif days_since_visit > 30:
                urgency += 10
        
        return min(urgency, 100.0)