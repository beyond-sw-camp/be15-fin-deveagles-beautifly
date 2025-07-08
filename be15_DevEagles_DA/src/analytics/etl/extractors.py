"""Data extractors for CRM database."""

from datetime import datetime
from typing import Iterator, Optional, Dict, Any
import pandas as pd
from sqlalchemy import text

from analytics.core.database import get_crm_db
from .base_extractors import BaseDataExtractor


class CRMDataExtractor(BaseDataExtractor):
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


class CustomerExtractor(BaseDataExtractor):
    """고객 데이터 추출기."""
    
    def extract(self, start_date: datetime = None, end_date: datetime = None) -> Iterator[pd.DataFrame]:
        """고객 데이터 추출."""
        query = """
        SELECT 
            c.id as customer_id,
            c.name,
            c.phone,
            c.email,
            c.birth_date,
            c.gender,
            c.address,
            c.memo,
            c.is_active,
            c.created_at,
            c.updated_at
        FROM customers c
        WHERE c.is_active = true
        """

        params = {}
        if start_date:
            query += " AND c.updated_at >= :start_date"
            params['start_date'] = start_date
        if end_date:
            query += " AND c.updated_at <= :end_date"
            params['end_date'] = end_date

        return self.execute_query_chunked(query, params)
    
    def get_last_update_time(self) -> Optional[datetime]:
        """마지막 고객 데이터 업데이트 시간 조회."""
        query = """
        SELECT MAX(updated_at) as last_update
        FROM customers
        WHERE is_active = true
        """
        result = self.execute_query(query)
        return result.iloc[0]['last_update'] if not result.empty else None


class VisitExtractor(BaseDataExtractor):
    """방문 데이터 추출기."""
    
    def extract(self, start_date: datetime = None, end_date: datetime = None) -> Iterator[pd.DataFrame]:
        """방문 데이터 추출."""
        query = """
        SELECT 
            v.id as visit_id,
            v.customer_id,
            v.employee_id,
            v.visit_date,
            v.total_amount,
            v.discount_amount,
            v.final_amount,
            v.payment_method,
            v.memo,
            v.status,
            v.created_at,
            v.updated_at,
            e.name as employee_name
        FROM visits v
        LEFT JOIN employees e ON v.employee_id = e.id
        WHERE v.status = 'completed'
        """

        params = {}
        if start_date:
            query += " AND v.visit_date >= :start_date"
            params['start_date'] = start_date
        if end_date:
            query += " AND v.visit_date <= :end_date"
            params['end_date'] = end_date

        return self.execute_query_chunked(query, params)
    
    def get_last_update_time(self) -> Optional[datetime]:
        """마지막 방문 데이터 업데이트 시간 조회."""
        query = """
        SELECT MAX(updated_at) as last_update
        FROM visits
        WHERE status = 'completed'
        """
        result = self.execute_query(query)
        return result.iloc[0]['last_update'] if not result.empty else None


class ServiceExtractor(BaseDataExtractor):
    """서비스 데이터 추출기."""
    
    def extract(self, start_date: datetime = None, end_date: datetime = None) -> Iterator[pd.DataFrame]:
        """서비스 데이터 추출."""
        query = """
        SELECT 
            s.id as service_id,
            s.name,
            s.category,
            s.price,
            s.duration_minutes,
            s.description,
            s.is_active,
            s.created_at,
            s.updated_at
        FROM services s
        WHERE s.is_active = true
        """

        params = {}
        if start_date:
            query += " AND s.updated_at >= :start_date"
            params['start_date'] = start_date
        if end_date:
            query += " AND s.updated_at <= :end_date"
            params['end_date'] = end_date

        return self.execute_query_chunked(query, params)
    
    def get_last_update_time(self) -> Optional[datetime]:
        """마지막 서비스 데이터 업데이트 시간 조회."""
        query = """
        SELECT MAX(updated_at) as last_update
        FROM services
        WHERE is_active = true
        """
        result = self.execute_query(query)
        return result.iloc[0]['last_update'] if not result.empty else None


class VisitServiceExtractor(BaseDataExtractor):
    """방문별 서비스 데이터 추출기."""
    
    def extract(self, start_date: datetime = None, end_date: datetime = None) -> Iterator[pd.DataFrame]:
        """방문별 서비스 데이터 추출."""
        query = """
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
            vs.memo,
            vs.created_at,
            vs.updated_at,
            s.name as service_name,
            s.category as service_category,
            e.name as employee_name
        FROM visit_services vs
        JOIN services s ON vs.service_id = s.id
        LEFT JOIN employees e ON vs.employee_id = e.id
        JOIN visits v ON vs.visit_id = v.id
        WHERE v.status = 'completed'
        """

        params = {}
        if start_date:
            query += " AND vs.created_at >= :start_date"
            params['start_date'] = start_date
        if end_date:
            query += " AND vs.created_at <= :end_date"
            params['end_date'] = end_date

        return self.execute_query_chunked(query, params)
    
    def get_last_update_time(self) -> Optional[datetime]:
        """마지막 방문별 서비스 데이터 업데이트 시간 조회."""
        query = """
        SELECT MAX(vs.updated_at) as last_update
        FROM visit_services vs
        JOIN visits v ON vs.visit_id = v.id
        WHERE v.status = 'completed'
        """
        result = self.execute_query(query)
        return result.iloc[0]['last_update'] if not result.empty else None 