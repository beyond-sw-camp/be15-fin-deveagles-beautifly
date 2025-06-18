"""ETL configuration management."""

from datetime import datetime, timedelta
from typing import Dict, List, Optional
from dataclasses import dataclass

from analytics.core.config import get_settings


@dataclass
class ETLConfig:
    """ETL 파이프라인 설정."""
    
    # 배치 처리 설정
    batch_size: int = 10000
    max_workers: int = 4
    chunk_size: int = 1000
    
    # 증분 처리 설정
    incremental: bool = True
    lookback_days: int = 7  # 증분 처리 시 조회할 과거 일수
    
    # 재시도 설정
    max_retries: int = 3
    retry_delay_seconds: int = 60
    
    # 타임아웃 설정
    query_timeout_seconds: int = 300
    connection_timeout_seconds: int = 30
    
    # 데이터 품질 설정
    data_quality_checks: bool = True
    null_threshold: float = 0.1  # 허용 가능한 NULL 비율
    duplicate_threshold: float = 0.05  # 허용 가능한 중복 비율
    
    @classmethod
    def from_settings(cls) -> "ETLConfig":
        """설정에서 ETL 구성을 생성."""
        settings = get_settings()
        
        return cls(
            batch_size=settings.etl_batch_size,
            incremental=settings.etl_incremental,
            # 기타 설정은 기본값 사용
        )
    
    def get_incremental_start_date(self, last_run: Optional[datetime] = None) -> datetime:
        """증분 처리 시작 날짜 계산."""
        if not self.incremental or last_run is None:
            # 전체 처리 또는 첫 실행
            return datetime.now() - timedelta(days=365)  # 1년 전부터
        
        # 안전을 위해 마지막 실행 시간에서 lookback_days만큼 뒤로
        return last_run - timedelta(days=self.lookback_days)


@dataclass 
class SparkConfig:
    """Spark 작업 설정."""
    
    app_name: str = "deveagles-analytics-etl"
    master: str = "local[*]"
    
    # 메모리 설정
    driver_memory: str = "2g"
    executor_memory: str = "2g"
    max_result_size: str = "1g"
    
    # 파티션 설정
    default_parallelism: int = 4
    sql_adaptive_enabled: bool = True
    sql_adaptive_coalesce_partitions_enabled: bool = True
    
    # 직렬화 설정
    serializer: str = "org.apache.spark.serializer.KryoSerializer"
    
    def to_spark_conf(self) -> Dict[str, str]:
        """Spark 설정 딕셔너리로 변환."""
        return {
            "spark.app.name": self.app_name,
            "spark.master": self.master,
            "spark.driver.memory": self.driver_memory,
            "spark.executor.memory": self.executor_memory,
            "spark.driver.maxResultSize": self.max_result_size,
            "spark.default.parallelism": str(self.default_parallelism),
            "spark.sql.adaptive.enabled": str(self.sql_adaptive_enabled).lower(),
            "spark.sql.adaptive.coalescePartitions.enabled": str(self.sql_adaptive_coalesce_partitions_enabled).lower(),
            "spark.serializer": self.serializer,
        }


@dataclass
class AirflowTaskConfig:
    """Airflow 태스크 설정."""
    
    # 태스크 기본 설정
    retries: int = 2
    retry_delay_minutes: int = 5
    execution_timeout_minutes: int = 30
    
    # 의존성 설정
    depends_on_past: bool = False
    wait_for_downstream: bool = False
    
    # 리소스 설정
    pool: Optional[str] = None
    priority_weight: int = 1
    
    # 센서 설정 (데이터 가용성 체크)
    sensor_timeout_minutes: int = 60
    sensor_poke_interval_seconds: int = 60
    
    def to_airflow_args(self) -> Dict[str, any]:
        """Airflow 기본 인수 딕셔너리로 변환."""
        return {
            "retries": self.retries,
            "retry_delay": timedelta(minutes=self.retry_delay_minutes),
            "execution_timeout": timedelta(minutes=self.execution_timeout_minutes),
            "depends_on_past": self.depends_on_past,
            "wait_for_downstream": self.wait_for_downstream,
            "pool": self.pool,
            "priority_weight": self.priority_weight,
        }


# 사전 정의된 설정들
DEFAULT_ETL_CONFIG = ETLConfig()
DEFAULT_SPARK_CONFIG = SparkConfig()
DEFAULT_AIRFLOW_CONFIG = AirflowTaskConfig()

# 환경별 설정
PRODUCTION_ETL_CONFIG = ETLConfig(
    batch_size=50000,
    max_workers=8,
    chunk_size=5000,
    max_retries=5,
    query_timeout_seconds=600,
)

DEVELOPMENT_ETL_CONFIG = ETLConfig(
    batch_size=1000,
    max_workers=2,
    chunk_size=100,
    lookback_days=1,
    query_timeout_seconds=60,
) 