"""Base model classes and common fields."""

from datetime import datetime
from typing import Optional

from pydantic import BaseModel, Field
from sqlalchemy import Column, DateTime, Integer
from sqlalchemy.ext.declarative import declarative_base

# SQLAlchemy Base for CRM models
Base = declarative_base()


class TimestampMixin:
    """Mixin for timestamp fields."""
    created_at = Column(DateTime, default=datetime.utcnow, nullable=False)
    updated_at = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow, nullable=False)


class BaseSchema(BaseModel):
    """Base Pydantic schema with common configuration."""
    
    class Config:
        from_attributes = True
        arbitrary_types_allowed = True
        json_encoders = {
            datetime: lambda v: v.isoformat()
        }


class CustomerSegment(BaseModel):
    """Customer segment enumeration."""
    NEW = "new"
    GROWING = "growing" 
    LOYAL = "loyal"
    VIP = "vip"
    INACTIVE = "inactive"


class RiskTag(BaseModel):
    """Risk assessment tags."""
    FIRST_VISIT_FOLLOW_UP = "first_visit_follow_up"
    PATTERN_BREAK_DETECTED = "pattern_break_detected"
    REACTIVATION_NEEDED = "reactivation_needed"
    CHURN_RISK_HIGH = "churn_risk_high"
    CHURN_RISK_MEDIUM = "churn_risk_medium"
    VIP_ATTENTION_NEEDED = "vip_attention_needed"


class PaginationParams(BaseModel):
    """Pagination parameters for API responses."""
    page: int = Field(default=1, ge=1)
    size: int = Field(default=20, ge=1, le=100)
    
    @property
    def offset(self) -> int:
        return (self.page - 1) * self.size 