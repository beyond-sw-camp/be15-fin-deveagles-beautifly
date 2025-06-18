"""Data loaders for analytics database."""

from datetime import datetime
from typing import List, Dict, Any
import pandas as pd

from analytics.core.database import get_analytics_db
from .base import BaseLoader, ETLResult


class AnalyticsDataLoader(BaseLoader):
    """Analytics 데이터베이스 기본 적재기."""
    
    def __init__(self, config=None):
        super().__init__(config)
        self.conn = get_analytics_db()
    
    def execute_sql(self, sql: str, params: List[Any] = None) -> int:
        """SQL 실행 및 영향받은 행 수 반환."""
        try:
            if params:
                result = self.conn.execute(sql, params)
            else:
                result = self.conn.execute(sql)
            
            # DuckDB는 rowcount를 직접 지원하지 않으므로 결과에서 추출
            if hasattr(result, 'rowcount'):
                return result.rowcount
            return 0
            
        except Exception as e:
            self.log_error("execute_sql", e, sql=sql[:100])
            raise
    
    def update_metadata(self, table_name: str, result: ETLResult):
        """ETL 메타데이터 업데이트."""
        try:
            self.execute_sql("""
                INSERT OR REPLACE INTO etl_metadata 
                (table_name, last_updated, records_processed, records_inserted, 
                 records_updated, records_deleted, processing_time_seconds, status, error_message)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """, [
                table_name,
                datetime.now(),
                result.records_processed,
                result.records_inserted,
                result.records_updated,
                result.records_deleted,
                result.processing_time_seconds,
                "completed" if result.success else "failed",
                result.error_message
            ])
            
        except Exception as e:
            self.log_error("update_metadata", e, table=table_name)


class CustomerAnalyticsLoader(AnalyticsDataLoader):
    """고객 분석 데이터 적재기."""
    
    def load(self, data: pd.DataFrame) -> ETLResult:
        """고객 분석 데이터 적재."""
        if data.empty:
            return ETLResult(success=True, records_processed=0)
        
        try:
            # 기존 데이터와 비교하여 UPSERT 수행
            inserted, updated = self._upsert_customer_analytics(data)
            
            return ETLResult(
                success=True,
                records_processed=len(data),
                records_inserted=inserted,
                records_updated=updated
            )
            
        except Exception as e:
            self.log_error("load_customer_analytics", e)
            return ETLResult(
                success=False,
                records_processed=len(data),
                error_message=str(e)
            )
    
    def _upsert_customer_analytics(self, data: pd.DataFrame) -> tuple[int, int]:
        """고객 분석 데이터 UPSERT."""
        inserted = 0
        updated = 0
        
        for _, row in data.iterrows():
            # 기존 데이터 확인
            existing = self.conn.execute(
                "SELECT customer_id FROM customer_analytics WHERE customer_id = ?",
                [row['customer_id']]
            ).fetchone()
            
            if existing:
                # UPDATE
                self._update_customer_record(row)
                updated += 1
            else:
                # INSERT
                self._insert_customer_record(row)
                inserted += 1
        
        return inserted, updated
    
    def _insert_customer_record(self, row: pd.Series):
        """고객 레코드 삽입."""
        self.execute_sql("""
            INSERT INTO customer_analytics 
            (customer_id, name, phone, email, birth_date, gender,
             first_visit_date, last_visit_date, total_visits, total_amount, avg_visit_amount,
             lifecycle_days, days_since_last_visit, visit_frequency, preferred_services,
             visits_3m, amount_3m, segment, segment_updated_at, churn_risk_score, churn_risk_level,
             updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """, [
            row['customer_id'], row['name'], row['phone'], row['email'], 
            row['birth_date'], row['gender'], row['first_visit_date'], row['last_visit_date'],
            row['total_visits'], row['total_amount'], row['avg_visit_amount'],
            row['lifecycle_days'], row['days_since_last_visit'], row['visit_frequency'],
            row['preferred_services'], row['visits_3m'], row['amount_3m'],
            row['segment'], row['segment_updated_at'], row['churn_risk_score'], 
            row['churn_risk_level'], row['updated_at']
        ])
    
    def _update_customer_record(self, row: pd.Series):
        """고객 레코드 업데이트."""
        self.execute_sql("""
            UPDATE customer_analytics SET
                name = ?, phone = ?, email = ?, birth_date = ?, gender = ?,
                first_visit_date = ?, last_visit_date = ?, total_visits = ?, 
                total_amount = ?, avg_visit_amount = ?, lifecycle_days = ?,
                days_since_last_visit = ?, visit_frequency = ?, preferred_services = ?,
                visits_3m = ?, amount_3m = ?, segment = ?, segment_updated_at = ?,
                churn_risk_score = ?, churn_risk_level = ?, updated_at = ?
            WHERE customer_id = ?
        """, [
            row['name'], row['phone'], row['email'], row['birth_date'], row['gender'],
            row['first_visit_date'], row['last_visit_date'], row['total_visits'],
            row['total_amount'], row['avg_visit_amount'], row['lifecycle_days'],
            row['days_since_last_visit'], row['visit_frequency'], row['preferred_services'],
            row['visits_3m'], row['amount_3m'], row['segment'], row['segment_updated_at'],
            row['churn_risk_score'], row['churn_risk_level'], row['updated_at'],
            row['customer_id']
        ])


class VisitAnalyticsLoader(AnalyticsDataLoader):
    """방문 분석 데이터 적재기."""
    
    def load(self, data: pd.DataFrame) -> ETLResult:
        """방문 분석 데이터 적재."""
        if data.empty:
            return ETLResult(success=True, records_processed=0)
        
        try:
            # 기존 데이터와 비교하여 UPSERT 수행
            inserted, updated = self._upsert_visit_analytics(data)
            
            return ETLResult(
                success=True,
                records_processed=len(data),
                records_inserted=inserted,
                records_updated=updated
            )
            
        except Exception as e:
            self.log_error("load_visit_analytics", e)
            return ETLResult(
                success=False,
                records_processed=len(data),
                error_message=str(e)
            )
    
    def _upsert_visit_analytics(self, data: pd.DataFrame) -> tuple[int, int]:
        """방문 분석 데이터 UPSERT."""
        inserted = 0
        updated = 0
        
        for _, row in data.iterrows():
            # 기존 데이터 확인
            existing = self.conn.execute(
                "SELECT visit_id FROM visit_analytics WHERE visit_id = ?",
                [row['visit_id']]
            ).fetchone()
            
            if existing:
                # UPDATE
                self._update_visit_record(row)
                updated += 1
            else:
                # INSERT
                self._insert_visit_record(row)
                inserted += 1
        
        return inserted, updated
    
    def _insert_visit_record(self, row: pd.Series):
        """방문 레코드 삽입."""
        self.execute_sql("""
            INSERT INTO visit_analytics 
            (visit_id, customer_id, employee_id, visit_date, total_amount, discount_amount, final_amount,
             service_count, service_categories, service_names, is_first_visit, 
             days_since_previous_visit, visit_sequence, visit_hour, visit_weekday, visit_month, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """, [
            row['visit_id'], row['customer_id'], row['employee_id'], row['visit_date'],
            row['total_amount'], row['discount_amount'], row['final_amount'],
            row['service_count'], row['service_categories'], row['service_names'],
            row['is_first_visit'], row['days_since_previous_visit'], row['visit_sequence'],
            row['visit_hour'], row['visit_weekday'], row['visit_month'], row['created_at']
        ])
    
    def _update_visit_record(self, row: pd.Series):
        """방문 레코드 업데이트."""
        self.execute_sql("""
            UPDATE visit_analytics SET
                customer_id = ?, employee_id = ?, visit_date = ?, total_amount = ?,
                discount_amount = ?, final_amount = ?, service_count = ?,
                service_categories = ?, service_names = ?, is_first_visit = ?,
                days_since_previous_visit = ?, visit_sequence = ?, visit_hour = ?,
                visit_weekday = ?, visit_month = ?, created_at = ?
            WHERE visit_id = ?
        """, [
            row['customer_id'], row['employee_id'], row['visit_date'], row['total_amount'],
            row['discount_amount'], row['final_amount'], row['service_count'],
            row['service_categories'], row['service_names'], row['is_first_visit'],
            row['days_since_previous_visit'], row['visit_sequence'], row['visit_hour'],
            row['visit_weekday'], row['visit_month'], row['created_at'], row['visit_id']
        ])


class ServicePreferenceLoader(AnalyticsDataLoader):
    """서비스 선호도 데이터 적재기."""
    
    def load(self, data: pd.DataFrame) -> ETLResult:
        """서비스 선호도 데이터 적재."""
        if data.empty:
            return ETLResult(success=True, records_processed=0)
        
        try:
            # 기존 데이터와 비교하여 UPSERT 수행
            inserted, updated = self._upsert_service_preferences(data)
            
            return ETLResult(
                success=True,
                records_processed=len(data),
                records_inserted=inserted,
                records_updated=updated
            )
            
        except Exception as e:
            self.log_error("load_service_preferences", e)
            return ETLResult(
                success=False,
                records_processed=len(data),
                error_message=str(e)
            )
    
    def _upsert_service_preferences(self, data: pd.DataFrame) -> tuple[int, int]:
        """서비스 선호도 데이터 UPSERT."""
        inserted = 0
        updated = 0
        
        for _, row in data.iterrows():
            # 기존 데이터 확인
            existing = self.conn.execute(
                "SELECT customer_id FROM customer_service_preferences WHERE customer_id = ? AND service_id = ?",
                [row['customer_id'], row['service_id']]
            ).fetchone()
            
            if existing:
                # UPDATE
                self._update_preference_record(row)
                updated += 1
            else:
                # INSERT
                self._insert_preference_record(row)
                inserted += 1
        
        return inserted, updated
    
    def _insert_preference_record(self, row: pd.Series):
        """선호도 레코드 삽입."""
        self.execute_sql("""
            INSERT INTO customer_service_preferences 
            (customer_id, service_id, service_name, service_category, total_visits,
             total_amount, avg_amount, first_service_date, last_service_date,
             preference_rank, visit_ratio, amount_ratio, recent_visits_3m,
             days_since_last_service, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """, [
            row['customer_id'], row['service_id'], row['service_name'], row['service_category'],
            row['total_visits'], row['total_amount'], row['avg_amount'],
            row['first_service_date'], row['last_service_date'], row['preference_rank'],
            row['visit_ratio'], row['amount_ratio'], row['recent_visits_3m'],
            row['days_since_last_service'], row['updated_at']
        ])
    
    def _update_preference_record(self, row: pd.Series):
        """선호도 레코드 업데이트."""
        self.execute_sql("""
            UPDATE customer_service_preferences SET
                service_name = ?, service_category = ?, total_visits = ?,
                total_amount = ?, avg_amount = ?, first_service_date = ?,
                last_service_date = ?, preference_rank = ?, visit_ratio = ?,
                amount_ratio = ?, recent_visits_3m = ?, days_since_last_service = ?,
                updated_at = ?
            WHERE customer_id = ? AND service_id = ?
        """, [
            row['service_name'], row['service_category'], row['total_visits'],
            row['total_amount'], row['avg_amount'], row['first_service_date'],
            row['last_service_date'], row['preference_rank'], row['visit_ratio'],
            row['amount_ratio'], row['recent_visits_3m'], row['days_since_last_service'],
            row['updated_at'], row['customer_id'], row['service_id']
        ])


class ServiceTagsLoader(AnalyticsDataLoader):
    """서비스 태그 데이터 적재기."""
    
    def load(self, data: pd.DataFrame) -> ETLResult:
        """서비스 태그 데이터 적재."""
        if data.empty:
            return ETLResult(success=True, records_processed=0)
        
        try:
            # 기존 데이터와 비교하여 UPSERT 수행
            inserted, updated = self._upsert_service_tags(data)
            
            return ETLResult(
                success=True,
                records_processed=len(data),
                records_inserted=inserted,
                records_updated=updated
            )
            
        except Exception as e:
            self.log_error("load_service_tags", e)
            return ETLResult(
                success=False,
                records_processed=len(data),
                error_message=str(e)
            )
    
    def _upsert_service_tags(self, data: pd.DataFrame) -> tuple[int, int]:
        """서비스 태그 데이터 UPSERT."""
        inserted = 0
        updated = 0
        
        for _, row in data.iterrows():
            # 기존 데이터 확인
            existing = self.conn.execute(
                "SELECT customer_id FROM customer_service_tags WHERE customer_id = ?",
                [row['customer_id']]
            ).fetchone()
            
            if existing:
                # UPDATE
                self._update_tags_record(row)
                updated += 1
            else:
                # INSERT
                self._insert_tags_record(row)
                inserted += 1
        
        return inserted, updated
    
    def _insert_tags_record(self, row: pd.Series):
        """태그 레코드 삽입."""
        self.execute_sql("""
            INSERT INTO customer_service_tags 
            (customer_id, top_service_1, top_service_2, top_service_3,
             preferred_categories, service_variety_score, loyalty_services,
             avg_service_price, preferred_price_range, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """, [
            row['customer_id'], row['top_service_1'], row['top_service_2'], row['top_service_3'],
            row['preferred_categories'], row['service_variety_score'], row['loyalty_services'],
            row['avg_service_price'], row['preferred_price_range'], row['updated_at']
        ])
    
    def _update_tags_record(self, row: pd.Series):
        """태그 레코드 업데이트."""
        self.execute_sql("""
            UPDATE customer_service_tags SET
                top_service_1 = ?, top_service_2 = ?, top_service_3 = ?,
                preferred_categories = ?, service_variety_score = ?, loyalty_services = ?,
                avg_service_price = ?, preferred_price_range = ?, updated_at = ?
            WHERE customer_id = ?
        """, [
            row['top_service_1'], row['top_service_2'], row['top_service_3'],
            row['preferred_categories'], row['service_variety_score'], row['loyalty_services'],
            row['avg_service_price'], row['preferred_price_range'], row['updated_at'],
            row['customer_id']
        ]) 