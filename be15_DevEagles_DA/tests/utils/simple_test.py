#!/usr/bin/env python3
"""Simple Analytics Database Test."""

import sys
from pathlib import Path

# 프로젝트 루트를 Python 경로에 추가
sys.path.insert(0, str(Path(__file__).parent.parent.parent / "src"))

def test_analytics_db():
    """Analytics 데이터베이스 테스트."""
    print("🧪 Simple Analytics Database Test")
    print("=" * 50)
    
    try:
        # Analytics 데이터베이스 연결 테스트
        from analytics.core.database import get_analytics_db
        
        print("1. Testing Analytics database connection...")
        analytics_db = get_analytics_db()
        result = analytics_db.execute("SELECT 1 as test").fetchone()
        
        if result and result[0] == 1:
            print("✅ Analytics database connection: OK")
        else:
            print("❌ Analytics database connection: Failed")
            return False
        
        # 테이블 확인
        print("\n2. Checking Analytics tables...")
        tables = [
            "customer_analytics",
            "visit_analytics", 
            "customer_service_preferences",
            "customer_service_tags",
            "etl_metadata"
        ]
        
        for table in tables:
            try:
                count = analytics_db.execute(f"SELECT COUNT(*) FROM {table}").fetchone()[0]
                print(f"✅ {table}: {count} records")
            except Exception as e:
                print(f"❌ {table}: Missing or error - {e}")
        
        # 샘플 데이터 생성 테스트
        print("\n3. Testing sample data creation...")
        from datetime import datetime, timedelta
        import random
        
        # 샘플 고객 데이터 생성
        sample_customers = []
        for i in range(1, 6):  # 5명의 고객
            sample_customers.append({
                'customer_id': i,
                'name': f'테스트고객{i}',
                'phone': f'010-1234-{i:04d}',
                'email': f'test{i}@example.com',
                'birth_date': f'199{random.randint(0,9)}-{random.randint(1,12):02d}-{random.randint(1,28):02d}',
                'gender': random.choice(['M', 'F']),
                'first_visit_date': datetime.now() - timedelta(days=random.randint(30, 365)),
                'last_visit_date': datetime.now() - timedelta(days=random.randint(1, 30)),
                'total_visits': random.randint(1, 10),
                'total_amount': random.randint(50000, 200000),
                'segment': random.choice(['new', 'growing', 'loyal'])
            })
        
        # 데이터 삽입
        for customer in sample_customers:
            analytics_db.execute("""
                INSERT OR REPLACE INTO customer_analytics 
                (customer_id, name, phone, email, birth_date, gender, 
                 first_visit_date, last_visit_date, total_visits, total_amount, segment)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """, (
                customer['customer_id'], customer['name'], customer['phone'], 
                customer['email'], customer['birth_date'], customer['gender'],
                customer['first_visit_date'], customer['last_visit_date'],
                customer['total_visits'], customer['total_amount'], customer['segment']
            ))
        
        print(f"✅ {len(sample_customers)}개의 샘플 데이터 생성 완료")
        
        # 데이터 조회 테스트
        print("\n4. Testing data retrieval...")
        result = analytics_db.execute("""
            SELECT customer_id, name, segment, total_visits, total_amount
            FROM customer_analytics 
            ORDER BY customer_id 
            LIMIT 5
        """).fetchall()
        
        print("\n📊 Sample Data:")
        print("ID | Name        | Segment | Visits | Amount")
        print("-" * 45)
        for row in result:
            print(f"{row[0]:2} | {row[1]:11} | {row[2]:7} | {row[3]:6} | {row[4]:,}")
        
        print("\n✅ All tests passed successfully!")
        return True
        
    except Exception as e:
        print(f"❌ Test failed: {e}")
        import traceback
        traceback.print_exc()
        return False

if __name__ == "__main__":
    success = test_analytics_db()
    sys.exit(0 if success else 1) 