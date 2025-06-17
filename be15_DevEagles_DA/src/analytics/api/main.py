"""FastAPI main application."""

from contextlib import asynccontextmanager

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from prometheus_fastapi_instrumentator import Instrumentator

from analytics.api.routes.health import router as health_router
from analytics.core.config import settings
from analytics.core.database import db_manager
from analytics.core.logging import get_logger

logger = get_logger("api")


@asynccontextmanager
async def lifespan(app: FastAPI):
    """Application lifespan manager."""
    logger.info(f"Starting {settings.app_name} v{settings.app_version}")
    
    # Startup
    try:
        # Test analytics database connection (DuckDB)
        analytics_conn = db_manager.get_analytics_connection()
        analytics_conn.execute("SELECT 1").fetchone()
        logger.info("Analytics database connection established successfully")
        
        # CRM database는 나중에 연결 (일단 skip)
        # crm_engine = db_manager.get_crm_engine()
        # with crm_engine.connect() as conn:
        #     conn.execute("SELECT 1")
        
    except Exception as e:
        logger.warning(f"Database connection warning: {e}")
        # 개발 단계에서는 DB 연결 실패해도 서버 시작하도록 함
    
    yield
    
    # Shutdown
    logger.info("Shutting down application")
    db_manager.close_connections()


# Create FastAPI application
app = FastAPI(
    title=settings.app_name,
    version=settings.app_version,
    description="Customer retention analytics service for beauty salon CRM",
    docs_url=settings.docs_url,
    redoc_url=settings.redoc_url,
    openapi_url=settings.openapi_url,
    lifespan=lifespan,
)

# Add CORS middleware
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # Configure properly for production
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Add Prometheus metrics
if settings.enable_metrics:
    instrumentator = Instrumentator()
    instrumentator.instrument(app).expose(app, endpoint=settings.metrics_path)

# Include routers
app.include_router(health_router, prefix=settings.api_prefix, tags=["Health"])

# Root endpoint
@app.get("/")
async def root():
    """Root endpoint with basic service information."""
    return {
        "service": settings.app_name,
        "version": settings.app_version,
        "status": "running",
        "docs": settings.docs_url,
    } 