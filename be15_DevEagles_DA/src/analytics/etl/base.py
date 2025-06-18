"""Base classes for ETL components."""

from abc import ABC, abstractmethod
from datetime import datetime
from typing import Any, Dict, List, Optional, Iterator
from dataclasses import dataclass

import pandas as pd

from analytics.core.logging import get_logger
from .config import ETLConfig


@dataclass
class ETLResult:
    """ETL 작업 결과."""
    
    success: bool
    records_processed: int = 0
    records_inserted: int = 0
    records_updated: int = 0
    records_deleted: int = 0
    processing_time_seconds: float = 0.0
    error_message: Optional[str] = None
    metadata: Dict[str, Any] = None
    
    def __post_init__(self):
        if self.metadata is None:
            self.metadata = {}


class BaseETLComponent(ABC):
    """ETL 컴포넌트 기본 클래스."""
    
    def __init__(self, config: ETLConfig = None):
        self.config = config or ETLConfig()
        self.logger = get_logger(self.__class__.__name__)
    
    def log_start(self, operation: str, **kwargs):
        """작업 시작 로그."""
        self.logger.info(f"Starting {operation}", extra=kwargs)
    
    def log_progress(self, operation: str, processed: int, total: int = None, **kwargs):
        """진행률 로그."""
        if total:
            progress = f"{processed}/{total} ({processed/total*100:.1f}%)"
        else:
            progress = str(processed)
        
        self.logger.info(f"{operation} progress: {progress}", extra=kwargs)
    
    def log_completion(self, operation: str, result: ETLResult, **kwargs):
        """작업 완료 로그."""
        self.logger.info(
            f"Completed {operation}: {result.records_processed} processed, "
            f"{result.processing_time_seconds:.2f}s",
            extra={**kwargs, "result": result.__dict__}
        )
    
    def log_error(self, operation: str, error: Exception, **kwargs):
        """에러 로그."""
        self.logger.error(f"Error in {operation}: {error}", exc_info=True, extra=kwargs)


class BaseExtractor(BaseETLComponent):
    """데이터 추출기 기본 클래스."""
    
    @abstractmethod
    def extract(self, start_date: datetime = None, end_date: datetime = None) -> Iterator[pd.DataFrame]:
        """데이터 추출."""
        pass
    
    @abstractmethod
    def get_last_update_time(self) -> Optional[datetime]:
        """마지막 업데이트 시간 조회."""
        pass
    
    def extract_incremental(self, last_run: Optional[datetime] = None) -> Iterator[pd.DataFrame]:
        """증분 데이터 추출."""
        start_date = self.config.get_incremental_start_date(last_run)
        end_date = datetime.now()
        
        self.log_start("incremental_extract", start_date=start_date, end_date=end_date)
        
        return self.extract(start_date=start_date, end_date=end_date)


class BaseTransformer(BaseETLComponent):
    """데이터 변환기 기본 클래스."""
    
    @abstractmethod
    def transform(self, data: pd.DataFrame) -> pd.DataFrame:
        """데이터 변환."""
        pass
    
    def validate_data_quality(self, data: pd.DataFrame) -> bool:
        """데이터 품질 검증."""
        if not self.config.data_quality_checks:
            return True
        
        total_rows = len(data)
        if total_rows == 0:
            self.logger.warning("Empty dataset detected")
            return False
        
        # NULL 비율 체크
        null_ratio = data.isnull().sum().sum() / (total_rows * len(data.columns))
        if null_ratio > self.config.null_threshold:
            self.logger.error(f"High null ratio detected: {null_ratio:.2%}")
            return False
        
        # 중복 비율 체크 (기본키가 있는 경우)
        if hasattr(data, 'customer_id') or 'id' in data.columns:
            key_col = 'customer_id' if 'customer_id' in data.columns else 'id'
            duplicate_ratio = data[key_col].duplicated().sum() / total_rows
            if duplicate_ratio > self.config.duplicate_threshold:
                self.logger.error(f"High duplicate ratio detected: {duplicate_ratio:.2%}")
                return False
        
        return True
    
    def transform_batch(self, data: pd.DataFrame) -> pd.DataFrame:
        """배치 단위 변환 (품질 검증 포함)."""
        self.log_start("transform", rows=len(data))
        
        if not self.validate_data_quality(data):
            raise ValueError("Data quality validation failed")
        
        transformed = self.transform(data)
        
        if not self.validate_data_quality(transformed):
            raise ValueError("Transformed data quality validation failed")
        
        self.log_progress("transform", len(transformed))
        return transformed


class BaseLoader(BaseETLComponent):
    """데이터 적재기 기본 클래스."""
    
    @abstractmethod
    def load(self, data: pd.DataFrame) -> ETLResult:
        """데이터 적재."""
        pass
    
    @abstractmethod
    def update_metadata(self, table_name: str, result: ETLResult):
        """메타데이터 업데이트."""
        pass
    
    def load_batch(self, data: pd.DataFrame, table_name: str) -> ETLResult:
        """배치 단위 적재."""
        start_time = datetime.now()
        
        try:
            self.log_start("load", table=table_name, rows=len(data))
            
            result = self.load(data)
            result.processing_time_seconds = (datetime.now() - start_time).total_seconds()
            
            self.update_metadata(table_name, result)
            self.log_completion("load", result, table=table_name)
            
            return result
            
        except Exception as e:
            error_result = ETLResult(
                success=False,
                error_message=str(e),
                processing_time_seconds=(datetime.now() - start_time).total_seconds()
            )
            
            self.log_error("load", e, table=table_name)
            return error_result


class BaseETLPipeline(BaseETLComponent):
    """ETL 파이프라인 기본 클래스."""
    
    def __init__(self, config: ETLConfig = None):
        super().__init__(config)
        self.extractors: List[BaseExtractor] = []
        self.transformers: List[BaseTransformer] = []
        self.loaders: List[BaseLoader] = []
    
    def add_extractor(self, extractor: BaseExtractor):
        """추출기 추가."""
        self.extractors.append(extractor)
    
    def add_transformer(self, transformer: BaseTransformer):
        """변환기 추가."""
        self.transformers.append(transformer)
    
    def add_loader(self, loader: BaseLoader):
        """적재기 추가."""
        self.loaders.append(loader)
    
    @abstractmethod
    async def run(self, incremental: bool = True) -> Dict[str, ETLResult]:
        """파이프라인 실행."""
        pass
    
    def chunk_dataframe(self, df: pd.DataFrame) -> Iterator[pd.DataFrame]:
        """DataFrame을 청크 단위로 분할."""
        chunk_size = self.config.chunk_size
        for i in range(0, len(df), chunk_size):
            yield df.iloc[i:i + chunk_size] 