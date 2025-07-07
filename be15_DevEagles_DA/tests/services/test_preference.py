#!/usr/bin/env python3
"""고객 선호 시술 분석 테스트 스크립트."""

import sys
import os
sys.path.insert(0, os.path.join(os.path.dirname(__file__), '..', '..', 'src'))

from analytics.services.preference import CustomerServicePreferenceService
from analytics.core.database import get_analytics_db
from rich.console import Console
from rich.table import Table

console = Console()

def test_preference_service():
    """선호 시술 분석 서비스 테스트."""
    
    console.print("🎨 고객 선호 시술 분석 서비스 테스트", style="bold blue")
    
    try:
        # 서비스 초기화
        service = CustomerServicePreferenceService()
        console.print("✅ 서비스 초기화 완료")
        
        # 테스트 1: 특정 고객 선호도 조회
        console.print("\n📊 테스트 1: 고객 1의 선호 시술 조회")
        preferences = service.get_customer_preferences(customer_id=1)
        
        if preferences:
            table = Table(title="고객 1 선호 시술")
            table.add_column("순위", style="cyan")
            table.add_column("서비스명", style="green")
            table.add_column("카테고리", style="yellow")
            table.add_column("방문횟수", style="magenta")
            table.add_column("총금액", style="red")
            table.add_column("방문비율", style="blue")
            
            for pref in preferences:
                table.add_row(
                    str(pref['preference_rank']),
                    pref['service_name'],
                    pref['service_category'],
                    str(pref['total_visits']),
                    f"{pref['total_amount']:,.0f}원",
                    f"{pref['visit_ratio']:.1%}"
                )
            
            console.print(table)
            console.print(f"✅ 고객 1의 선호 시술 {len(preferences)}개 조회 완료")
        else:
            console.print("❌ 고객 1의 선호 시술 데이터 없음")
        
        # 테스트 2: 고객 서비스 태그 조회
        console.print("\n🏷️ 테스트 2: 고객 1의 서비스 태그 조회")
        tags = service.get_customer_service_tags(customer_id=1)
        
        if tags:
            console.print(f"• 선호 서비스: {tags['top_service_1']}, {tags['top_service_2']}, {tags['top_service_3']}")
            console.print(f"• 선호 카테고리: {', '.join(tags['preferred_categories'])}")
            console.print(f"• 다양성 점수: {tags['service_variety_score']:.2f}")
            console.print(f"• 평균 가격대: {tags['avg_service_price']:,.0f}원 ({tags['preferred_price_range']})")
            console.print(f"• 모든 태그: {', '.join(tags['all_preference_tags'])}")
            console.print("✅ 고객 1의 서비스 태그 조회 완료")
        else:
            console.print("❌ 고객 1의 서비스 태그 없음")
        
        # 테스트 3: 서비스 인기도 순위
        console.print("\n🏆 테스트 3: 서비스 인기도 순위 (상위 10개)")
        rankings = service.get_service_popularity_ranking(limit=10)
        
        if rankings:
            rank_table = Table(title="서비스 인기도 순위")
            rank_table.add_column("순위", style="cyan")
            rank_table.add_column("서비스명", style="green")
            rank_table.add_column("카테고리", style="yellow")
            rank_table.add_column("고객수", style="magenta")
            rank_table.add_column("총매출", style="red")
            rank_table.add_column("인기점수", style="blue")
            
            for rank in rankings:
                rank_table.add_row(
                    str(rank['rank']),
                    rank['service_name'],
                    rank['service_category'],
                    str(rank['customer_count']),
                    f"{rank['total_revenue']:,.0f}원",
                    f"{rank['popularity_score']:.1f}"
                )
            
            console.print(rank_table)
            console.print(f"✅ 서비스 인기도 순위 {len(rankings)}개 조회 완료")
        else:
            console.print("❌ 서비스 인기도 데이터 없음")
        
        # 테스트 4: 카테고리별 분포
        console.print("\n📊 테스트 4: 카테고리별 선호도 분포")
        distribution = service.get_category_preferences_distribution()
        
        if distribution and 'categories' in distribution and distribution['categories']:
            cat_table = Table(title="카테고리별 분포")
            cat_table.add_column("카테고리", style="green")
            cat_table.add_column("고객수", style="cyan")
            cat_table.add_column("고객비율", style="yellow")
            cat_table.add_column("총매출", style="red")
            cat_table.add_column("매출비율", style="blue")
            
            for category, data in distribution['categories'].items():
                cat_table.add_row(
                    category,
                    str(data['customer_count']),
                    f"{data['customer_ratio']:.1%}",
                    f"{data['total_revenue']:,.0f}원",
                    f"{data['revenue_ratio']:.1%}"
                )
            
            console.print(cat_table)
            console.print(f"✅ 카테고리별 분포 {len(distribution['categories'])}개 조회 완료")
            console.print(f"📈 총 고객: {distribution['total_customers']}명, 총 매출: {distribution['total_revenue']:,.0f}원")
        else:
            console.print("❌ 카테고리별 분포 데이터 없음")
        
        # 테스트 5: 유사한 고객 찾기
        console.print("\n👥 테스트 5: 고객 1과 유사한 고객 찾기")
        similar = service.find_similar_customers(customer_id=1, limit=5)
        
        if similar:
            sim_table = Table(title="유사한 고객")
            sim_table.add_column("고객ID", style="cyan")
            sim_table.add_column("공통서비스수", style="green")
            sim_table.add_column("유사도점수", style="red")
            sim_table.add_column("공통서비스", style="yellow")
            
            for sim in similar:
                sim_table.add_row(
                    str(sim['customer_id']),
                    str(sim['common_services_count']),
                    f"{sim['similarity_score']:.2f}",
                    ', '.join(sim['common_services'][:3])
                )
            
            console.print(sim_table)
            console.print(f"✅ 유사한 고객 {len(similar)}명 조회 완료")
        else:
            console.print("❌ 유사한 고객 없음")
        
        # 테스트 6: 서비스 추천
        console.print("\n💡 테스트 6: 고객 1 추천 서비스")
        recommendations = service.get_service_recommendations(customer_id=1, limit=5)
        
        if recommendations:
            rec_table = Table(title="추천 서비스")
            rec_table.add_column("서비스명", style="green")
            rec_table.add_column("카테고리", style="yellow")
            rec_table.add_column("추천점수", style="red")
            rec_table.add_column("이유", style="cyan")
            
            for rec in recommendations:
                rec_table.add_row(
                    rec['service_name'],
                    rec['service_category'],
                    f"{rec['recommendation_score']:.2f}",
                    rec['reason']
                )
            
            console.print(rec_table)
            console.print(f"✅ 추천 서비스 {len(recommendations)}개 조회 완료")
        else:
            console.print("❌ 추천 서비스 없음")
        
        # 테스트 7: 고객 여정 분석
        console.print("\n🛣️ 테스트 7: 고객 1 서비스 여정 분석")
        journey = service.get_customer_journey_analysis(customer_id=1)
        
        if journey:
            console.print(f"• 총 시도한 서비스: {journey['total_services_tried']}개")
            console.print(f"• 선호도 진화: {journey['preference_evolution']['description']}")
            console.print(f"• 충성도 패턴: {journey['loyalty_pattern']['description']} (점수: {journey['loyalty_pattern']['score']:.2f})")
            console.print(f"• 지출 패턴: {journey['spending_pattern']['description']}")
            console.print("✅ 고객 여정 분석 완료")
        else:
            console.print("❌ 고객 여정 데이터 없음")
        
        # 테스트 8: 서비스 트렌드 분석
        console.print("\n📈 테스트 8: 서비스 트렌드 분석 (최근 30일)")
        trends = service.analyze_service_trends(days=30)
        
        if trends and 'trends' in trends and trends['trends']:
            trend_table = Table(title="서비스 트렌드")
            trend_table.add_column("서비스명", style="green")
            trend_table.add_column("현재고객", style="cyan")
            trend_table.add_column("활성고객", style="magenta")
            trend_table.add_column("활성률", style="red")
            trend_table.add_column("트렌드점수", style="blue")
            
            for trend in trends['trends'][:10]:  # 상위 10개만
                trend_table.add_row(
                    trend['service_name'],
                    str(trend['current_customers']),
                    str(trend['active_customers']),
                    f"{trend['activity_rate']:.1%}",
                    f"{trend['trend_score']:.2f}"
                )
            
            console.print(trend_table)
            console.print(f"✅ 서비스 트렌드 {len(trends['trends'])}개 조회 완료")
        else:
            console.print("❌ 서비스 트렌드 데이터 없음")
        
        console.print("\n🎉 모든 테스트 완료!", style="bold green")
        
    except Exception as e:
        console.print(f"❌ 테스트 실패: {e}", style="red")
        import traceback
        traceback.print_exc()

if __name__ == "__main__":
    test_preference_service() 