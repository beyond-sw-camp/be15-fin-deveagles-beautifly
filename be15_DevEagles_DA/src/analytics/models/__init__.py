"""Domain models and data structures."""

from .base import (
    Base,
    TimestampMixin,
    BaseSchema,
    CustomerSegment,
    RiskTag,
    PaginationParams,
)

from .crm import (
    Customer,
    Employee,
    Service,
    Visit,
    VisitService,
    CustomerTag,
)

from .analytics import (
    CustomerAnalytics,
    CustomerServicePreference,
    CustomerServiceTags,
    VisitAnalytics,
    ServiceAnalytics,
    CustomerRiskAssessment,
    BusinessMetrics,
    ETLMetadata,
)

__all__ = [
    # Base models
    "Base",
    "TimestampMixin", 
    "BaseSchema",
    "CustomerSegment",
    "RiskTag",
    "PaginationParams",
    
    # CRM models
    "Customer",
    "Employee",
    "Service",
    "Visit",
    "VisitService",
    "CustomerTag",
    
    # Analytics models
    "CustomerAnalytics",
    "CustomerServicePreference",
    "CustomerServiceTags",
    "VisitAnalytics",
    "ServiceAnalytics", 
    "CustomerRiskAssessment",
    "BusinessMetrics",
    "ETLMetadata",
] 