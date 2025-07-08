"""
Beautifly ETL Pipeline

실제 beautifly 데이터베이스에서 analytics 데이터베이스로 데이터를 처리하는 ETL 파이프라인
"""

from datetime import datetime, timedelta
from typing import Dict, Any, List
import asyncio

from analytics.etl.base import BaseETLPipeline, ETLResult
from analytics.etl.config import ETLConfig
from analytics.etl.beautifly_extractors import (
    BeautiflyCustomerExtractor,
    BeautiflyReservationExtractor,
    BeautiflyServiceExtractor,
    BeautiflyReservationServiceExtractor,
    BeautiflyStaffExtractor
)
from analytics.etl.transformers import (
    CustomerAnalyticsTransformer,
    VisitAnalyticsTransformer,
    ServicePreferenceTransformer,
    ServiceTagsTransformer
)
from analytics.etl.loaders import (
    CustomerAnalyticsLoader,
    VisitAnalyticsLoader,
    ServicePreferenceLoader,
    ServiceTagsLoader
)
from analytics.core.logging import get_logger

logger = get_logger("beautifly_pipeline")


class BeautiflyETLPipeline(BaseETLPipeline):
    """Beautifly 데이터베이스 ETL 파이프라인."""
    
    def __init__(self, config: ETLConfig = None):
        super().__init__(config)
        self._setup_components()
        
    def _setup_components(self):
        """ETL 컴포넌트들을 설정."""
        # 추출기들
        self.customer_extractor = BeautiflyCustomerExtractor(self.config)
        self.reservation_extractor = BeautiflyReservationExtractor(self.config)
        self.service_extractor = BeautiflyServiceExtractor(self.config)
        self.reservation_service_extractor = BeautiflyReservationServiceExtractor(self.config)
        self.staff_extractor = BeautiflyStaffExtractor(self.config)
        
        # 변환기들
        self.customer_transformer = CustomerAnalyticsTransformer(self.config)
        self.visit_transformer = VisitAnalyticsTransformer(self.config)
        self.preference_transformer = ServicePreferenceTransformer(self.config)
        self.tags_transformer = ServiceTagsTransformer(self.config)
        
        # 로더들
        self.customer_loader = CustomerAnalyticsLoader(self.config)
        self.visit_loader = VisitAnalyticsLoader(self.config)
        self.preference_loader = ServicePreferenceLoader(self.config)
        self.tags_loader = ServiceTagsLoader(self.config)
        
    async def run(self, incremental: bool = True) -> Dict[str, ETLResult]:
        """ETL 파이프라인 실행."""
        self.log_start("beautifly_etl_pipeline", incremental=incremental)
        
        results = {}
        
        try:
            # 1. 고객 분석 데이터 ETL
            results['customer_analytics'] = await self._run_customer_analytics_etl(incremental)
            
            # 2. 방문 분석 데이터 ETL  
            results['visit_analytics'] = await self._run_visit_analytics_etl(incremental)
            
            # 3. 서비스 선호도 분석 ETL
            results['service_preferences'] = await self._run_service_preference_etl(incremental)
            
            # 4. 서비스 태그 ETL
            results['service_tags'] = await self._run_service_tags_etl()
            
            # 5. 최종 고객 통계 업데이트
            results['customer_stats_update'] = await self._update_customer_analytics()
            
            # 전체 결과 집계
            overall_result = self._aggregate_results(results)
            self.log_completion("beautifly_etl_pipeline", overall_result)
            
            return results
            
        except Exception as e:
            self.log_error("beautifly_etl_pipeline", e)
            error_result = ETLResult(success=False, error_message=str(e))
            results['error'] = error_result
            return results
    
    async def _run_customer_analytics_etl(self, incremental: bool) -> ETLResult:
        """고객 분석 데이터 ETL 실행."""
        self.log_start("customer_analytics_etl")
        
        try:
            start_time = datetime.now()
            total_processed = 0
            total_inserted = 0
            total_updated = 0
            
            # 증분 처리를 위한 날짜 범위 계산
            start_date = None
            end_date = None
            if incremental:
                last_update = self.customer_extractor.get_last_update_time()
                if last_update:
                    start_date = last_update - timedelta(days=1)  # 안전 여백
            
            # 데이터 추출, 변환, 로드
            for customer_chunk in self.customer_extractor.extract(start_date, end_date):
                if customer_chunk.empty:
                    continue
                
                # 데이터 변환
                transformed_data = self.customer_transformer.transform(customer_chunk)
                
                # 데이터 로드
                load_result = self.customer_loader.load(transformed_data)
                
                total_processed += len(customer_chunk)
                total_inserted += load_result.records_inserted
                total_updated += load_result.records_updated
                
                self.log_progress("customer_analytics_etl", total_processed)
            
            processing_time = (datetime.now() - start_time).total_seconds()
            
            # 메타데이터 업데이트
            result = ETLResult(
                success=True,
                records_processed=total_processed,
                records_inserted=total_inserted,
                records_updated=total_updated,
                processing_time_seconds=processing_time
            )
            
            self.customer_loader.update_metadata("customer_analytics", result)
            return result
            
        except Exception as e:
            self.log_error("customer_analytics_etl", e)
            return ETLResult(success=False, error_message=str(e))
    
    async def _run_visit_analytics_etl(self, incremental: bool) -> ETLResult:
        """방문 분석 데이터 ETL 실행."""
        self.log_start("visit_analytics_etl")
        
        try:
            start_time = datetime.now()
            total_processed = 0
            total_inserted = 0
            total_updated = 0
            
            # 증분 처리를 위한 날짜 범위 계산
            start_date = None
            end_date = None
            if incremental:
                last_update = self.reservation_extractor.get_last_update_time()
                if last_update:
                    start_date = last_update - timedelta(days=1)  # 안전 여백
            
            # 데이터 추출, 변환, 로드
            for visit_chunk in self.reservation_extractor.extract(start_date, end_date):
                if visit_chunk.empty:
                    continue
                
                # 데이터 변환
                transformed_data = self.visit_transformer.transform(visit_chunk)
                
                # 데이터 로드
                load_result = self.visit_loader.load(transformed_data)
                
                total_processed += len(visit_chunk)
                total_inserted += load_result.records_inserted
                total_updated += load_result.records_updated
                
                self.log_progress("visit_analytics_etl", total_processed)
            
            processing_time = (datetime.now() - start_time).total_seconds()
            
            result = ETLResult(
                success=True,
                records_processed=total_processed,
                records_inserted=total_inserted,
                records_updated=total_updated,
                processing_time_seconds=processing_time
            )
            
            self.visit_loader.update_metadata("visit_analytics", result)
            return result
            
        except Exception as e:
            self.log_error("visit_analytics_etl", e)
            return ETLResult(success=False, error_message=str(e))
    
    async def _run_service_preference_etl(self, incremental: bool) -> ETLResult:
        """서비스 선호도 분석 ETL 실행."""
        self.log_start("service_preference_etl")
        
        try:
            start_time = datetime.now()
            
            # 예약-서비스 상세 데이터에서 선호도 분석
            start_date = None
            end_date = None
            if incremental:
                last_update = self.reservation_service_extractor.get_last_update_time()
                if last_update:
                    start_date = last_update - timedelta(days=7)  # 선호도는 더 넓은 범위 필요
            
            # 모든 예약-서비스 데이터를 메모리에 로드 (선호도 계산을 위해)
            all_data = []
            for chunk in self.reservation_service_extractor.extract(start_date, end_date):
                if not chunk.empty:
                    all_data.append(chunk)
            
            if not all_data:
                return ETLResult(success=True, records_processed=0)
            
            # 데이터 결합
            import pandas as pd
            combined_data = pd.concat(all_data, ignore_index=True)
            
            # 데이터 변환 (선호도 분석)
            transformed_data = self.preference_transformer.transform(combined_data)
            
            # 데이터 로드
            load_result = self.preference_loader.load(transformed_data)
            
            processing_time = (datetime.now() - start_time).total_seconds()
            
            result = ETLResult(
                success=True,
                records_processed=len(combined_data),
                records_inserted=load_result.records_inserted,
                records_updated=load_result.records_updated,
                processing_time_seconds=processing_time
            )
            
            self.preference_loader.update_metadata("customer_service_preferences", result)
            return result
            
        except Exception as e:
            self.log_error("service_preference_etl", e)
            return ETLResult(success=False, error_message=str(e))
    
    async def _run_service_tags_etl(self) -> ETLResult:
        """서비스 태그 ETL 실행 (선호도 데이터 기반)."""
        self.log_start("service_tags_etl")
        
        try:
            start_time = datetime.now()
            
            # 선호도 데이터에서 태그 생성
            preference_data = self._get_all_preference_data()
            
            if preference_data.empty:
                return ETLResult(success=True, records_processed=0)
            
            # 데이터 변환 (태그 생성)
            transformed_data = self.tags_transformer.transform(preference_data)
            
            # 데이터 로드
            load_result = self.tags_loader.load(transformed_data)
            
            processing_time = (datetime.now() - start_time).total_seconds()
            
            result = ETLResult(
                success=True,
                records_processed=len(preference_data),
                records_inserted=load_result.records_inserted,
                records_updated=load_result.records_updated,
                processing_time_seconds=processing_time
            )
            
            self.tags_loader.update_metadata("customer_service_tags", result)
            return result
            
        except Exception as e:
            self.log_error("service_tags_etl", e)
            return ETLResult(success=False, error_message=str(e))
    
    def _get_all_preference_data(self) -> 'pd.DataFrame':
        """모든 선호도 데이터 조회."""
        from analytics.core.database import get_analytics_db
        import pandas as pd
        
        try:
            analytics_db = get_analytics_db()
            result = analytics_db.execute("""
                SELECT customer_id, service_id, service_name, service_category,
                       total_visits, total_amount, avg_amount, preference_rank,
                       visit_ratio, amount_ratio
                FROM customer_service_preferences
                ORDER BY customer_id, preference_rank
            """).fetchall()
            
            if result:
                columns = ['customer_id', 'service_id', 'service_name', 'service_category',
                          'total_visits', 'total_amount', 'avg_amount', 'preference_rank',
                          'visit_ratio', 'amount_ratio']
                return pd.DataFrame(result, columns=columns)
            else:
                return pd.DataFrame()
                
        except Exception as e:
            logger.error(f"Failed to get preference data: {e}")
            return pd.DataFrame()
    
    async def _update_customer_analytics(self) -> ETLResult:
        """고객 분석 데이터의 계산된 필드들 업데이트."""
        self.log_start("customer_stats_update")
        
        try:
            start_time = datetime.now()
            updated_count = await self._calculate_and_update_customer_stats()
            processing_time = (datetime.now() - start_time).total_seconds()
            
            return ETLResult(
                success=True,
                records_updated=updated_count,
                processing_time_seconds=processing_time
            )
            
        except Exception as e:
            self.log_error("customer_stats_update", e)
            return ETLResult(success=False, error_message=str(e))
    
    async def _calculate_and_update_customer_stats(self) -> int:
        """고객별 통계 계산 및 업데이트."""
        from analytics.core.database import get_analytics_db
        
        analytics_db = get_analytics_db()
        
        # 고객별 첫 방문일과 총 방문일 수 계산
        update_query = """
        UPDATE customer_analytics 
        SET 
            first_visit_date = (
                SELECT MIN(visit_date) 
                FROM visit_analytics 
                WHERE visit_analytics.customer_id = customer_analytics.customer_id
            ),
            lifecycle_days = DATEDIFF('day', (
                SELECT MIN(visit_date) 
                FROM visit_analytics 
                WHERE visit_analytics.customer_id = customer_analytics.customer_id
            ), CURRENT_DATE),
            days_since_last_visit = DATEDIFF('day', (
                SELECT MAX(visit_date) 
                FROM visit_analytics 
                WHERE visit_analytics.customer_id = customer_analytics.customer_id
            ), CURRENT_DATE),
            visit_frequency = (
                SELECT COUNT(*) / GREATEST(1, DATEDIFF('day', MIN(visit_date), CURRENT_DATE) / 30.0)
                FROM visit_analytics 
                WHERE visit_analytics.customer_id = customer_analytics.customer_id
            ),
            updated_at = CURRENT_TIMESTAMP
        WHERE customer_id IN (
            SELECT DISTINCT customer_id FROM visit_analytics
        )
        """
        
        result = analytics_db.execute(update_query)
        return result.rowcount if hasattr(result, 'rowcount') else 0
    
    def _aggregate_results(self, results: Dict[str, ETLResult]) -> ETLResult:
        """여러 ETL 결과를 집계."""
        total_processed = 0
        total_inserted = 0
        total_updated = 0
        total_time = 0.0
        all_success = True
        error_messages = []
        
        for step_name, result in results.items():
            if result.success:
                total_processed += result.records_processed
                total_inserted += result.records_inserted
                total_updated += result.records_updated
                total_time += result.processing_time_seconds
            else:
                all_success = False
                if result.error_message:
                    error_messages.append(f"{step_name}: {result.error_message}")
        
        return ETLResult(
            success=all_success,
            records_processed=total_processed,
            records_inserted=total_inserted,
            records_updated=total_updated,
            processing_time_seconds=total_time,
            error_message="; ".join(error_messages) if error_messages else None
        )


def create_beautifly_pipeline(config: ETLConfig = None) -> BeautiflyETLPipeline:
    """Beautifly ETL 파이프라인 생성."""
    if config is None:
        config = ETLConfig.from_settings()
    
    return BeautiflyETLPipeline(config) 