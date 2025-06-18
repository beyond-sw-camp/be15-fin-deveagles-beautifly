#!/usr/bin/env python3
"""위험 고객 샘플 데이터 생성."""

import sys
import os
sys.path.insert(0, os.path.join(os.path.dirname(__file__), 'src'))

from datetime import datetime, timedelta
import random
from analytics.core.database import get_analytics_db

def create_high_risk_customers():
    """고위험 고객 샘플 데이터 생성."""
    conn = get_analytics_db()
    today = datetime.now()
    
    print("🚨 고위험 고객 샘플 데이터 생성...")
    
    # 고위험 시나리오별 고객 생성
    high_risk_scenarios = [
        {
            'name': '위험고객_장기미방문',
            'segment': 'loyal',
            'total_visits': 15,
            'total_amount': 500000,
            'days_since_last_visit': 120,  # 4개월 미방문
            'visits_3m': 0
        },
        {
            'name': '위험고객_VIP이탈',
            'segment': 'vip',
            'total_visits': 25,
            'total_amount': 1200000,
            'days_since_last_visit': 45,  # 1.5개월 미방문
            'visits_3m': 0
        },
        {
            'name': '위험고객_신규미정착',
            'segment': 'new',
            'total_visits': 1,
            'total_amount': 80000,
            'days_since_last_visit': 25,  # 첫 방문 후 25일 미방문
            'visits_3m': 0
        },
        {
            'name': '위험고객_성장중단',
            'segment': 'growing',
            'total_visits': 8,
            'total_amount': 320000,
            'days_since_last_visit': 60,  # 2개월 미방문
            'visits_3m': 0
        },
        {
            'name': '위험고객_패턴변화',
            'segment': 'loyal',
            'total_visits': 20,
            'total_amount': 600000,
            'days_since_last_visit': 90,  # 3개월 미방문
            'visits_3m': 1  # 최근 3개월에 1회만
        }
    ]
    
    # 기존 고위험 고객 데이터 삭제
    conn.execute("DELETE FROM customer_analytics WHERE name LIKE '위험고객_%'")
    
    for i, scenario in enumerate(high_risk_scenarios, start=100):
        customer_id = i
        
        # 방문 날짜 계산
        days_since_visit = scenario['days_since_last_visit']
        last_visit_date = today - timedelta(days=days_since_visit)
        
        # 첫 방문 날짜 (세그먼트에 따라 다르게)
        if scenario['segment'] == 'new':
            first_visit_date = last_visit_date
        elif scenario['segment'] == 'growing':
            first_visit_date = today - timedelta(days=random.randint(90, 180))
        elif scenario['segment'] == 'loyal':
            first_visit_date = today - timedelta(days=random.randint(300, 600))
        else:  # vip
            first_visit_date = today - timedelta(days=random.randint(400, 800))
        
        lifecycle_days = (today - first_visit_date).days
        avg_visit_amount = scenario['total_amount'] / scenario['total_visits']
        visit_frequency = scenario['total_visits'] / max(1, lifecycle_days) * 30
        
        # 고객 데이터 삽입
        conn.execute("""
            INSERT OR REPLACE INTO customer_analytics 
            (customer_id, name, phone, email, birth_date, gender,
             first_visit_date, last_visit_date, total_visits, total_amount, avg_visit_amount,
             lifecycle_days, days_since_last_visit, visit_frequency, preferred_services,
             visits_3m, amount_3m, segment, segment_updated_at, churn_risk_score, churn_risk_level,
             updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """, (
            customer_id,
            scenario['name'],
            f'010-9000-{customer_id:04d}',
            f'risk{customer_id}@example.com',
            '1985-05-15',
            'F',
            first_visit_date,
            last_visit_date,
            scenario['total_visits'],
            scenario['total_amount'],
            avg_visit_amount,
            lifecycle_days,
            days_since_visit,
            visit_frequency,
            '[]',  # preferred_services
            scenario['visits_3m'],
            scenario['visits_3m'] * avg_visit_amount,
            scenario['segment'],
            today,
            0.0,  # 초기 위험 점수
            'low',  # 초기 위험 수준
            today
        ))
        
        print(f"✓ {scenario['name']} (ID: {customer_id}) 생성완료")
        print(f"  - 세그먼트: {scenario['segment']}")
        print(f"  - 총 방문: {scenario['total_visits']}회")
        print(f"  - 총 금액: {scenario['total_amount']:,}원")
        print(f"  - 미방문: {days_since_visit}일")
    
    print(f"\n✅ 총 {len(high_risk_scenarios)}명의 고위험 고객 샘플 생성 완료!")

def create_medium_risk_customers():
    """중위험 고객 샘플 데이터 생성."""
    conn = get_analytics_db()
    today = datetime.now()
    
    print("\n⚠️ 중위험 고객 샘플 데이터 생성...")
    
    medium_risk_scenarios = [
        {
            'name': '중위험고객_방문감소',
            'segment': 'growing',
            'total_visits': 6,
            'total_amount': 240000,
            'days_since_last_visit': 35,  # 1개월+ 미방문
            'visits_3m': 1
        },
        {
            'name': '중위험고객_충성도하락',
            'segment': 'loyal',
            'total_visits': 12,
            'total_amount': 400000,
            'days_since_last_visit': 40,  # 1개월+ 미방문
            'visits_3m': 2
        },
        {
            'name': '중위험고객_신규지연',
            'segment': 'new',
            'total_visits': 2,
            'total_amount': 120000,
            'days_since_last_visit': 18,  # 신규 고객 18일 미방문
            'visits_3m': 1
        }
    ]
    
    for i, scenario in enumerate(medium_risk_scenarios, start=200):
        customer_id = i
        
        days_since_visit = scenario['days_since_last_visit']
        last_visit_date = today - timedelta(days=days_since_visit)
        
        if scenario['segment'] == 'new':
            first_visit_date = today - timedelta(days=random.randint(20, 40))
        elif scenario['segment'] == 'growing':
            first_visit_date = today - timedelta(days=random.randint(60, 120))
        else:  # loyal
            first_visit_date = today - timedelta(days=random.randint(200, 400))
        
        lifecycle_days = (today - first_visit_date).days
        avg_visit_amount = scenario['total_amount'] / scenario['total_visits']
        visit_frequency = scenario['total_visits'] / max(1, lifecycle_days) * 30
        
        conn.execute("""
            INSERT OR REPLACE INTO customer_analytics 
            (customer_id, name, phone, email, birth_date, gender,
             first_visit_date, last_visit_date, total_visits, total_amount, avg_visit_amount,
             lifecycle_days, days_since_last_visit, visit_frequency, preferred_services,
             visits_3m, amount_3m, segment, segment_updated_at, churn_risk_score, churn_risk_level,
             updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """, (
            customer_id,
            scenario['name'],
            f'010-8000-{customer_id:04d}',
            f'medium{customer_id}@example.com',
            '1990-03-20',
            'M',
            first_visit_date,
            last_visit_date,
            scenario['total_visits'],
            scenario['total_amount'],
            avg_visit_amount,
            lifecycle_days,
            days_since_visit,
            visit_frequency,
            '[]',
            scenario['visits_3m'],
            scenario['visits_3m'] * avg_visit_amount,
            scenario['segment'],
            today,
            0.0,
            'low',
            today
        ))
        
        print(f"✓ {scenario['name']} (ID: {customer_id}) 생성완료")
    
    print(f"\n✅ 총 {len(medium_risk_scenarios)}명의 중위험 고객 샘플 생성 완료!")

if __name__ == "__main__":
    create_high_risk_customers()
    create_medium_risk_customers()
    print("\n🎯 모든 위험 고객 샘플 데이터 생성 완료!") 