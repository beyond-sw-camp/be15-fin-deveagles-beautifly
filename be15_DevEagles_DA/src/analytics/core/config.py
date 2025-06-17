"""Application configuration management."""

from functools import lru_cache
from typing import Optional

from pydantic import Field
from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    """Application settings with environment variable support."""

    model_config = SettingsConfigDict(
        env_file=".env",
        env_file_encoding="utf-8",
        case_sensitive=False,
        extra="ignore",
    )

    # Application
    app_name: str = Field(default="DevEagles Analytics", description="Application name")
    app_version: str = Field(default="0.1.0", description="Application version")
    debug: bool = Field(default=False, description="Debug mode")
    log_level: str = Field(default="INFO", description="Logging level")

    # Server
    host: str = Field(default="0.0.0.0", description="Server host")
    port: int = Field(default=8000, description="Server port")
    workers: int = Field(default=1, description="Number of worker processes")
    reload: bool = Field(default=False, description="Auto-reload on code changes")

    # Database - CRM (Source)
    crm_database_url: str = Field(
        default="mysql://readonly_user:password@localhost:3306/crm",
        description="CRM database connection URL",
    )
    crm_pool_size: int = Field(default=5, description="CRM DB connection pool size")
    crm_max_overflow: int = Field(default=10, description="CRM DB max overflow")

    # Database - Analytics (DuckDB)
    analytics_db_path: str = Field(
        default="data/analytics.db", description="DuckDB database file path"
    )
    analytics_db_threads: int = Field(
        default=4, description="DuckDB number of threads"
    )

    # ETL Settings
    etl_batch_size: int = Field(default=10000, description="ETL batch size")
    etl_incremental: bool = Field(
        default=True, description="Use incremental ETL processing"
    )
    etl_timestamp_file: str = Field(
        default="data/last_etl.txt", description="ETL timestamp tracking file"
    )

    # ML Model Settings
    model_storage_path: str = Field(
        default="models", description="Model storage directory"
    )
    model_retrain_threshold: float = Field(
        default=0.05, description="Model performance degradation threshold"
    )
    feature_columns: list[str] = Field(
        default=[
            "age",
            "frequency",
            "avg_monetary",
            "lifecycle_days",
            "service_variety",
            "employee_variety",
            "visits_3m",
        ],
        description="Default feature columns for ML models",
    )

    # Scheduling
    scheduler_jobstore_url: str = Field(
        default="sqlite:///data/scheduler.db",
        description="APScheduler jobstore URL",
    )
    etl_schedule_hour: int = Field(default=2, description="Daily ETL schedule hour")
    tagging_schedule_hour: int = Field(
        default=3, description="Daily tagging schedule hour"
    )
    training_schedule_day: str = Field(
        default="sun", description="Weekly training schedule day"
    )
    training_schedule_hour: int = Field(
        default=4, description="Weekly training schedule hour"
    )

    # Customer Segmentation
    new_customer_visit_threshold: int = Field(
        default=3, description="Max visits to be considered new customer"
    )
    growing_customer_visit_threshold: int = Field(
        default=10, description="Max visits to be considered growing customer"
    )
    vip_customer_amount_threshold: int = Field(
        default=100000, description="Min amount to be considered VIP customer"
    )

    # Risk Assessment
    new_customer_followup_days: int = Field(
        default=7, description="Days after first visit for followup"
    )
    new_customer_risk_days: int = Field(
        default=20, description="Days to consider new customer at risk"
    )
    reactivation_needed_days: int = Field(
        default=30, description="Days to consider reactivation needed"
    )
    growing_customer_delay_multiplier: float = Field(
        default=1.5, description="Multiplier for growing customer expected return"
    )
    loyal_customer_delay_multiplier: float = Field(
        default=2.0, description="Multiplier for loyal customer expected return"
    )

    # API Settings
    api_prefix: str = Field(default="/api/v1", description="API route prefix")
    docs_url: Optional[str] = Field(default="/docs", description="Swagger UI URL")
    redoc_url: Optional[str] = Field(default="/redoc", description="ReDoc URL")
    openapi_url: Optional[str] = Field(
        default="/openapi.json", description="OpenAPI schema URL"
    )

    # Security
    secret_key: str = Field(
        default="your-secret-key-change-in-production",
        description="Secret key for JWT tokens",
    )
    access_token_expire_minutes: int = Field(
        default=30, description="Access token expiration time"
    )
    algorithm: str = Field(default="HS256", description="JWT algorithm")

    # Monitoring
    enable_metrics: bool = Field(default=True, description="Enable Prometheus metrics")
    metrics_path: str = Field(default="/metrics", description="Metrics endpoint path")
    enable_tracing: bool = Field(default=False, description="Enable request tracing")

    # External Services
    workflow_service_url: Optional[str] = Field(
        default=None, description="Workflow service URL for notifications"
    )
    notification_webhook_url: Optional[str] = Field(
        default=None, description="Webhook URL for notifications"
    )

    class Config:
        """Pydantic configuration."""

        env_prefix = "ANALYTICS_"


@lru_cache()
def get_settings() -> Settings:
    """Get cached application settings."""
    return Settings()


# Global settings instance
settings = get_settings() 