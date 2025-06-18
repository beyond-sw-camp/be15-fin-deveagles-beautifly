"""Main ETL pipeline orchestrator."""

import asyncio
from datetime import datetime
from typing import Dict, List, Optional
from concurrent.futures import ThreadPoolExecutor

from analytics.core.logging import get_logger
from .base import BaseETLPipeline, ETLResult
from .config import ETLConfig
from .extractors import (
    CustomerExtractor, 
    VisitExtractor, 
    VisitServiceExtractor
)
from .transformers import (
    CustomerAnalyticsTransformer,
    VisitAnalyticsTransformer,
    ServicePreferenceTransformer,
    ServiceTagsTransformer
)
from .loaders import (
    CustomerAnalyticsLoader,
    VisitAnalyticsLoader,
    ServicePreferenceLoader,
    ServiceTagsLoader
)


class ETLPipeline(BaseETLPipeline):
    """고객 분석 ETL 파이프라인."""
    
    def __init__(self, config: ETLConfig = None):
        super().__init__(config)
        self.logger = get_logger("ETLPipeline")
        
        # 컴포넌트 초기화
        self._setup_components()
    
    def _setup_components(self):
        """ETL 컴포넌트 설정."""
        # Extractors
        self.customer_extractor = CustomerExtractor(self.config)
        self.visit_extractor = VisitExtractor(self.config)
        self.visit_service_extractor = VisitServiceExtractor(self.config)
        
        # Transformers
        self.customer_transformer = CustomerAnalyticsTransformer(self.config)
        self.visit_transformer = VisitAnalyticsTransformer(self.config)
        self.preference_transformer = ServicePreferenceTransformer(self.config)
        self.tags_transformer = ServiceTagsTransformer(self.config)
        
        # Loaders
        self.customer_loader = CustomerAnalyticsLoader(self.config)
        self.visit_loader = VisitAnalyticsLoader(self.config)
        self.preference_loader = ServicePreferenceLoader(self.config)
        self.tags_loader = ServiceTagsLoader(self.config)
    
    async def run(self, incremental: bool = True) -> Dict[str, ETLResult]:
        """전체 ETL 파이프라인 실행."""
        self.log_start("etl_pipeline", incremental=incremental)
        
        results = {}
        
        try:
            # 1. 고객 분석 ETL
            results['customer_analytics'] = await self._run_customer_analytics_etl(incremental)
            
            # 2. 방문 분석 ETL
            results['visit_analytics'] = await self._run_visit_analytics_etl(incremental)
            
            # 3. 서비스 선호도 분석 ETL (방문 데이터 기반)
            results['service_preferences'] = await self._run_service_preference_etl(incremental)
            
            # 4. 서비스 태그 생성 ETL (선호도 데이터 기반)
            results['service_tags'] = await self._run_service_tags_etl()
            
            # 5. 고객 분석 데이터 업데이트 (방문 통계 반영)
            results['customer_analytics_update'] = await self._update_customer_analytics()
            
            self.log_completion("etl_pipeline", self._aggregate_results(results))
            
        except Exception as e:
            self.log_error("etl_pipeline", e)
            results['pipeline_error'] = ETLResult(success=False, error_message=str(e))
        
        return results
    
    async def _run_customer_analytics_etl(self, incremental: bool) -> ETLResult:
        """고객 분석 ETL 실행."""
        try:
            self.log_start("customer_analytics_etl")
            
            # 추출
            last_run = None
            if incremental:
                last_run = self.customer_extractor.get_last_update_time()
            
            total_processed = 0
            total_inserted = 0
            total_updated = 0
            
            # 청크 단위로 처리
            for chunk in self.customer_extractor.extract_incremental(last_run):
                if chunk.empty:
                    continue
                
                # 변환
                transformed = self.customer_transformer.transform_batch(chunk)
                
                # 적재
                result = self.customer_loader.load_batch(transformed, "customer_analytics")
                
                total_processed += result.records_processed
                total_inserted += result.records_inserted
                total_updated += result.records_updated
                
                if not result.success:
                    return result
            
            return ETLResult(
                success=True,
                records_processed=total_processed,
                records_inserted=total_inserted,
                records_updated=total_updated
            )
            
        except Exception as e:
            self.log_error("customer_analytics_etl", e)
            return ETLResult(success=False, error_message=str(e))
    
    async def _run_visit_analytics_etl(self, incremental: bool) -> ETLResult:
        """방문 분석 ETL 실행."""
        try:
            self.log_start("visit_analytics_etl")
            
            # 추출
            last_run = None
            if incremental:
                last_run = self.visit_extractor.get_last_update_time()
            
            total_processed = 0
            total_inserted = 0
            total_updated = 0
            
            # 청크 단위로 처리
            for chunk in self.visit_extractor.extract_incremental(last_run):
                if chunk.empty:
                    continue
                
                # 변환
                transformed = self.visit_transformer.transform_batch(chunk)
                
                # 적재
                result = self.visit_loader.load_batch(transformed, "visit_analytics")
                
                total_processed += result.records_processed
                total_inserted += result.records_inserted
                total_updated += result.records_updated
                
                if not result.success:
                    return result
            
            return ETLResult(
                success=True,
                records_processed=total_processed,
                records_inserted=total_inserted,
                records_updated=total_updated
            )
            
        except Exception as e:
            self.log_error("visit_analytics_etl", e)
            return ETLResult(success=False, error_message=str(e))
    
    async def _run_service_preference_etl(self, incremental: bool) -> ETLResult:
        """서비스 선호도 분석 ETL 실행."""
        try:
            self.log_start("service_preference_etl")
            
            # 추출 (방문-서비스 상세 데이터)
            last_run = None
            if incremental:
                last_run = self.visit_service_extractor.get_last_update_time()
            
            total_processed = 0
            total_inserted = 0
            total_updated = 0
            
            # 청크 단위로 처리
            for chunk in self.visit_service_extractor.extract_incremental(last_run):
                if chunk.empty:
                    continue
                
                # 변환 (고객별 서비스별 선호도 계산)
                transformed = self.preference_transformer.transform_batch(chunk)
                
                # 적재
                result = self.preference_loader.load_batch(transformed, "customer_service_preferences")
                
                total_processed += result.records_processed
                total_inserted += result.records_inserted
                total_updated += result.records_updated
                
                if not result.success:
                    return result
            
            return ETLResult(
                success=True,
                records_processed=total_processed,
                records_inserted=total_inserted,
                records_updated=total_updated
            )
            
        except Exception as e:
            self.log_error("service_preference_etl", e)
            return ETLResult(success=False, error_message=str(e))
    
    async def _run_service_tags_etl(self) -> ETLResult:
        """서비스 태그 생성 ETL 실행."""
        try:
            self.log_start("service_tags_etl")
            
            # 선호도 데이터에서 태그 생성
            # 모든 고객의 선호도 데이터를 가져와서 태그 생성
            preference_data = self._get_all_preference_data()
            
            if preference_data.empty:
                return ETLResult(success=True, records_processed=0)
            
            # 변환 (태그 생성)
            transformed = self.tags_transformer.transform_batch(preference_data)
            
            # 적재
            result = self.tags_loader.load_batch(transformed, "customer_service_tags")
            
            return result
            
        except Exception as e:
            self.log_error("service_tags_etl", e)
            return ETLResult(success=False, error_message=str(e))
    
    async def _update_customer_analytics(self) -> ETLResult:
        """고객 분석 데이터 업데이트 (방문 통계 반영)."""
        try:
            self.log_start("customer_analytics_update")
            
            # 방문 통계 계산 및 고객 분석 데이터 업데이트
            updated_count = await self._calculate_and_update_customer_stats()
            
            return ETLResult(
                success=True,
                records_processed=updated_count,
                records_updated=updated_count
            )
            
        except Exception as e:
            self.log_error("customer_analytics_update", e)
            return ETLResult(success=False, error_message=str(e))
    
    def _get_all_preference_data(self) -> 'pd.DataFrame':
        """모든 고객의 서비스 선호도 데이터 조회."""
        import pandas as pd
        
        try:
            conn = self.preference_loader.conn
            query = """
            SELECT * FROM customer_service_preferences
            ORDER BY customer_id, preference_rank
            """
            
            result = conn.execute(query).fetchdf()
            return result
            
        except Exception as e:
            self.log_error("get_all_preference_data", e)
            return pd.DataFrame()
    
    async def _calculate_and_update_customer_stats(self) -> int:
        """고객별 방문 통계 계산 및 업데이트."""
        import pandas as pd
        
        try:
            conn = self.customer_loader.conn
            
            # 고객별 방문 통계 계산
            stats_query = """
            WITH customer_visit_stats AS (
                SELECT 
                    customer_id,
                    COUNT(*) as total_visits,
                    SUM(final_amount) as total_amount,
                    AVG(final_amount) as avg_visit_amount,
                    MIN(visit_date) as first_visit_date,
                    MAX(visit_date) as last_visit_date,
                    COUNT(CASE WHEN visit_date >= CURRENT_DATE - INTERVAL 90 DAYS THEN 1 END) as visits_3m,
                    SUM(CASE WHEN visit_date >= CURRENT_DATE - INTERVAL 90 DAYS THEN final_amount ELSE 0 END) as amount_3m
                FROM visit_analytics
                GROUP BY customer_id
            )
            UPDATE customer_analytics 
            SET 
                total_visits = stats.total_visits,
                total_amount = stats.total_amount,
                avg_visit_amount = stats.avg_visit_amount,
                first_visit_date = stats.first_visit_date,
                last_visit_date = stats.last_visit_date,
                visits_3m = stats.visits_3m,
                amount_3m = stats.amount_3m,
                lifecycle_days = DATE_DIFF('day', stats.first_visit_date, stats.last_visit_date),
                days_since_last_visit = DATE_DIFF('day', stats.last_visit_date, CURRENT_DATE),
                visit_frequency = CASE 
                    WHEN DATE_DIFF('day', stats.first_visit_date, stats.last_visit_date) > 0 
                    THEN stats.total_visits::FLOAT / DATE_DIFF('day', stats.first_visit_date, stats.last_visit_date) * 30
                    ELSE 0 
                END,
                updated_at = CURRENT_TIMESTAMP
            FROM customer_visit_stats stats
            WHERE customer_analytics.customer_id = stats.customer_id
            """
            
            result = conn.execute(stats_query)
            return result.rowcount if hasattr(result, 'rowcount') else 0
            
        except Exception as e:
            self.log_error("calculate_and_update_customer_stats", e)
            raise
    
    def _aggregate_results(self, results: Dict[str, ETLResult]) -> ETLResult:
        """결과 집계."""
        total_processed = sum(r.records_processed for r in results.values() if r.success)
        total_inserted = sum(r.records_inserted for r in results.values() if r.success)
        total_updated = sum(r.records_updated for r in results.values() if r.success)
        
        success = all(r.success for r in results.values())
        errors = [r.error_message for r in results.values() if not r.success and r.error_message]
        
        return ETLResult(
            success=success,
            records_processed=total_processed,
            records_inserted=total_inserted,
            records_updated=total_updated,
            error_message="; ".join(errors) if errors else None,
            metadata={"step_results": {k: v.__dict__ for k, v in results.items()}}
        )


class SparkETLPipeline(BaseETLPipeline):
    """Spark 기반 ETL 파이프라인 (대용량 데이터 처리용)."""
    
    def __init__(self, config: ETLConfig = None, spark_config=None):
        super().__init__(config)
        self.spark_config = spark_config
        self.spark = None
        self.logger = get_logger("SparkETLPipeline")
    
    def _init_spark(self):
        """Spark 세션 초기화."""
        try:
            from pyspark.sql import SparkSession
            
            builder = SparkSession.builder
            
            if self.spark_config:
                for key, value in self.spark_config.to_spark_conf().items():
                    builder = builder.config(key, value)
            
            self.spark = builder.getOrCreate()
            self.logger.info("Spark session initialized")
            
        except ImportError:
            self.logger.error("PySpark not available. Install with: pip install pyspark")
            raise
        except Exception as e:
            self.logger.error(f"Failed to initialize Spark: {e}")
            raise
    
    async def run(self, incremental: bool = True) -> Dict[str, ETLResult]:
        """Spark 기반 ETL 파이프라인 실행."""
        if not self.spark:
            self._init_spark()
        
        try:
            # Spark 기반 대용량 데이터 처리 로직
            # 현재는 기본 파이프라인과 동일하게 구현
            # 실제 운영에서는 Spark DataFrame을 활용한 최적화된 처리 구현
            
            self.log_start("spark_etl_pipeline", incremental=incremental)
            
            # 기본 파이프라인 실행 (추후 Spark 최적화)
            pipeline = ETLPipeline(self.config)
            results = await pipeline.run(incremental)
            
            return results
            
        finally:
            if self.spark:
                self.spark.stop()
                self.logger.info("Spark session stopped")


# 파이프라인 팩토리
def create_pipeline(use_spark: bool = False, config: ETLConfig = None) -> BaseETLPipeline:
    """ETL 파이프라인 생성."""
    if use_spark:
        from .config import DEFAULT_SPARK_CONFIG
        return SparkETLPipeline(config, DEFAULT_SPARK_CONFIG)
    else:
        return ETLPipeline(config) 