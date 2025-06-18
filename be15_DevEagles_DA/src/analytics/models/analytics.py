"""Analytics data models for DuckDB."""

from datetime import datetime, date
from decimal import Decimal
from typing import List, Optional, Dict, Any

from pydantic import BaseModel, Field, computed_field

from .base import BaseSchema, CustomerSegment


class CustomerAnalytics(BaseSchema):
    """고객 분석 데이터 모델."""
    
    customer_id: int
    name: str
    phone: str
    email: Optional[str] = None
    birth_date: Optional[date] = None
    gender: Optional[str] = None
    
    # 계산된 분석 필드
    first_visit_date: Optional[datetime] = None
    last_visit_date: Optional[datetime] = None
    total_visits: int = 0
    total_amount: Decimal = Field(default=Decimal("0.00"))
    avg_visit_amount: Decimal = Field(default=Decimal("0.00"))
    
    # 라이프사이클 관련
    lifecycle_days: int = 0  # 첫 방문부터 현재까지 일수
    days_since_last_visit: Optional[int] = None
    
    # 행동 패턴
    visit_frequency: float = 0.0  # 월평균 방문 횟수
    preferred_services: List[str] = Field(default_factory=list)
    
    # 최근 3개월 활동
    visits_3m: int = 0
    amount_3m: Decimal = Field(default=Decimal("0.00"))
    
    # 세그먼트 정보
    segment: str = CustomerSegment.NEW
    segment_updated_at: datetime = Field(default_factory=datetime.utcnow)
    
    # 리스크 점수
    churn_risk_score: float = 0.0
    churn_risk_level: str = "low"  # low, medium, high
    
    @computed_field
    @property
    def age(self) -> Optional[int]:
        """나이 계산."""
        if self.birth_date:
            today = date.today()
            return today.year - self.birth_date.year - (
                (today.month, today.day) < (self.birth_date.month, self.birth_date.day)
            )
        return None
    
    @computed_field
    @property
    def is_new_customer(self) -> bool:
        """신규 고객 여부."""
        return self.segment == CustomerSegment.NEW
    
    @computed_field
    @property
    def is_at_risk(self) -> bool:
        """위험 고객 여부."""
        return self.churn_risk_level in ["medium", "high"]


class CustomerServicePreference(BaseSchema):
    """고객 선호 시술 분석 모델."""
    
    customer_id: int
    service_id: int
    service_name: str
    service_category: Optional[str] = None
    
    # 이용 통계
    total_visits: int = 0  # 해당 서비스 총 이용 횟수
    total_amount: Decimal = Field(default=Decimal("0.00"))  # 해당 서비스 총 금액
    avg_amount: Decimal = Field(default=Decimal("0.00"))  # 해당 서비스 평균 금액
    
    # 시간 패턴
    first_service_date: Optional[datetime] = None
    last_service_date: Optional[datetime] = None
    
    # 선호도 분석
    preference_rank: int = 1  # 고객 내 선호도 순위 (1위, 2위, 3위...)
    visit_ratio: float = 0.0  # 전체 방문 중 해당 서비스 비율
    amount_ratio: float = 0.0  # 전체 지출 중 해당 서비스 비율
    
    # 최근 활동
    recent_visits_3m: int = 0  # 최근 3개월 이용 횟수
    days_since_last_service: Optional[int] = None
    
    @computed_field
    @property
    def is_frequent_service(self) -> bool:
        """자주 이용하는 서비스 여부 (상위 3개 서비스)."""
        return self.preference_rank <= 3
    
    @computed_field
    @property
    def is_high_value_service(self) -> bool:
        """고가치 서비스 여부 (금액 비율 20% 이상)."""
        return self.amount_ratio >= 0.2


class CustomerServiceTags(BaseSchema):
    """고객 선호 시술 태그 모델."""
    
    customer_id: int
    
    # 선호 시술 태그 (상위 3개)
    top_service_1: Optional[str] = None
    top_service_2: Optional[str] = None  
    top_service_3: Optional[str] = None
    
    # 선호 카테고리 태그
    preferred_categories: List[str] = Field(default_factory=list)
    
    # 시술 패턴 태그
    service_variety_score: float = 0.0  # 서비스 다양성 점수 (0-1)
    loyalty_services: List[str] = Field(default_factory=list)  # 꾸준히 이용하는 서비스
    
    # 가격대 선호도
    avg_service_price: Decimal = Field(default=Decimal("0.00"))
    preferred_price_range: str = "medium"  # low, medium, high
    
    # 업데이트 정보
    updated_at: datetime = Field(default_factory=datetime.utcnow)
    
    @computed_field
    @property
    def service_tags(self) -> List[str]:
        """선호 서비스 태그 리스트."""
        tags = []
        if self.top_service_1:
            tags.append(f"선호_{self.top_service_1}")
        if self.top_service_2:
            tags.append(f"선호_{self.top_service_2}")
        if self.top_service_3:
            tags.append(f"선호_{self.top_service_3}")
        return tags
    
    @computed_field
    @property
    def category_tags(self) -> List[str]:
        """선호 카테고리 태그 리스트."""
        return [f"카테고리_{cat}" for cat in self.preferred_categories]
    
    @computed_field
    @property
    def all_preference_tags(self) -> List[str]:
        """모든 선호도 태그 통합."""
        tags = self.service_tags + self.category_tags
        
        # 다양성 태그
        if self.service_variety_score >= 0.7:
            tags.append("다양한_시술_선호")
        elif self.service_variety_score <= 0.3:
            tags.append("특정_시술_집중")
            
        # 가격대 태그
        tags.append(f"{self.preferred_price_range}_가격대_선호")
        
        return tags


class VisitAnalytics(BaseSchema):
    """방문 분석 데이터 모델."""
    
    visit_id: int
    customer_id: int
    employee_id: Optional[int] = None
    visit_date: datetime
    
    # 금액 정보
    total_amount: Decimal
    discount_amount: Decimal = Field(default=Decimal("0.00"))
    final_amount: Decimal
    
    # 서비스 정보
    service_count: int = 0
    service_categories: List[str] = Field(default_factory=list)
    service_names: List[str] = Field(default_factory=list)
    
    # 고객 방문 패턴 분석
    is_first_visit: bool = False
    days_since_previous_visit: Optional[int] = None
    visit_sequence: int = 1  # 해당 고객의 n번째 방문
    
    # 시간대/요일 분석
    visit_hour: int = Field(default=0)
    visit_weekday: int = Field(default=0)  # 0=월요일
    visit_month: int = Field(default=1)


class ServiceAnalytics(BaseSchema):
    """서비스 분석 데이터 모델."""
    
    service_id: int
    name: str
    category: Optional[str] = None
    base_price: Decimal
    
    # 사용 통계
    total_bookings: int = 0
    total_revenue: Decimal = Field(default=Decimal("0.00"))
    avg_price: Decimal = Field(default=Decimal("0.00"))
    
    # 고객 분석
    unique_customers: int = 0
    repeat_customers: int = 0
    repeat_rate: float = 0.0
    
    # 시간 분석
    popular_time_slots: List[int] = Field(default_factory=list)


class CustomerRiskAssessment(BaseSchema):
    """고객 리스크 평가 결과."""
    
    customer_id: int
    assessment_date: datetime = Field(default_factory=datetime.utcnow)
    
    # 리스크 점수 (0-100)
    churn_risk_score: float = 0.0
    engagement_score: float = 0.0
    value_score: float = 0.0
    
    # 리스크 요인
    risk_factors: Dict[str, Any] = Field(default_factory=dict)
    
    # 권장 액션
    recommended_tags: List[str] = Field(default_factory=list)
    recommended_actions: List[str] = Field(default_factory=list)
    
    # 예측 정보
    predicted_next_visit: Optional[datetime] = None
    predicted_ltv: Optional[Decimal] = None


class BusinessMetrics(BaseSchema):
    """비즈니스 메트릭 모델."""
    
    date: date
    
    # 고객 메트릭
    total_customers: int = 0
    new_customers: int = 0
    returning_customers: int = 0
    churned_customers: int = 0
    
    # 방문 메트릭  
    total_visits: int = 0
    avg_visits_per_customer: float = 0.0
    
    # 매출 메트릭
    total_revenue: Decimal = Field(default=Decimal("0.00"))
    avg_revenue_per_visit: Decimal = Field(default=Decimal("0.00"))
    avg_revenue_per_customer: Decimal = Field(default=Decimal("0.00"))
    
    # 세그먼트별 분포
    segment_distribution: Dict[str, int] = Field(default_factory=dict)
    
    # 리스크 분포
    risk_distribution: Dict[str, int] = Field(default_factory=dict)


class ETLMetadata(BaseSchema):
    """ETL 처리 메타데이터."""
    
    table_name: str
    last_updated: datetime = Field(default_factory=datetime.utcnow)
    records_processed: int = 0
    records_inserted: int = 0
    records_updated: int = 0
    records_deleted: int = 0
    processing_time_seconds: float = 0.0
    status: str = "completed"  # running, completed, failed
    error_message: Optional[str] = None 