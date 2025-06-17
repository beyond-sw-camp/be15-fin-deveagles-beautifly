"""Health check API endpoints."""

from datetime import datetime
from typing import Dict, Any

from fastapi import APIRouter, HTTPException, Depends
from pydantic import BaseModel

from analytics.core.config import settings
from analytics.core.database import get_crm_db, get_analytics_db
from analytics.core.logging import get_logger

logger = get_logger("health")

router = APIRouter()


class HealthResponse(BaseModel):
    """Health check response model."""
    status: str
    timestamp: datetime
    version: str
    database: Dict[str, Any]
    dependencies: Dict[str, Any]


@router.get("/health", response_model=HealthResponse)
async def health_check():
    """
    Comprehensive health check endpoint.
    
    Returns:
        HealthResponse: Service health status and dependencies
    """
    try:
        # Test CRM database connection
        crm_engine = get_crm_db()
        crm_status = "unknown"
        crm_error = None
        
        try:
            with crm_engine.connect() as conn:
                conn.execute("SELECT 1")
            crm_status = "healthy"
        except Exception as e:
            crm_status = "unhealthy"
            crm_error = str(e)
            logger.warning(f"CRM database health check failed: {e}")
        
        # Test Analytics database connection
        analytics_conn = get_analytics_db()
        analytics_status = "unknown"
        analytics_error = None
        
        try:
            analytics_conn.execute("SELECT 1").fetchone()
            analytics_status = "healthy"
        except Exception as e:
            analytics_status = "unhealthy"
            analytics_error = str(e)
            logger.warning(f"Analytics database health check failed: {e}")
        
        # Determine overall status
        overall_status = "healthy"
        if crm_status == "unhealthy" or analytics_status == "unhealthy":
            overall_status = "degraded"
        
        response = HealthResponse(
            status=overall_status,
            timestamp=datetime.utcnow(),
            version=settings.app_version,
            database={
                "crm": {
                    "status": crm_status,
                    "type": "MariaDB",
                    "error": crm_error
                },
                "analytics": {
                    "status": analytics_status,
                    "type": "DuckDB",
                    "path": settings.analytics_db_path,
                    "error": analytics_error
                }
            },
            dependencies={
                "prometheus_metrics": settings.enable_metrics,
                "debug_mode": settings.debug,
                "log_level": settings.log_level
            }
        )
        
        if overall_status == "degraded":
            logger.warning("Health check shows degraded status")
        
        return response
        
    except Exception as e:
        logger.error(f"Health check failed: {e}")
        raise HTTPException(
            status_code=503,
            detail=f"Health check failed: {str(e)}"
        )


@router.get("/ready")
async def readiness_check():
    """
    Kubernetes readiness probe endpoint.
    
    Returns:
        dict: Simple ready status
    """
    try:
        # Quick database connectivity check
        crm_engine = get_crm_db()
        analytics_conn = get_analytics_db()
        
        # Test connections
        with crm_engine.connect() as conn:
            conn.execute("SELECT 1")
        analytics_conn.execute("SELECT 1").fetchone()
        
        return {"status": "ready"}
        
    except Exception as e:
        logger.error(f"Readiness check failed: {e}")
        raise HTTPException(
            status_code=503,
            detail="Service not ready"
        )


@router.get("/live")
async def liveness_check():
    """
    Kubernetes liveness probe endpoint.
    
    Returns:
        dict: Simple alive status
    """
    return {
        "status": "alive",
        "timestamp": datetime.utcnow(),
        "service": settings.app_name
    } 