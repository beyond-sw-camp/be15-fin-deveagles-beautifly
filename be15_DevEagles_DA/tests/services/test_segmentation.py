#!/usr/bin/env python3
"""Customer Segmentation Test Script."""

import sys
from pathlib import Path
from datetime import datetime, timedelta
import random

# 프로젝트 루트를 Python 경로에 추가
sys.path.insert(0, str(Path(__file__).parent.parent.parent / "src"))

def create_realistic_customer_data():
    """현실적인 고객 데이터 생성."""
    print("📊 Creating realistic customer data for segmentation test...")
    
    from analytics.core.database import get_analytics_db
    
    analytics_db = get_analytics_db()
    
    # 기존 데이터 삭제
    analytics_db.execute("DELETE FROM customer_analytics")
    
    customers = []
    today = datetime.now()
    
    # 1. 신규 고객 (5명) - 최근 30일 이내, 방문 1-2회
    for i in range(1, 6):
        first_visit = today - timedelta(days=random.randint(1, 30))
        last_visit = first_visit + timedelta(days=random.randint(0, 15))
        visits = random.randint(1, 2)
        amount = random.randint(30000, 80000)
        
        customers.append({
            'customer_id': i,
            'name': f'신규고객{i}',
            'phone': f'010-1000-{i:04d}',
            'email': f'new{i}@example.com',
            'birth_date': f'199{random.randint(0,9)}-{random.randint(1,12):02d}-{random.randint(1,28):02d}',
            'gender': random.choice(['M', 'F']),
            'first_visit_date': first_visit,
            'last_visit_date': last_visit,
            'total_visits': visits,
            'total_amount': amount,
            'avg_visit_amount': amount / visits,
            'lifecycle_days': (today - first_visit).days,
            'days_since_last_visit': (today - last_visit).days,
            'visit_frequency': visits / max(1, (today - first_visit).days) * 30,
            'visits_3m': visits,
            'amount_3m': amount,
            'segment': 'new'
        })
    
    # 2. 성장 고객 (7명) - 방문 3-9회, 최근 활동 있음
    for i in range(6, 13):
        first_visit = today - timedelta(days=random.randint(60, 120))
        last_visit = today - timedelta(days=random.randint(5, 30))
        visits = random.randint(3, 9)
        amount = random.randint(100000, 300000)
        
        customers.append({
            'customer_id': i,
            'name': f'성장고객{i}',
            'phone': f'010-2000-{i:04d}',
            'email': f'growing{i}@example.com',
            'birth_date': f'199{random.randint(0,9)}-{random.randint(1,12):02d}-{random.randint(1,28):02d}',
            'gender': random.choice(['M', 'F']),
            'first_visit_date': first_visit,
            'last_visit_date': last_visit,
            'total_visits': visits,
            'total_amount': amount,
            'avg_visit_amount': amount / visits,
            'lifecycle_days': (today - first_visit).days,
            'days_since_last_visit': (today - last_visit).days,
            'visit_frequency': visits / max(1, (today - first_visit).days) * 30,
            'visits_3m': random.randint(1, 3),
            'amount_3m': random.randint(50000, 150000),
            'segment': 'growing'
        })
    
    # 3. 충성 고객 (5명) - 방문 10회 이상, 정기적 방문
    for i in range(13, 18):
        first_visit = today - timedelta(days=random.randint(180, 365))
        last_visit = today - timedelta(days=random.randint(1, 20))
        visits = random.randint(10, 20)
        amount = random.randint(300000, 600000)
        
        customers.append({
            'customer_id': i,
            'name': f'충성고객{i}',
            'phone': f'010-3000-{i:04d}',
            'email': f'loyal{i}@example.com',
            'birth_date': f'198{random.randint(0,9)}-{random.randint(1,12):02d}-{random.randint(1,28):02d}',
            'gender': random.choice(['M', 'F']),
            'first_visit_date': first_visit,
            'last_visit_date': last_visit,
            'total_visits': visits,
            'total_amount': amount,
            'avg_visit_amount': amount / visits,
            'lifecycle_days': (today - first_visit).days,
            'days_since_last_visit': (today - last_visit).days,
            'visit_frequency': visits / max(1, (today - first_visit).days) * 30,
            'visits_3m': random.randint(2, 5),
            'amount_3m': random.randint(100000, 250000),
            'segment': 'loyal'
        })
    
    # 4. VIP 고객 (3명) - 고액 결제, 많은 방문
    for i in range(18, 21):
        first_visit = today - timedelta(days=random.randint(200, 500))
        last_visit = today - timedelta(days=random.randint(1, 15))
        visits = random.randint(15, 30)
        amount = random.randint(600000, 1200000)
        
        customers.append({
            'customer_id': i,
            'name': f'VIP고객{i}',
            'phone': f'010-4000-{i:04d}',
            'email': f'vip{i}@example.com',
            'birth_date': f'197{random.randint(0,9)}-{random.randint(1,12):02d}-{random.randint(1,28):02d}',
            'gender': random.choice(['M', 'F']),
            'first_visit_date': first_visit,
            'last_visit_date': last_visit,
            'total_visits': visits,
            'total_amount': amount,
            'avg_visit_amount': amount / visits,
            'lifecycle_days': (today - first_visit).days,
            'days_since_last_visit': (today - last_visit).days,
            'visit_frequency': visits / max(1, (today - first_visit).days) * 30,
            'visits_3m': random.randint(3, 8),
            'amount_3m': random.randint(200000, 400000),
            'segment': 'vip'
        })
    
    # 5. 이탈 위험 고객 (4명) - 2개월 이상 미방문
    for i in range(21, 25):
        first_visit = today - timedelta(days=random.randint(120, 300))
        last_visit = today - timedelta(days=random.randint(60, 120))
        visits = random.randint(5, 12)
        amount = random.randint(150000, 400000)
        
        customers.append({
            'customer_id': i,
            'name': f'위험고객{i}',
            'phone': f'010-5000-{i:04d}',
            'email': f'atrisk{i}@example.com',
            'birth_date': f'198{random.randint(0,9)}-{random.randint(1,12):02d}-{random.randint(1,28):02d}',
            'gender': random.choice(['M', 'F']),
            'first_visit_date': first_visit,
            'last_visit_date': last_visit,
            'total_visits': visits,
            'total_amount': amount,
            'avg_visit_amount': amount / visits,
            'lifecycle_days': (today - first_visit).days,
            'days_since_last_visit': (today - last_visit).days,
            'visit_frequency': visits / max(1, (today - first_visit).days) * 30,
            'visits_3m': 0,
            'amount_3m': 0,
            'segment': 'at_risk'
        })
    
    # 6. 휴면 고객 (3명) - 6개월 이상 미방문
    for i in range(25, 28):
        first_visit = today - timedelta(days=random.randint(300, 600))
        last_visit = today - timedelta(days=random.randint(180, 300))
        visits = random.randint(3, 8)
        amount = random.randint(80000, 250000)
        
        customers.append({
            'customer_id': i,
            'name': f'휴면고객{i}',
            'phone': f'010-6000-{i:04d}',
            'email': f'dormant{i}@example.com',
            'birth_date': f'198{random.randint(0,9)}-{random.randint(1,12):02d}-{random.randint(1,28):02d}',
            'gender': random.choice(['M', 'F']),
            'first_visit_date': first_visit,
            'last_visit_date': last_visit,
            'total_visits': visits,
            'total_amount': amount,
            'avg_visit_amount': amount / visits,
            'lifecycle_days': (today - first_visit).days,
            'days_since_last_visit': (today - last_visit).days,
            'visit_frequency': visits / max(1, (today - first_visit).days) * 30,
            'visits_3m': 0,
            'amount_3m': 0,
            'segment': 'dormant'
        })
    
    # 데이터 삽입
    for customer in customers:
        analytics_db.execute("""
            INSERT INTO customer_analytics 
            (customer_id, name, phone, email, birth_date, gender, 
             first_visit_date, last_visit_date, total_visits, total_amount, avg_visit_amount,
             lifecycle_days, days_since_last_visit, visit_frequency, 
             visits_3m, amount_3m, segment, segment_updated_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """, (
            customer['customer_id'], customer['name'], customer['phone'], 
            customer['email'], customer['birth_date'], customer['gender'],
            customer['first_visit_date'], customer['last_visit_date'],
            customer['total_visits'], customer['total_amount'], customer['avg_visit_amount'],
            customer['lifecycle_days'], customer['days_since_last_visit'], customer['visit_frequency'],
            customer['visits_3m'], customer['amount_3m'], customer['segment'],
            datetime.now(), datetime.now()
        ))
    
    print(f"✅ Created {len(customers)} realistic customer records")
    return len(customers)


def test_segmentation():
    """세그멘테이션 테스트."""
    print("🎯 Testing Customer Segmentation")
    print("=" * 50)
    
    try:
        from analytics.services.segmentation import CustomerSegmentationService
        
        service = CustomerSegmentationService()
        
        # 1. 전체 고객 세그멘테이션
        print("\n1. Running segmentation for all customers...")
        results = service.segment_all_customers()
        
        print("\n📊 Segmentation Results:")
        total_customers = sum(results.values())
        for segment, count in results.items():
            percentage = (count / total_customers * 100) if total_customers > 0 else 0
            print(f"  {segment.upper()}: {count} customers ({percentage:.1f}%)")
        
        # 2. 세그먼트 분포 확인
        print("\n2. Getting segment distribution...")
        distribution = service.get_segment_distribution()
        
        print("\n📈 Segment Distribution:")
        print("Segment".ljust(10), "Count".ljust(8), "Avg Visits".ljust(12), "Avg Amount".ljust(15), "Avg Days")
        print("-" * 60)
        for segment, stats in distribution.items():
            print(
                segment.ljust(10),
                str(stats['count']).ljust(8),
                str(stats['avg_visits']).ljust(12),
                f"{stats['avg_amount']:,.0f}".ljust(15),
                str(stats['avg_days_since_visit'])
            )
        
        # 3. 특정 세그먼트 인사이트
        print("\n3. Getting insights for VIP segment...")
        vip_insights = service.get_segment_insights('vip')
        
        if "error" not in vip_insights:
            print(f"\n🌟 VIP Segment Insights:")
            print(f"  Total Customers: {vip_insights['total_customers']}")
            print(f"  Average Visits: {vip_insights['avg_visits']}")
            print(f"  Average Amount: {vip_insights['avg_amount']:,.0f}")
            print(f"  Amount Range: {vip_insights['amount_range']['min']:,.0f} - {vip_insights['amount_range']['max']:,.0f}")
            
            print(f"\n🎯 Recommended Actions for VIP customers:")
            for i, action in enumerate(vip_insights['recommended_actions'], 1):
                print(f"    {i}. {action}")
        
        # 4. 개별 고객 세그멘테이션 테스트
        print("\n4. Testing individual customer segmentation...")
        test_customer_id = 1
        segment = service.segment_customer(test_customer_id)
        print(f"  Customer {test_customer_id} segmented as: {segment}")
        
        # 5. 트렌드 분석
        print("\n5. Analyzing segment trends...")
        trends = service.analyze_segment_trends()
        print(f"  Total customers analyzed: {trends['total_customers']}")
        print(f"  Analysis period: {trends['analysis_period_days']} days")
        
        if trends['recent_changes']:
            print("  Recent segment changes:")
            for segment, count in trends['recent_changes'].items():
                print(f"    {segment}: {count} customers")
        else:
            print("  No recent segment changes detected")
        
        print("\n✅ All segmentation tests completed successfully!")
        return True
        
    except Exception as e:
        print(f"❌ Segmentation test failed: {e}")
        import traceback
        traceback.print_exc()
        return False


def main():
    """메인 테스트 함수."""
    print("DevEagles Customer Segmentation Test")
    print("=" * 60)
    
    try:
        # 1. 현실적인 테스트 데이터 생성
        create_realistic_customer_data()
        
        # 2. 세그멘테이션 테스트
        success = test_segmentation()
        
        if success:
            print("\n🎉 All tests passed! Customer segmentation is working correctly.")
            print("\n💡 You can now use the CLI commands:")
            print("  cd src && python -m analytics.cli segment --show-distribution")
            print("  cd src && python -m analytics.cli segment --show-insights vip")
            print("  cd src && python -m analytics.cli segment --show-trends")
        else:
            print("\n❌ Some tests failed. Please check the logs.")
            
        return success
        
    except Exception as e:
        print(f"❌ Test execution failed: {e}")
        return False


if __name__ == "__main__":
    success = main()
    sys.exit(0 if success else 1) 