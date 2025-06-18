#!/usr/bin/env python3
"""ETL Pipeline Test Script."""

import asyncio
import sys
from pathlib import Path

# 프로젝트 루트를 Python 경로에 추가
sys.path.insert(0, str(Path(__file__).parent / "src"))

from analytics.etl.pipeline import create_pipeline
from analytics.etl.config import DEVELOPMENT_ETL_CONFIG
from analytics.core.logging import get_logger


async def test_etl_pipeline():
    """ETL 파이프라인 테스트."""
    logger = get_logger("test_etl")
    
    print("🧪 ETL Pipeline Test")
    print("=" * 50)
    
    try:
        # 1. 파이프라인 생성 테스트
        print("1. Creating ETL pipeline...")
        pipeline = create_pipeline(use_spark=False, config=DEVELOPMENT_ETL_CONFIG)
        print("✅ Pipeline created successfully")
        
        # 2. 데이터베이스 연결 테스트
        print("\n2. Testing database connections...")
        
        from analytics.core.database import get_crm_db, get_analytics_db
        from sqlalchemy import text
        
        # CRM DB 테스트
        try:
            crm_db = get_crm_db()
            with crm_db.connect() as conn:
                conn.execute(text("SELECT 1"))
            print("✅ CRM database connection: OK")
        except Exception as e:
            print(f"❌ CRM database connection: {e}")
            return False
        
        # Analytics DB 테스트
        try:
            analytics_db = get_analytics_db()
            analytics_db.execute("SELECT 1")
            print("✅ Analytics database connection: OK")
        except Exception as e:
            print(f"❌ Analytics database connection: {e}")
            return False
        
        # 3. 테이블 존재 확인
        print("\n3. Checking required tables...")
        
        required_tables = [
            "customer_analytics",
            "visit_analytics",
            "customer_service_preferences",
            "customer_service_tags",
            "etl_metadata"
        ]
        
        missing_tables = []
        
        for table in required_tables:
            try:
                result = analytics_db.execute(f"SELECT COUNT(*) FROM {table}").fetchone()
                count = result[0] if result else 0
                print(f"✅ {table}: {count} records")
            except Exception as e:
                print(f"❌ {table}: Missing or error - {e}")
                missing_tables.append(table)
        
        if missing_tables:
            print(f"\n⚠️ Missing tables: {', '.join(missing_tables)}")
            print("Run 'python -m analytics.cli init-analytics-db' to create tables")
            return False
        
        # 4. ETL 파이프라인 실행 (dry run)
        print("\n4. Running ETL pipeline (dry run)...")
        
        # 실제로는 데이터가 없을 수 있으므로 컴포넌트 생성만 테스트
        try:
            # 추출기 테스트
            from analytics.etl.extractors import CustomerExtractor, VisitExtractor
            customer_extractor = CustomerExtractor(DEVELOPMENT_ETL_CONFIG)
            visit_extractor = VisitExtractor(DEVELOPMENT_ETL_CONFIG)
            print("✅ Extractors created successfully")
            
            # 변환기 테스트
            from analytics.etl.transformers import CustomerAnalyticsTransformer, VisitAnalyticsTransformer
            customer_transformer = CustomerAnalyticsTransformer(DEVELOPMENT_ETL_CONFIG)
            visit_transformer = VisitAnalyticsTransformer(DEVELOPMENT_ETL_CONFIG)
            print("✅ Transformers created successfully")
            
            # 로더 테스트
            from analytics.etl.loaders import CustomerAnalyticsLoader, VisitAnalyticsLoader
            customer_loader = CustomerAnalyticsLoader(DEVELOPMENT_ETL_CONFIG)
            visit_loader = VisitAnalyticsLoader(DEVELOPMENT_ETL_CONFIG)
            print("✅ Loaders created successfully")
            
        except Exception as e:
            print(f"❌ ETL component creation failed: {e}")
            return False
        
        print("\n✅ All ETL tests passed!")
        return True
        
    except Exception as e:
        logger.error(f"ETL test failed: {e}")
        print(f"❌ ETL test failed: {e}")
        return False


async def test_sample_etl_run():
    """샘플 데이터로 실제 ETL 실행 테스트."""
    print("\n🔄 Sample ETL Run Test")
    print("=" * 50)
    
    try:
        # 샘플 데이터가 있는지 확인
        from analytics.core.database import get_crm_db
        
        crm_db = get_crm_db()
        with crm_db.connect() as conn:
            customer_count = conn.execute(text("SELECT COUNT(*) FROM customers")).fetchone()[0]
            visit_count = conn.execute(text("SELECT COUNT(*) FROM visits")).fetchone()[0]
        
        print(f"CRM Data: {customer_count} customers, {visit_count} visits")
        
        if customer_count == 0:
            print("⚠️ No sample data found. Run 'python -m analytics.cli create-sample' first")
            return False
        
        # ETL 실행
        pipeline = create_pipeline(use_spark=False, config=DEVELOPMENT_ETL_CONFIG)
        
        print("Starting ETL pipeline...")
        results = await pipeline.run(incremental=False)  # 전체 실행
        
        # 결과 출력
        print("\n📊 ETL Results:")
        for step, result in results.items():
            if result.success:
                print(f"✅ {step}: {result.records_processed} processed "
                     f"({result.processing_time_seconds:.2f}s)")
            else:
                print(f"❌ {step}: {result.error_message}")
        
        # 결과 데이터 확인
        from analytics.core.database import get_analytics_db
        analytics_db = get_analytics_db()
        
        print("\n📈 Analytics Data After ETL:")
        tables = ["customer_analytics", "visit_analytics", "customer_service_preferences", "customer_service_tags"]
        
        for table in tables:
            try:
                count = analytics_db.execute(f"SELECT COUNT(*) FROM {table}").fetchone()[0]
                print(f"✅ {table}: {count} records")
            except Exception as e:
                print(f"❌ {table}: {e}")
        
        return True
        
    except Exception as e:
        print(f"❌ Sample ETL run failed: {e}")
        return False


async def main():
    """메인 테스트 함수."""
    print("DevEagles Analytics ETL Test Suite")
    print("=" * 60)
    
    # 기본 테스트
    basic_test_passed = await test_etl_pipeline()
    
    if not basic_test_passed:
        print("\n❌ Basic tests failed. Please fix issues before proceeding.")
        return False
    
    # 샘플 ETL 실행 테스트 (선택사항)
    print("\n" + "=" * 60)
    
    user_input = input("Run sample ETL test? (y/N): ").strip().lower()
    if user_input in ['y', 'yes']:
        sample_test_passed = await test_sample_etl_run()
        
        if sample_test_passed:
            print("\n🎉 All tests passed successfully!")
        else:
            print("\n⚠️ Sample ETL test failed, but basic functionality is OK")
    else:
        print("\n✅ Basic ETL tests completed successfully!")
    
    return True


if __name__ == "__main__":
    asyncio.run(main()) 