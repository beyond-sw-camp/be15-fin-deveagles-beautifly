from typing import Dict, Any, Optional, Iterator
from datetime import datetime
import pandas as pd
from sqlalchemy.engine import Engine
from sqlalchemy import text

from ..core.database import get_crm_db
from ..core.config import get_settings
from .base import BaseExtractor

class BaseDataExtractor(BaseExtractor):
    """기본 데이터 추출기 클래스."""

    def __init__(self, config=None):
        super().__init__(config)
        self.engine: Engine = get_crm_db()
        self.settings = get_settings()

    def execute_query(self, query: str, params: Dict[str, Any] = None) -> pd.DataFrame:
        """SQL 쿼리 실행 및 결과를 DataFrame으로 반환."""
        try:
            with self.engine.connect() as conn:
                self.log_start("execute_query")
                result = pd.read_sql_query(text(query), conn, params=params)
                self.log_completion("execute_query", {"records": len(result)})
                return result
        except Exception as e:
            self.log_error("execute_query", e)
            raise

    def execute_query_chunked(self, query: str, params: Dict[str, Any] = None, chunk_size: int = None) -> Iterator[pd.DataFrame]:
        """대용량 데이터를 위한 청크 단위 쿼리 실행."""
        if chunk_size is None:
            chunk_size = self.config.batch_size if self.config else 10000

        try:
            with self.engine.connect() as conn:
                self.log_start("execute_query_chunked")
                
                # 청크 단위로 데이터 읽기
                for chunk_df in pd.read_sql_query(
                    text(query),
                    conn,
                    params=params,
                    chunksize=chunk_size
                ):
                    self.log_progress("execute_query_chunked", len(chunk_df))
                    yield chunk_df
                    
        except Exception as e:
            self.log_error("execute_query_chunked", e)
            raise

    def get_last_update_time(self) -> Optional[datetime]:
        """마지막 업데이트 시간 조회 - 하위 클래스에서 구현 필요."""
        raise NotImplementedError("Subclasses must implement get_last_update_time()")

    def extract(self, start_date: datetime = None, end_date: datetime = None) -> Iterator[pd.DataFrame]:
        """데이터 추출 - 하위 클래스에서 구현 필요."""
        raise NotImplementedError("Subclasses must implement extract()") 