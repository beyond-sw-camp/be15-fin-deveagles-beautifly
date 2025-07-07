#!/usr/bin/env python3
"""Verify Customer Segmentation Results."""

import sys
from pathlib import Path

# 프로젝트 루트를 Python 경로에 추가
sys.path.insert(0, str(Path(__file__).parent.parent.parent / "src"))

def verify_segmentation():
    """세그멘테이션 결과 검증."""
    from analytics.services.segmentation import CustomerSegmentationService
    
    service = CustomerSegmentationService()
    
    print("🎯 Customer Segmentation Verification")
    print("=" * 50)
    
    # 1. 세그먼트 분포
    print("\n📊 Current Segment Distribution:")
    distribution = service.get_segment_distribution()
    
    for segment, stats in distribution.items():
        print(f"  {segment.upper()}: {stats['count']} customers")
        print(f"    - 평균 방문: {stats['avg_visits']}회")
        print(f"    - 평균 금액: {stats['avg_amount']:,.0f}원")
        print(f"    - 평균 미방문일: {stats['avg_days_since_visit']}일")
        print()
    
    # 2. VIP 고객 인사이트
    print("🌟 VIP 고객 상세 분석:")
    vip_insights = service.get_segment_insights('vip')
    
    if "error" not in vip_insights:
        print(f"  총 VIP 고객: {vip_insights['total_customers']}명")
        print(f"  평균 방문 횟수: {vip_insights['avg_visits']}회")
        print(f"  평균 결제 금액: {vip_insights['avg_amount']:,.0f}원")
        print(f"  금액 범위: {vip_insights['amount_range']['min']:,.0f}원 ~ {vip_insights['amount_range']['max']:,.0f}원")
        
        print("\n  💡 VIP 고객 마케팅 전략:")
        for i, action in enumerate(vip_insights['recommended_actions'], 1):
            print(f"    {i}. {action}")
    
    # 3. 이탈 위험 고객 분석
    print("\n⚠️ 이탈 위험 고객 분석:")
    at_risk_insights = service.get_segment_insights('at_risk')
    
    if "error" not in at_risk_insights:
        print(f"  이탈 위험 고객: {at_risk_insights['total_customers']}명")
        print(f"  평균 미방문일: {at_risk_insights['avg_days_since_visit']}일")
        print(f"  평균 과거 결제 금액: {at_risk_insights['avg_amount']:,.0f}원")
        
        print("\n  🚨 이탈 방지 액션:")
        for i, action in enumerate(at_risk_insights['recommended_actions'], 1):
            print(f"    {i}. {action}")
    
    print("\n✅ 세그멘테이션 검증 완료!")

if __name__ == "__main__":
    verify_segmentation() 