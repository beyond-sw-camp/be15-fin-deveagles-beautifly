"""
Beautifly Database ETL Extractors

실제 beautifly 데이터베이스에서 데이터를 추출하는 ETL 추출기들
"""

import pandas as pd
from datetime import datetime, timedelta
from typing import Iterator, Optional, Dict, Any
from sqlalchemy import text

from analytics.etl.base import BaseExtractor
from analytics.core.logging import get_logger

logger = get_logger("beautifly_extractors")


class BeautiflyDataExtractor(BaseExtractor):
    """Beautifly CRM 데이터 추출 기본 클래스."""
    
    def __init__(self, config=None):
        super().__init__(config)
        from analytics.core.database import get_crm_db
        self.engine = get_crm_db()
        
    def execute_query(self, query: str, params: Dict[str, Any] = None) -> pd.DataFrame:
        """SQL 쿼리 실행 및 데이터프레임 반환."""
        try:
            with self.engine.connect() as conn:
                if params:
                    result = conn.execute(text(query), params)
                else:
                    result = conn.execute(text(query))
                
                # 결과를 pandas DataFrame으로 변환
                df = pd.DataFrame(result.fetchall(), columns=result.keys())
                return df
                
        except Exception as e:
            self.log_error("query_execution", e, query=query[:100] + "...")
            raise
    
    def execute_query_chunked(self, query: str, params: Dict[str, Any] = None, chunk_size: int = None) -> Iterator[pd.DataFrame]:
        """대용량 데이터를 청크 단위로 처리."""
        if chunk_size is None:
            chunk_size = self.config.batch_size if self.config else 10000
        
        offset = 0
        while True:
            chunked_query = f"{query} LIMIT {chunk_size} OFFSET {offset}"
            df = self.execute_query(chunked_query, params)
            
            if df.empty:
                break
                
            yield df
            offset += chunk_size


class BeautiflyCustomerExtractor(BaseExtractor):
    """Beautifly 고객 데이터 추출기."""
    
    def extract(self, start_date: datetime = None, end_date: datetime = None) -> Iterator[pd.DataFrame]:
        """고객 데이터 추출."""
        self.log_start("customer_extraction", start_date=start_date, end_date=end_date)
        
        query = """
        SELECT 
            c.customer_id,
            c.customer_name,
            c.phone_number,
            c.birthdate,
            c.gender,
            c.visit_count,
            c.total_revenue,
            c.recent_visit_date,
            c.created_at,
            c.modified_at,
            s.shop_name,
            cg.grade_name
        FROM customer c
        JOIN shop s ON c.shop_id = s.shop_id
        JOIN customer_grade cg ON c.customer_grade_id = cg.customer_grade_id
        WHERE c.deleted_at IS NULL
        """
        
        params = {}
        if start_date:
            query += " AND c.modified_at >= :start_date"
            params['start_date'] = start_date
        if end_date:
            query += " AND c.modified_at <= :end_date"
            params['end_date'] = end_date
        
        try:
            total_processed = 0
            for chunk_df in self.execute_query_chunked(query, params):
                total_processed += len(chunk_df)
                self.log_progress("customer_extraction", total_processed)
                yield chunk_df
                
        except Exception as e:
            self.log_error("customer_extraction", e)
            raise
    
    def get_last_update_time(self) -> Optional[datetime]:
        """마지막 고객 데이터 업데이트 시간 조회."""
        query = """
        SELECT MAX(modified_at) as last_update
        FROM customer
        WHERE deleted_at IS NULL
        """
        result = self.execute_query(query)
        return result.iloc[0]['last_update'] if not result.empty else None


class BeautiflyReservationExtractor(BaseExtractor):
    """Beautifly 예약 데이터 추출기."""
    
    def extract(self, start_date: datetime = None, end_date: datetime = None) -> Iterator[pd.DataFrame]:
        """예약 데이터 추출."""
        self.log_start("reservation_extraction", start_date=start_date, end_date=end_date)
        
        query = """
        SELECT 
            r.reservation_id,
            r.customer_id,
            r.staff_id,
            r.shop_id,
            r.reservation_status_name,
            r.reservation_start_at,
            r.reservation_end_at,
            r.created_at,
            r.modified_at,
            s.staff_name,
            sh.shop_name
        FROM reservation r
        JOIN staff s ON r.staff_id = s.staff_id
        JOIN shop sh ON r.shop_id = sh.shop_id
        WHERE r.deleted_at IS NULL
        """

        params = {}
        if start_date:
            query += " AND r.modified_at >= :start_date"
            params['start_date'] = start_date
        if end_date:
            query += " AND r.modified_at <= :end_date"
            params['end_date'] = end_date
        
        try:
            total_processed = 0
            for chunk_df in self.execute_query_chunked(query, params):
                total_processed += len(chunk_df)
                self.log_progress("reservation_extraction", total_processed)
                yield chunk_df
                
        except Exception as e:
            self.log_error("reservation_extraction", e)
            raise
    
    def get_last_update_time(self) -> Optional[datetime]:
        """마지막 예약 데이터 업데이트 시간 조회."""
        query = """
        SELECT MAX(modified_at) as last_update
        FROM reservation
        WHERE deleted_at IS NULL
        """
        result = self.execute_query(query)
        return result.iloc[0]['last_update'] if not result.empty else None


class BeautiflyServiceExtractor(BaseExtractor):
    """Beautifly 서비스 데이터 추출기."""
    
    def extract(self, start_date: datetime = None, end_date: datetime = None) -> Iterator[pd.DataFrame]:
        """서비스 데이터 추출."""
        self.log_start("service_extraction", start_date=start_date, end_date=end_date)
        
        query = """
        SELECT 
            si.secondary_item_id,
            si.secondary_item_name,
            si.secondary_item_price,
            si.time_taken,
            si.created_at,
            si.modified_at,
            pi.primary_item_id,
            pi.primary_item_name,
            pi.category
        FROM secondary_item si
        JOIN primary_item pi ON si.primary_item_id = pi.primary_item_id
        WHERE si.deleted_at IS NULL
        AND si.is_active = 1
        """

        params = {}
        if start_date:
            query += " AND si.modified_at >= :start_date"
            params['start_date'] = start_date
        if end_date:
            query += " AND si.modified_at <= :end_date"
            params['end_date'] = end_date
        
        try:
            total_processed = 0
            for chunk_df in self.execute_query_chunked(query, params):
                total_processed += len(chunk_df)
                self.log_progress("service_extraction", total_processed)
                yield chunk_df
                
        except Exception as e:
            self.log_error("service_extraction", e)
            raise
    
    def get_last_update_time(self) -> Optional[datetime]:
        """마지막 서비스 데이터 업데이트 시간 조회."""
        query = """
        SELECT MAX(modified_at) as last_update
        FROM secondary_item
        WHERE deleted_at IS NULL
        AND is_active = 1
        """
        result = self.execute_query(query)
        return result.iloc[0]['last_update'] if not result.empty else None


class BeautiflyReservationServiceExtractor(BaseExtractor):
    """Beautifly 예약-서비스 상세 데이터 추출기."""
    
    def extract(self, start_date: datetime = None, end_date: datetime = None) -> Iterator[pd.DataFrame]:
        """예약-서비스 상세 데이터 추출."""
        self.log_start("reservation_service_extraction", start_date=start_date, end_date=end_date)
        
        base_query = """
        SELECT 
            rd.reservation_detail_id as visit_service_id,
            rd.reservation_id as visit_id,
            rd.secondary_item_id as service_id,
            r.customer_id,
            r.staff_id as employee_id,
            si.secondary_item_name as service_name,
            pi.primary_item_name as service_category,
            si.secondary_item_price as unit_price,
            1 as quantity,  -- beautifly에는 quantity 개념이 없음
            si.secondary_item_price as total_price,
            0 as discount_amount,  -- 개별 서비스 할인은 sales 테이블에서 관리
            si.secondary_item_price as final_price,
            r.reservation_start_at as service_date,
            si.time_taken as duration_minutes
        FROM reservation_detail rd
        INNER JOIN reservation r ON rd.reservation_id = r.reservation_id
        INNER JOIN secondary_item si ON rd.secondary_item_id = si.secondary_item_id
        INNER JOIN primary_item pi ON si.primary_item_id = pi.primary_item_id
        WHERE r.deleted_at IS NULL 
            AND r.customer_id IS NOT NULL
            AND r.reservation_status_name IN ('COMPLETED', 'PAID')
            AND si.deleted_at IS NULL
            AND pi.deleted_at IS NULL
        """
        
        # 날짜 범위 필터 추가
        if start_date or end_date:
            if start_date:
                base_query += f" AND r.reservation_start_at >= '{start_date}'"
            if end_date:
                base_query += f" AND r.reservation_start_at <= '{end_date}'"
        
        base_query += " ORDER BY r.reservation_start_at, rd.reservation_detail_id"
        
        try:
            total_processed = 0
            for chunk_df in self.execute_query_chunked(base_query):
                total_processed += len(chunk_df)
                self.log_progress("reservation_service_extraction", total_processed)
                yield chunk_df
                
        except Exception as e:
            self.log_error("reservation_service_extraction", e)
            raise
    
    def get_last_update_time(self) -> Optional[datetime]:
        """마지막 업데이트 시간 조회."""
        try:
            query = """
            SELECT MAX(r.modified_at) as last_update 
            FROM reservation_detail rd
            INNER JOIN reservation r ON rd.reservation_id = r.reservation_id
            WHERE r.deleted_at IS NULL AND r.customer_id IS NOT NULL
            """
            result = self.execute_query(query)
            return result.iloc[0]['last_update'] if not result.empty and result.iloc[0]['last_update'] else None
        except Exception as e:
            logger.warning(f"Failed to get last update time: {e}")
            return None


class BeautiflyStaffExtractor(BaseExtractor):
    """Beautifly 직원 데이터 추출기."""
    
    def extract(self, start_date: datetime = None, end_date: datetime = None) -> Iterator[pd.DataFrame]:
        """직원 데이터 추출."""
        self.log_start("staff_extraction", start_date=start_date, end_date=end_date)
        
        base_query = """
        SELECT 
            staff_id as employee_id,
            staff_name as name,
            phone_number as phone,
            email,
            grade as position,
            staff_status as department,
            joined_date as hire_date,
            left_date,
            staff_description as specialties,
            CASE WHEN left_date IS NULL THEN 1 ELSE 0 END as is_active,
            created_at,
            modified_at
        FROM staff
        WHERE 1=1
        """
        
        # 날짜 범위 필터 추가
        if start_date or end_date:
            if start_date:
                base_query += f" AND modified_at >= '{start_date}'"
            if end_date:
                base_query += f" AND modified_at <= '{end_date}'"
        
        base_query += " ORDER BY staff_id"
        
        try:
            total_processed = 0
            for chunk_df in self.execute_query_chunked(base_query):
                total_processed += len(chunk_df)
                self.log_progress("staff_extraction", total_processed)
                yield chunk_df
                
        except Exception as e:
            self.log_error("staff_extraction", e)
            raise
    
    def get_last_update_time(self) -> Optional[datetime]:
        """마지막 업데이트 시간 조회."""
        try:
            query = "SELECT MAX(modified_at) as last_update FROM staff"
            result = self.execute_query(query)
            return result.iloc[0]['last_update'] if not result.empty and result.iloc[0]['last_update'] else None
        except Exception as e:
            logger.warning(f"Failed to get last update time: {e}")
            return None 