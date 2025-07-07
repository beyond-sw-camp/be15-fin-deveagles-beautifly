#!/usr/bin/env python3
"""
Beautifly ETL Pipeline Test Script

실제 beautifly 데이터베이스를 사용한 ETL 파이프라인 테스트
"""

import asyncio
import sys
from pathlib import Path
from datetime import datetime

# 프로젝트 루트를 Python 경로에 추가
sys.path.insert(0, str(Path(__file__).parent.parent.parent / "src"))

from analytics.etl.beautifly_pipeline import create_beautifly_pipeline
from analytics.etl.config import ETLConfig
from analytics.core.logging import get_logger, setup_logging

# 로깅 설정
setup_logging()
logger = get_logger("beautifly_etl_test")


def test_database_connections():
    """데이터베이스 연결 테스트."""
    print("🔍 Testing database connections...")
    print("=" * 60)
    
    try:
        # CRM DB 연결 테스트
        from analytics.core.database import get_crm_db
        crm_engine = get_crm_db()
        
        with crm_engine.connect() as conn:
            from sqlalchemy import text
            result = conn.execute(text("SELECT COUNT(*) as customer_count FROM customer WHERE deleted_at IS NULL"))
            customer_count = result.fetchone()[0]
            print(f"✅ CRM Database: {customer_count:,} customers found")
        
        # Analytics DB 연결 테스트
        from analytics.core.database import get_analytics_db
        analytics_db = get_analytics_db()
        
        analytics_db.execute("SELECT 1")
        print("✅ Analytics Database: Connection successful")
        
        # Analytics 테이블 확인
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
                print(f"❌ {table}: Missing - {e}")
                missing_tables.append(table)
        
        if missing_tables:
            print(f"\n⚠️ Missing tables: {', '.join(missing_tables)}")
            print("Please run: python -m analytics.cli init-analytics-db")
            return False
        
        return True
        
    except Exception as e:
        print(f"❌ Database connection test failed: {e}")
        return False


def test_extractors():
    """ETL 추출기 테스트."""
    print("\n🔧 Testing ETL extractors...")
    print("=" * 60)
    
    try:
        from analytics.etl.beautifly_extractors import (
            BeautiflyCustomerExtractor,
            BeautiflyReservationExtractor,
            BeautiflyServiceExtractor,
            BeautiflyReservationServiceExtractor
        )
        
        config = ETLConfig(batch_size=100)  # 작은 배치 사이즈로 테스트
        
        # 1. 고객 추출기 테스트
        print("\n1. Testing Customer Extractor...")
        customer_extractor = BeautiflyCustomerExtractor(config)
        
        customer_data_found = False
        total_customers = 0
        for chunk in customer_extractor.extract():
            total_customers += len(chunk)
            if not chunk.empty:
                customer_data_found = True
                print(f"   Sample customer: {chunk.iloc[0]['name']} (ID: {chunk.iloc[0]['customer_id']})")
                break
        
        if customer_data_found:
            print(f"✅ Customer Extractor: {total_customers:,} customers found")
        else:
            print("❌ Customer Extractor: No data found")
            return False
        
        # 2. 예약 추출기 테스트
        print("\n2. Testing Reservation Extractor...")
        reservation_extractor = BeautiflyReservationExtractor(config)
        
        reservation_data_found = False
        total_reservations = 0
        for chunk in reservation_extractor.extract():
            total_reservations += len(chunk)
            if not chunk.empty:
                reservation_data_found = True
                print(f"   Sample reservation: Customer {chunk.iloc[0]['customer_id']} on {chunk.iloc[0]['visit_date']}")
                break
        
        if reservation_data_found:
            print(f"✅ Reservation Extractor: {total_reservations:,} reservations found")
        else:
            print("❌ Reservation Extractor: No data found")
            return False
        
        # 3. 서비스 추출기 테스트
        print("\n3. Testing Service Extractor...")
        service_extractor = BeautiflyServiceExtractor(config)
        
        service_data_found = False
        total_services = 0
        for chunk in service_extractor.extract():
            total_services += len(chunk)
            if not chunk.empty:
                service_data_found = True
                print(f"   Sample service: {chunk.iloc[0]['service_name']} ({chunk.iloc[0]['category']})")
                break
        
        if service_data_found:
            print(f"✅ Service Extractor: {total_services:,} services found")
        else:
            print("❌ Service Extractor: No data found")
            return False
        
        # 4. 예약-서비스 상세 추출기 테스트
        print("\n4. Testing Reservation-Service Detail Extractor...")
        detail_extractor = BeautiflyReservationServiceExtractor(config)
        
        detail_data_found = False
        total_details = 0
        for chunk in detail_extractor.extract():
            total_details += len(chunk)
            if not chunk.empty:
                detail_data_found = True
                print(f"   Sample detail: Service {chunk.iloc[0]['service_name']} for Customer {chunk.iloc[0]['customer_id']}")
                break
        
        if detail_data_found:
            print(f"✅ Detail Extractor: {total_details:,} service details found")
        else:
            print("❌ Detail Extractor: No data found")
            return False
        
        return True
        
    except Exception as e:
        print(f"❌ Extractor test failed: {e}")
        import traceback
        traceback.print_exc()
        return False


async def test_etl_pipeline():
    """전체 ETL 파이프라인 테스트."""
    print("\n🚀 Testing Beautifly ETL Pipeline...")
    print("=" * 60)
    
    try:
        # ETL 설정
        config = ETLConfig(
            batch_size=500,  # 적당한 배치 사이즈
            incremental=False  # 첫 실행은 전체 처리
        )
        
        # 파이프라인 생성
        pipeline = create_beautifly_pipeline(config)
        
        print("🔄 Starting ETL pipeline execution...")
        start_time = datetime.now()
        
        # ETL 실행
        results = await pipeline.run(incremental=False)
        
        execution_time = (datetime.now() - start_time).total_seconds()
        print(f"⏱️  Total execution time: {execution_time:.2f} seconds")
        
        # 결과 분석
        print("\n📊 ETL Results:")
        print("-" * 40)
        
        total_processed = 0
        total_inserted = 0
        total_updated = 0
        success_count = 0
        
        for step_name, result in results.items():
            if result.success:
                success_count += 1
                total_processed += result.records_processed
                total_inserted += result.records_inserted
                total_updated += result.records_updated
                
                print(f"✅ {step_name}:")
                print(f"   - Processed: {result.records_processed:,}")
                print(f"   - Inserted: {result.records_inserted:,}")
                print(f"   - Updated: {result.records_updated:,}")
                print(f"   - Time: {result.processing_time_seconds:.2f}s")
            else:
                print(f"❌ {step_name}: {result.error_message}")
        
        print(f"\n📈 Summary:")
        print(f"   - Steps completed: {success_count}/{len(results)}")
        print(f"   - Total processed: {total_processed:,}")
        print(f"   - Total inserted: {total_inserted:,}")
        print(f"   - Total updated: {total_updated:,}")
        
        # Analytics 데이터베이스 결과 확인
        print("\n🔍 Verifying analytics data...")
        from analytics.core.database import get_analytics_db
        analytics_db = get_analytics_db()
        
        tables_to_check = [
            "customer_analytics",
            "visit_analytics",
            "customer_service_preferences", 
            "customer_service_tags"
        ]
        
        for table in tables_to_check:
            try:
                result = analytics_db.execute(f"SELECT COUNT(*) FROM {table}").fetchone()
                count = result[0] if result else 0
                print(f"   {table}: {count:,} records")
            except Exception as e:
                print(f"   {table}: Error - {e}")
        
        return success_count == len(results)
        
    except Exception as e:
        print(f"❌ ETL pipeline test failed: {e}")
        import traceback
        traceback.print_exc()
        return False


async def main():
    """메인 테스트 함수."""
    print("🧪 Beautifly ETL Pipeline Test Suite")
    print("=" * 80)
    
    # 1. 데이터베이스 연결 테스트
    if not test_database_connections():
        print("\n❌ Database connection tests failed. Please check your setup.")
        return False
    
    # 2. 추출기 테스트
    if not test_extractors():
        print("\n❌ Extractor tests failed. Please check your CRM database.")
        return False
    
    # 3. 전체 ETL 파이프라인 테스트
    etl_success = await test_etl_pipeline()
    
    if etl_success:
        print("\n🎉 All tests passed! Beautifly ETL pipeline is working correctly.")
        print("\n💡 Next steps:")
        print("   1. You can now run incremental ETL updates")
        print("   2. Set up scheduled ETL jobs")
        print("   3. Test customer segmentation and risk tagging")
        print("   4. Explore analytics data with the CLI tools")
        
        print("\n🔧 CLI Commands to try:")
        print("   cd src && python -m analytics.cli segment --show-distribution")
        print("   cd src && python -m analytics.cli preference --show-ranking")
        print("   cd src && python -m analytics.cli risk-tagging --show-distribution")
    else:
        print("\n❌ ETL pipeline test failed. Please check the logs above.")
    
    return etl_success


if __name__ == "__main__":
    success = asyncio.run(main())
    sys.exit(0 if success else 1) 