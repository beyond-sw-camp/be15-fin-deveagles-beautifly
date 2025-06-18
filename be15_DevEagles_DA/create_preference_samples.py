#!/usr/bin/env python3
"""선호 시술 샘플 데이터 생성 스크립트."""

import sys
import os
sys.path.insert(0, os.path.join(os.path.dirname(__file__), 'src'))

from datetime import datetime, timedelta
import random
from analytics.core.database import get_analytics_db

def create_preference_samples():
    """선호 시술 샘플 데이터 생성."""
    
    print("🎨 선호 시술 샘플 데이터 생성 시작...")
    
    try:
        conn = get_analytics_db()
        
        # 샘플 서비스 목록
        services = [
            {"id": 1, "name": "컷", "category": "헤어"},
            {"id": 2, "name": "펌", "category": "헤어"},
            {"id": 3, "name": "염색", "category": "헤어"},
            {"id": 4, "name": "트리트먼트", "category": "헤어"},
            {"id": 5, "name": "페이셜", "category": "스킨케어"},
            {"id": 6, "name": "마사지", "category": "스킨케어"},
            {"id": 7, "name": "네일아트", "category": "네일"},
            {"id": 8, "name": "젤네일", "category": "네일"},
            {"id": 9, "name": "속눈썹연장", "category": "아이케어"},
            {"id": 10, "name": "눈썹정리", "category": "아이케어"}
        ]
        
        # 고객별 선호 시술 데이터 생성 (고객 1-10)
        for customer_id in range(1, 11):
            # 각 고객마다 3-6개의 서비스 이용 기록 생성
            customer_services = random.sample(services, random.randint(3, 6))
            
            total_customer_visits = 0
            total_customer_amount = 0
            preferences = []
            
            for rank, service in enumerate(customer_services, 1):
                visits = random.randint(1, 10)
                amount_per_visit = random.randint(30000, 150000)
                total_amount = visits * amount_per_visit
                
                total_customer_visits += visits
                total_customer_amount += total_amount
                
                preferences.append({
                    'customer_id': customer_id,
                    'service_id': service['id'],
                    'service_name': service['name'],
                    'service_category': service['category'],
                    'total_visits': visits,
                    'total_amount': total_amount,
                    'avg_amount': amount_per_visit,
                    'first_service_date': datetime.now() - timedelta(days=random.randint(100, 300)),
                    'last_service_date': datetime.now() - timedelta(days=random.randint(1, 30)),
                    'preference_rank': rank,
                    'recent_visits_3m': random.randint(0, visits),
                    'days_since_last_service': random.randint(1, 30)
                })
            
            # 비율 계산 및 데이터 삽입
            for pref in preferences:
                pref['visit_ratio'] = pref['total_visits'] / total_customer_visits
                pref['amount_ratio'] = pref['total_amount'] / total_customer_amount
                
                conn.execute("""
                    INSERT OR REPLACE INTO customer_service_preferences 
                    (customer_id, service_id, service_name, service_category, 
                     total_visits, total_amount, avg_amount, first_service_date, 
                     last_service_date, preference_rank, visit_ratio, amount_ratio,
                     recent_visits_3m, days_since_last_service)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """, (
                    pref['customer_id'], pref['service_id'], pref['service_name'],
                    pref['service_category'], pref['total_visits'], pref['total_amount'],
                    pref['avg_amount'], pref['first_service_date'], pref['last_service_date'],
                    pref['preference_rank'], pref['visit_ratio'], pref['amount_ratio'],
                    pref['recent_visits_3m'], pref['days_since_last_service']
                ))
            
            # 고객 서비스 태그 생성
            top_services = sorted(preferences, key=lambda x: x['preference_rank'])[:3]
            categories = list(set([p['service_category'] for p in preferences]))
            avg_price = sum([p['avg_amount'] for p in preferences]) / len(preferences)
            
            price_range = "low" if avg_price < 50000 else "high" if avg_price > 100000 else "medium"
            variety_score = len(preferences) / 10.0  # 최대 10개 서비스 기준
            
            conn.execute("""
                INSERT OR REPLACE INTO customer_service_tags 
                (customer_id, top_service_1, top_service_2, top_service_3,
                 preferred_categories, service_variety_score, avg_service_price, preferred_price_range)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """, (
                customer_id,
                top_services[0]['service_name'] if len(top_services) > 0 else None,
                top_services[1]['service_name'] if len(top_services) > 1 else None,
                top_services[2]['service_name'] if len(top_services) > 2 else None,
                str(categories),  # 리스트를 문자열로 저장
                variety_score,
                avg_price,
                price_range
            ))
        
        print("✅ 10명 고객의 선호 시술 샘플 데이터 생성 완료")
        
    except Exception as e:
        print(f"❌ 샘플 데이터 생성 실패: {e}")
        import traceback
        traceback.print_exc()

if __name__ == "__main__":
    create_preference_samples() 