"""Application configuration management with YAML support."""

import os
from functools import lru_cache
from pathlib import Path
from typing import Any, Dict, List, Optional

import yaml
from pydantic import BaseModel, Field
from pydantic_settings import BaseSettings, SettingsConfigDict


class DatabaseConfig(BaseModel):
    """Database configuration."""
    url: str
    pool_size: int = 5
    max_overflow: int = 10


class AppConfig(BaseModel):
    """Application configuration."""
    name: str = "DevEagles Analytics"
    version: str = "0.1.0"
    debug: bool = False
    log_level: str = "INFO"


class ServerConfig(BaseModel):
    """Server configuration."""
    host: str = "0.0.0.0"
    port: int = 8000
    workers: int = 1
    reload: bool = False


class ETLConfig(BaseModel):
    """ETL configuration."""
    batch_size: int = 10000
    incremental: bool = True
    timestamp_file: str = "data/last_etl.txt"


class MLConfig(BaseModel):
    """Machine Learning configuration."""
    model_storage_path: str = "models"
    retrain_threshold: float = 0.05
    feature_columns: List[str] = Field(default_factory=lambda: [
        "age", "frequency", "avg_monetary", "lifecycle_days",
        "service_variety", "employee_variety", "visits_3m"
    ])


class ChurnAnalysisNotificationConfig(BaseModel):
    """Churn analysis notification configuration."""
    enabled: bool = True
    webhook_url: Optional[str] = None
    on_success: bool = True
    on_failure: bool = True
    include_summary: bool = True


class ChurnAnalysisConfig(BaseModel):
    """Churn analysis scheduling configuration."""
    enabled: bool = True
    schedule_hour: int = 3
    schedule_minute: int = 0
    timezone: str = "Asia/Seoul"
    max_retries: int = 3
    retry_delay_minutes: int = 30
    dry_run: bool = False
    segment_update_method: str = "api"  # "api" or "direct"
    auto_apply_segments: bool = True
    notification: ChurnAnalysisNotificationConfig = Field(default_factory=ChurnAnalysisNotificationConfig)


class SchedulingConfig(BaseModel):
    """Scheduling configuration."""
    jobstore_url: str = "sqlite:///data/scheduler.db"
    etl_schedule_hour: int = 2
    tagging_schedule_hour: int = 3
    training_schedule_day: str = "sun"
    training_schedule_hour: int = 4
    churn_analysis: ChurnAnalysisConfig = Field(default_factory=ChurnAnalysisConfig)


class CustomerSegmentationConfig(BaseModel):
    """Customer segmentation configuration."""
    new_customer_visit_threshold: int = 3
    growing_customer_visit_threshold: int = 10
    vip_customer_amount_threshold: int = 100000


class RiskAssessmentConfig(BaseModel):
    """Risk assessment configuration."""
    new_customer_followup_days: int = 7
    new_customer_risk_days: int = 20
    reactivation_needed_days: int = 30
    growing_customer_delay_multiplier: float = 1.5
    loyal_customer_delay_multiplier: float = 2.0


class APIConfig(BaseModel):
    """API configuration."""
    prefix: str = "/api/v1"
    docs_url: Optional[str] = "/docs"
    redoc_url: Optional[str] = "/redoc"
    openapi_url: Optional[str] = "/openapi.json"


class SecurityConfig(BaseModel):
    """Security configuration."""
    secret_key: str = "your-secret-key-change-in-production"
    access_token_expire_minutes: int = 30
    algorithm: str = "HS256"


class MonitoringConfig(BaseModel):
    """Monitoring configuration."""
    enable_metrics: bool = True
    metrics_path: str = "/metrics"
    enable_tracing: bool = False


class ExternalServicesConfig(BaseModel):
    """External services configuration."""
    workflow_service_url: Optional[str] = None
    notification_webhook_url: Optional[str] = None
    be_api_url: str = "http://localhost:8080"
    be_api_timeout: int = 30


class LocalDevConfig(BaseModel):
    """Local development specific configuration."""
    auto_reload_templates: bool = False
    enable_debug_toolbar: bool = False
    mock_external_apis: bool = False
    sample_data_size: int = 10000


class Settings(BaseSettings):
    """Application settings with environment variable support."""

    model_config = SettingsConfigDict(
        env_file=".env",
        env_file_encoding="utf-8",
        case_sensitive=False,
        extra="ignore",
        env_prefix="ANALYTICS_",
    )

    # Environment
    environment: str = Field(default="development", description="Environment name")
    
    # Configurations
    app: AppConfig = Field(default_factory=AppConfig)
    server: ServerConfig = Field(default_factory=ServerConfig)
    database: Dict[str, DatabaseConfig] = Field(default_factory=dict)
    etl: ETLConfig = Field(default_factory=ETLConfig)
    ml: MLConfig = Field(default_factory=MLConfig)
    scheduling: SchedulingConfig = Field(default_factory=SchedulingConfig)
    customer_segmentation: CustomerSegmentationConfig = Field(default_factory=CustomerSegmentationConfig)
    risk_assessment: RiskAssessmentConfig = Field(default_factory=RiskAssessmentConfig)
    api: APIConfig = Field(default_factory=APIConfig)
    security: SecurityConfig = Field(default_factory=SecurityConfig)
    monitoring: MonitoringConfig = Field(default_factory=MonitoringConfig)
    external_services: ExternalServicesConfig = Field(default_factory=ExternalServicesConfig)
    local_dev: LocalDevConfig = Field(default_factory=LocalDevConfig)

    def __init__(self, **kwargs):
        super().__init__(**kwargs)
        self._load_yaml_config()
        self._apply_env_vars_final()

    def _load_yaml_config(self) -> None:
        """Load configuration from YAML files."""
        # config 디렉토리 경로를 상대적으로 찾기
        current_dir = Path(__file__).parent
        config_dir = current_dir.parent.parent / "config"
        if not config_dir.exists():
            config_dir = Path("config")  # fallback
        
        # 환경별 설정 파일 경로
        env_config_file = config_dir / f"{self.environment}.yaml"
        local_config_file = config_dir / "local.yaml"
        
        # 기본 설정부터 로드
        if env_config_file.exists():
            with open(env_config_file, 'r', encoding='utf-8') as f:
                config_data = yaml.safe_load(f)
                self._update_from_dict(config_data)
        
        # 로컬 설정으로 오버라이드 (개발자별 설정)
        if local_config_file.exists():
            with open(local_config_file, 'r', encoding='utf-8') as f:
                local_config = yaml.safe_load(f)
                self._update_from_dict(local_config)

    def _update_from_dict(self, config_data: Dict[str, Any]) -> None:
        """Update configuration from dictionary."""
        if not config_data:
            return
            
        for key, value in config_data.items():
            if hasattr(self, key):
                current_attr = getattr(self, key)
                if isinstance(current_attr, BaseModel):
                    # Pydantic 모델인 경우 업데이트
                    if isinstance(value, dict):
                        # 환경변수 치환 적용
                        substituted_value = self._substitute_env_vars_recursive(value)
                        updated_model = current_attr.__class__(**substituted_value)
                        setattr(self, key, updated_model)
                elif key == "database" and isinstance(value, dict):
                    # 데이터베이스 설정 특별 처리
                    db_configs = {}
                    for db_name, db_config in value.items():
                        if isinstance(db_config, dict):
                            # 환경변수 치환 적용
                            substituted_config = self._substitute_env_vars_recursive(db_config)
                            db_configs[db_name] = DatabaseConfig(**substituted_config)
                    setattr(self, key, db_configs)
                else:
                    # 단일 값에 대해서도 환경변수 치환 적용
                    substituted_value = self._substitute_env_vars_recursive(value)
                    setattr(self, key, substituted_value)

    def _substitute_env_vars(self, value: str) -> str:
        """환경변수 치환 (${VAR_NAME} 형식)."""
        if isinstance(value, str) and value.startswith("${") and value.endswith("}"):
            env_var_name = value[2:-1]
            return os.getenv(env_var_name, value)
        return value

    def _substitute_env_vars_recursive(self, obj: Any) -> Any:
        """재귀적으로 환경변수 치환."""
        if isinstance(obj, dict):
            return {k: self._substitute_env_vars_recursive(v) for k, v in obj.items()}
        elif isinstance(obj, list):
            return [self._substitute_env_vars_recursive(item) for item in obj]
        elif isinstance(obj, str):
            return self._substitute_env_vars(obj)
        else:
            return obj

    def _apply_env_vars_final(self) -> None:
        """환경변수를 최종적으로 적용하여 최고 우선순위 보장."""
        # CRM Database URL 환경변수 직접 적용
        crm_url_env = os.getenv("ANALYTICS_CRM_DATABASE_URL")
        if crm_url_env and "crm" in self.database:
            self.database["crm"].url = crm_url_env
        elif crm_url_env:
            # database dict이 없는 경우 생성
            if not hasattr(self, 'database') or not self.database:
                self.database = {}
            self.database["crm"] = DatabaseConfig(url=crm_url_env)
        
        # 다른 중요한 환경변수들도 최종 적용
        secret_key_env = os.getenv("ANALYTICS_SECRET_KEY")
        if secret_key_env:
            self.security.secret_key = secret_key_env

    # 호환성 프로퍼티
    @property
    def app_name(self) -> str:
        return self.app.name

    @property
    def app_version(self) -> str:
        return self.app.version

    @property
    def debug(self) -> bool:
        return self.app.debug

    @property
    def log_level(self) -> str:
        return self.app.log_level

    @property
    def host(self) -> str:
        return self.server.host

    @property
    def port(self) -> int:
        return self.server.port

    @property
    def workers(self) -> int:
        return self.server.workers

    @property
    def reload(self) -> bool:
        return self.server.reload

    @property
    def crm_database_url(self) -> str:
        if "crm" in self.database:
            url = self.database["crm"].url
            return self._substitute_env_vars(url)
        # 기본값을 올바른 자격증명으로 변경
        return "mysql+pymysql://swcamp:swcamp@localhost:3306/beautifly"

    @property
    def crm_pool_size(self) -> int:
        return self.database.get("crm", DatabaseConfig(url="", pool_size=5)).pool_size

    @property
    def crm_max_overflow(self) -> int:
        return self.database.get("crm", DatabaseConfig(url="", max_overflow=10)).max_overflow

    @property
    def analytics_db_path(self) -> str:
        from pathlib import Path
        import os
        
        # 프로젝트 루트 절대 경로 계산 (src/../.. = be15_DevEagles_DA)
        project_root = Path(__file__).parent.parent.parent.parent
        
        if "analytics" in self.database:
            db_url = self.database["analytics"].url
            # 상대경로인 경우 절대경로로 변환
            if not os.path.isabs(db_url):
                return str(project_root / db_url)
            return db_url
        
        # 기본값: 항상 프로젝트 루트의 data 폴더 사용
        return str(project_root / "data" / "analytics_local.db")

    @property
    def analytics_db_threads(self) -> int:
        if "analytics" in self.database:
            return self.database["analytics"].pool_size
        return 4

    @property
    def api_prefix(self) -> str:
        return self.api.prefix

    @property
    def docs_url(self) -> Optional[str]:
        return self.api.docs_url

    @property
    def redoc_url(self) -> Optional[str]:
        return self.api.redoc_url

    @property
    def openapi_url(self) -> Optional[str]:
        return self.api.openapi_url

    @property
    def secret_key(self) -> str:
        return self._substitute_env_vars(self.security.secret_key)

    @property
    def enable_metrics(self) -> bool:
        return self.monitoring.enable_metrics

    @property
    def metrics_path(self) -> str:
        return self.monitoring.metrics_path

    # ETL 설정 프로퍼티들
    @property
    def etl_batch_size(self) -> int:
        return self.etl.batch_size

    @property
    def etl_incremental(self) -> bool:
        return self.etl.incremental

    @property
    def etl_timestamp_file(self) -> str:
        from pathlib import Path
        import os
        
        file_path = self.etl.timestamp_file
        
        # 상대경로인 경우 절대경로로 변환
        if not os.path.isabs(file_path):
            project_root = Path(__file__).parent.parent.parent.parent
            return str(project_root / file_path)
        
        return file_path

    # ML 설정 프로퍼티들
    @property
    def model_storage_path(self) -> str:
        from pathlib import Path
        import os
        
        model_path = self.ml.model_storage_path
        
        # 상대경로인 경우 절대경로로 변환
        if not os.path.isabs(model_path):
            project_root = Path(__file__).parent.parent.parent.parent
            return str(project_root / model_path)
        
        return model_path

    @property
    def model_retrain_threshold(self) -> float:
        return self.ml.retrain_threshold

    @property
    def feature_columns(self) -> List[str]:
        return self.ml.feature_columns

    # 스케줄링 설정 프로퍼티들
    @property
    def scheduler_jobstore_url(self) -> str:
        from pathlib import Path
        import os
        
        url = self._substitute_env_vars(self.scheduling.jobstore_url)
        
        # SQLite URL인 경우 절대경로로 변환
        if url.startswith("sqlite:///"):
            db_path = url[10:]  # "sqlite:///" 제거
            if not os.path.isabs(db_path):
                project_root = Path(__file__).parent.parent.parent.parent
                abs_path = project_root / db_path
                return f"sqlite:///{abs_path}"
        
        return url

    @property
    def etl_schedule_hour(self) -> int:
        return self.scheduling.etl_schedule_hour

    @property
    def tagging_schedule_hour(self) -> int:
        return self.scheduling.tagging_schedule_hour

    @property
    def training_schedule_day(self) -> str:
        return self.scheduling.training_schedule_day

    @property
    def training_schedule_hour(self) -> int:
        return self.scheduling.training_schedule_hour

    # 고객 세분화 설정 프로퍼티들
    @property
    def new_customer_visit_threshold(self) -> int:
        return self.customer_segmentation.new_customer_visit_threshold

    @property
    def growing_customer_visit_threshold(self) -> int:
        return self.customer_segmentation.growing_customer_visit_threshold

    @property
    def vip_customer_amount_threshold(self) -> int:
        return self.customer_segmentation.vip_customer_amount_threshold

    # 위험 평가 설정 프로퍼티들
    @property
    def new_customer_followup_days(self) -> int:
        return self.risk_assessment.new_customer_followup_days

    @property
    def new_customer_risk_days(self) -> int:
        return self.risk_assessment.new_customer_risk_days

    @property
    def reactivation_needed_days(self) -> int:
        return self.risk_assessment.reactivation_needed_days

    @property
    def growing_customer_delay_multiplier(self) -> float:
        return self.risk_assessment.growing_customer_delay_multiplier

    @property
    def loyal_customer_delay_multiplier(self) -> float:
        return self.risk_assessment.loyal_customer_delay_multiplier

    # 보안 설정 프로퍼티들
    @property
    def access_token_expire_minutes(self) -> int:
        return self.security.access_token_expire_minutes

    @property
    def algorithm(self) -> str:
        return self.security.algorithm

    # 모니터링 설정 프로퍼티들
    @property
    def enable_tracing(self) -> bool:
        return self.monitoring.enable_tracing

    # 외부 서비스 설정 프로퍼티들
    @property
    def workflow_service_url(self) -> Optional[str]:
        return self._substitute_env_vars(self.external_services.workflow_service_url) if self.external_services.workflow_service_url else None

    @property
    def notification_webhook_url(self) -> Optional[str]:
        return self._substitute_env_vars(self.external_services.notification_webhook_url) if self.external_services.notification_webhook_url else None

    @property
    def be_api_url(self) -> str:
        """Get BE API URL."""
        return self._substitute_env_vars(self.external_services.be_api_url)

    @property
    def be_api_timeout(self) -> int:
        """Get BE API timeout."""
        return self.external_services.be_api_timeout


@lru_cache()
def get_settings() -> Settings:
    """Get cached application settings."""
    return Settings()


def clear_settings_cache():
    """Clear settings cache to reload configuration."""
    get_settings.cache_clear()


# Global settings instance
settings = get_settings() 