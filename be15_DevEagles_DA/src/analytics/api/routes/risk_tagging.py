"""Risk Tagging API Routes."""

from typing import List, Optional
from fastapi import APIRouter, HTTPException, Query, Depends
from pydantic import BaseModel, Field

from analytics.services.risk_tagging import CustomerRiskTaggingService
from analytics.models.base import BaseSchema, PaginationParams
from analytics.core.logging import get_logger

logger = get_logger(__name__)

router = APIRouter()


class RiskAnalysisResponse(BaseSchema):
    """고객 위험도 분석 응답."""
    customer_id: int
    risk_score: float
    risk_level: str
    risk_factors: dict
    recommended_segments: List[str]
    recommended_actions: List[str]
    assessment_date: str


class HighRiskCustomerResponse(BaseSchema):
    """고위험 고객 응답."""
    customer_id: int
    name: str
    phone: Optional[str] = None
    email: Optional[str] = None
    churn_risk_score: float
    churn_risk_level: str
    total_visits: int
    total_amount: float
    days_since_last_visit: int
    segment: str
    last_visit_date: Optional[str] = None
    urgency_score: float


class RiskSegmentResponse(BaseSchema):
    """위험 세그먼트 응답."""
    segment_tag: str
    segment_title: str
    priority: int
    expires_at: str


class RiskDistributionResponse(BaseSchema):
    """위험 분포 응답."""
    distribution: dict
    total_customers: int


class BatchSegmentingRequest(BaseModel):
    """배치 세그먼트 요청."""
    dry_run: bool = Field(default=False, description="실제 세그먼트 없이 시뮬레이션만 실행")


class BatchSegmentingResponse(BaseSchema):
    """배치 세그먼트 응답."""
    total_customers: int
    segmented_customers: int
    high_risk_customers: int
    medium_risk_customers: int
    low_risk_customers: int
    segments_created: int
    errors: List[str]


def get_risk_tagging_service() -> CustomerRiskTaggingService:
    """위험 태깅 서비스 의존성 주입."""
    return CustomerRiskTaggingService()


@router.post("/customers/{customer_id}/risk-analysis", response_model=RiskAnalysisResponse)
async def analyze_customer_risk(
    customer_id: int,
    service: CustomerRiskTaggingService = Depends(get_risk_tagging_service)
):
    """고객 위험도 분석."""
    try:
        logger.info(f"고객 위험도 분석 요청: customer_id={customer_id}")
        
        result = service.analyze_customer_risk(customer_id)
        
        if 'error' in result:
            raise HTTPException(status_code=404, detail=result['error'])
        
        return RiskAnalysisResponse(
            customer_id=customer_id,
            risk_score=result['risk_score'],
            risk_level=result['risk_level'],
            risk_factors=result['risk_factors'],
            recommended_segments=result['recommended_segments'],
            recommended_actions=result['recommended_actions'],
            assessment_date=result['assessment_date']
        )
        
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"고객 위험도 분석 실패: customer_id={customer_id}, error={e}")
        raise HTTPException(status_code=500, detail="분석 서버 오류")


@router.get("/customers/high-risk", response_model=List[HighRiskCustomerResponse])
async def get_high_risk_customers(
    limit: int = Query(default=50, ge=1, le=200, description="조회할 최대 고객 수"),
    service: CustomerRiskTaggingService = Depends(get_risk_tagging_service)
):
    """고위험 고객 목록 조회."""
    try:
        logger.info(f"고위험 고객 목록 조회: limit={limit}")
        
        customers = service.get_high_risk_customers(limit)
        
        return [
            HighRiskCustomerResponse(
                customer_id=customer['customer_id'],
                name=customer['name'],
                phone=customer.get('phone'),
                email=customer.get('email'),
                churn_risk_score=customer['churn_risk_score'],
                churn_risk_level=customer['churn_risk_level'],
                total_visits=customer['total_visits'],
                total_amount=customer['total_amount'],
                days_since_last_visit=customer['days_since_last_visit'],
                segment=customer.get('segment', ''),
                last_visit_date=customer.get('last_visit_date'),
                urgency_score=customer['urgency_score']
            )
            for customer in customers
        ]
        
    except Exception as e:
        logger.error(f"고위험 고객 목록 조회 실패: error={e}")
        raise HTTPException(status_code=500, detail="고위험 고객 조회 실패")


@router.get("/customers/{customer_id}/risk-segments", response_model=List[RiskSegmentResponse])
async def get_customer_risk_segments(
    customer_id: int,
    service: CustomerRiskTaggingService = Depends(get_risk_tagging_service)
):
    """고객 위험 세그먼트 조회."""
    try:
        logger.info(f"고객 위험 세그먼트 조회: customer_id={customer_id}")
        
        segments = service.get_customer_risk_segments(customer_id)
        
        return [
            RiskSegmentResponse(
                segment_tag=segment['segment_tag'],
                segment_title=segment['segment_title'],
                priority=segment['priority'],
                expires_at=segment['expires_at']
            )
            for segment in segments
        ]
        
    except Exception as e:
        logger.error(f"고객 위험 세그먼트 조회 실패: customer_id={customer_id}, error={e}")
        raise HTTPException(status_code=500, detail="세그먼트 조회 실패")


@router.post("/customers/batch-risk-segmenting", response_model=BatchSegmentingResponse)
async def batch_risk_segmenting(
    request: BatchSegmentingRequest,
    service: CustomerRiskTaggingService = Depends(get_risk_tagging_service)
):
    """배치 위험 세그먼트 처리."""
    try:
        logger.info(f"배치 위험 세그먼트 처리: dry_run={request.dry_run}")
        
        result = service.batch_segment_all_customers(request.dry_run)
        
        return BatchSegmentingResponse(
            total_customers=result['total_customers'],
            segmented_customers=result['segmented_customers'],
            high_risk_customers=result['high_risk_customers'],
            medium_risk_customers=result['medium_risk_customers'],
            low_risk_customers=result['low_risk_customers'],
            segments_created=result['segments_created'],
            errors=result['errors']
        )
        
    except Exception as e:
        logger.error(f"배치 위험 세그먼트 처리 실패: error={e}")
        raise HTTPException(status_code=500, detail="배치 세그먼트 처리 실패")


@router.get("/risk-distribution", response_model=RiskDistributionResponse)
async def get_risk_distribution(
    service: CustomerRiskTaggingService = Depends(get_risk_tagging_service)
):
    """위험 분포 조회."""
    try:
        logger.info("위험 분포 조회")
        
        result = service.get_risk_distribution()
        
        return RiskDistributionResponse(
            distribution=result['distribution'],
            total_customers=result['total_customers']
        )
        
    except Exception as e:
        logger.error(f"위험 분포 조회 실패: error={e}")
        raise HTTPException(status_code=500, detail="위험 분포 조회 실패")


@router.get("/risk-trends")
async def get_risk_trends(
    days: int = Query(default=30, ge=1, le=365, description="분석할 일수"),
    service: CustomerRiskTaggingService = Depends(get_risk_tagging_service)
):
    """위험 트렌드 조회."""
    try:
        logger.info(f"위험 트렌드 조회: days={days}")
        
        result = service.get_risk_trends(days)
        
        return result
        
    except Exception as e:
        logger.error(f"위험 트렌드 조회 실패: error={e}")
        raise HTTPException(status_code=500, detail="위험 트렌드 조회 실패")