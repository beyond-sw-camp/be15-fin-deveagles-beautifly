"""Data extractors for CRM database."""

from datetime import datetime
from typing import Iterator, Optional, Dict, Any
import pandas as pd
from sqlalchemy import text

from analytics.core.database import get_crm_db
from .base import BaseExtractor


class CRMDataExtractor(BaseExtractor):
    """CRM 데이터베이스 기본 추출기."""
    
    def __init__(self, config=None):
        super().__init__(config)
        self.engine = get_crm_db()
    
    def execute_query(self, query: str, params: Dict[str, Any] = None) -> pd.DataFrame:
        """SQL 쿼리 실행."""
        try:
            with self.engine.connect() as conn:
                result = pd.read_sql(
                    text(query), 
                    conn, 
                    params=params or {},
                    chunksize=self.config.chunk_size
                )
                
                # chunksize가 설정된 경우 첫 번째 청크만 반환
                if hasattr(result, '__iter__'):
                    return next(result)
                return result
                
        except Exception as e:
            self.log_error("execute_query", e, query=query[:100])
            raise
    
    def execute_query_chunked(self, query: str, params: Dict[str, Any] = None) -> Iterator[pd.DataFrame]:
        """청크 단위로 SQL 쿼리 실행."""
        try:
            with self.engine.connect() as conn:
                chunks = pd.read_sql(
                    text(query),
                    conn,
                    params=params or {},
                    chunksize=self.config.chunk_size
                )
                
                for chunk in chunks:
                    yield chunk
                    
        except Exception as e:
            self.log_error("execute_query_chunked", e, query=query[:100])
            raise


class CustomerExtractor(CRMDataExtractor):
    """고객 데이터 추출기."""
    
    def extract(self, start_date: datetime = None, end_date: datetime = None) -> Iterator[pd.DataFrame]:
        """고객 데이터 추출."""
        base_query = """
        SELECT 
            id as customer_id,
            name,
            phone,
            email,
            birth_date,
            gender,
            address,
            memo,
            is_active,
            created_at,
            updated_at
        FROM customers
        WHERE is_active = 1
        """
        
        params = {}
        
        if start_date and end_date:
            # 증분 처리: 생성/수정된 고객만
            base_query += " AND (created_at >= :start_date OR updated_at >= :start_date)"
            base_query += " AND (created_at <= :end_date OR updated_at <= :end_date)"
            params.update({
                "start_date": start_date,
                "end_date": end_date
            })
        
        base_query += " ORDER BY id"
        
        self.log_start("customer_extract", start_date=start_date, end_date=end_date)
        
        return self.execute_query_chunked(base_query, params)
    
    def get_last_update_time(self) -> Optional[datetime]:
        """고객 테이블의 마지막 업데이트 시간."""
        query = "SELECT MAX(updated_at) as last_update FROM customers"
        result = self.execute_query(query)
        
        if not result.empty and result.iloc[0]['last_update']:
            return result.iloc[0]['last_update']
        return None


class VisitExtractor(CRMDataExtractor):
    """방문 데이터 추출기."""
    
    def extract(self, start_date: datetime = None, end_date: datetime = None) -> Iterator[pd.DataFrame]:
        """방문 데이터 추출 (서비스 정보 포함)."""
        base_query = """
        SELECT 
            v.id as visit_id,
            v.customer_id,
            v.employee_id,
            v.visit_date,
            v.total_amount,
            v.discount_amount,
            v.final_amount,
            v.payment_method,
            v.memo as visit_memo,
            v.status,
            v.created_at,
            v.updated_at,
            -- 서비스 정보 (JSON 집계)
            COUNT(vs.id) as service_count,
            GROUP_CONCAT(DISTINCT s.category) as service_categories,
            GROUP_CONCAT(s.name) as service_names,
            SUM(vs.quantity) as total_service_quantity
        FROM visits v
        LEFT JOIN visit_services vs ON v.id = vs.visit_id
        LEFT JOIN services s ON vs.service_id = s.id
        WHERE v.status = 'completed'
        """
        
        params = {}
        
        if start_date and end_date:
            base_query += " AND v.visit_date >= :start_date AND v.visit_date <= :end_date"
            params.update({
                "start_date": start_date,
                "end_date": end_date
            })
        
        base_query += """
        GROUP BY v.id, v.customer_id, v.employee_id, v.visit_date, 
                 v.total_amount, v.discount_amount, v.final_amount,
                 v.payment_method, v.memo, v.status, v.created_at, v.updated_at
        ORDER BY v.visit_date, v.id
        """
        
        self.log_start("visit_extract", start_date=start_date, end_date=end_date)
        
        return self.execute_query_chunked(base_query, params)
    
    def get_last_update_time(self) -> Optional[datetime]:
        """방문 테이블의 마지막 업데이트 시간."""
        query = """
        SELECT MAX(GREATEST(v.updated_at, COALESCE(vs.updated_at, v.updated_at))) as last_update
        FROM visits v
        LEFT JOIN visit_services vs ON v.id = vs.visit_id
        """
        result = self.execute_query(query)
        
        if not result.empty and result.iloc[0]['last_update']:
            return result.iloc[0]['last_update']
        return None


class ServiceExtractor(CRMDataExtractor):
    """서비스 데이터 추출기."""
    
    def extract(self, start_date: datetime = None, end_date: datetime = None) -> Iterator[pd.DataFrame]:
        """서비스 데이터 추출."""
        base_query = """
        SELECT 
            id as service_id,
            name,
            category,
            price,
            duration_minutes,
            description,
            is_active,
            created_at,
            updated_at
        FROM services
        WHERE is_active = 1
        """
        
        params = {}
        
        if start_date and end_date:
            base_query += " AND (created_at >= :start_date OR updated_at >= :start_date)"
            base_query += " AND (created_at <= :end_date OR updated_at <= :end_date)"
            params.update({
                "start_date": start_date,
                "end_date": end_date
            })
        
        base_query += " ORDER BY id"
        
        self.log_start("service_extract", start_date=start_date, end_date=end_date)
        
        return self.execute_query_chunked(base_query, params)
    
    def get_last_update_time(self) -> Optional[datetime]:
        """서비스 테이블의 마지막 업데이트 시간."""
        query = "SELECT MAX(updated_at) as last_update FROM services"
        result = self.execute_query(query)
        
        if not result.empty and result.iloc[0]['last_update']:
            return result.iloc[0]['last_update']
        return None


class VisitServiceExtractor(CRMDataExtractor):
    """방문-서비스 상세 데이터 추출기."""
    
    def extract(self, start_date: datetime = None, end_date: datetime = None) -> Iterator[pd.DataFrame]:
        """방문별 서비스 상세 데이터 추출."""
        base_query = """
        SELECT 
            vs.id as visit_service_id,
            vs.visit_id,
            vs.service_id,
            vs.employee_id,
            vs.quantity,
            vs.unit_price,
            vs.total_price,
            vs.discount_amount,
            vs.final_price,
            vs.memo as service_memo,
            vs.created_at,
            vs.updated_at,
            -- 방문 정보
            v.customer_id,
            v.visit_date,
            -- 서비스 정보
            s.name as service_name,
            s.category as service_category
        FROM visit_services vs
        INNER JOIN visits v ON vs.visit_id = v.id
        INNER JOIN services s ON vs.service_id = s.id
        WHERE v.status = 'completed'
        """
        
        params = {}
        
        if start_date and end_date:
            base_query += " AND v.visit_date >= :start_date AND v.visit_date <= :end_date"
            params.update({
                "start_date": start_date,
                "end_date": end_date
            })
        
        base_query += " ORDER BY v.visit_date, vs.visit_id, vs.id"
        
        self.log_start("visit_service_extract", start_date=start_date, end_date=end_date)
        
        return self.execute_query_chunked(base_query, params)
    
    def get_last_update_time(self) -> Optional[datetime]:
        """방문-서비스 테이블의 마지막 업데이트 시간."""
        query = """
        SELECT MAX(vs.updated_at) as last_update
        FROM visit_services vs
        INNER JOIN visits v ON vs.visit_id = v.id
        WHERE v.status = 'completed'
        """
        result = self.execute_query(query)
        
        if not result.empty and result.iloc[0]['last_update']:
            return result.iloc[0]['last_update']
        return None 