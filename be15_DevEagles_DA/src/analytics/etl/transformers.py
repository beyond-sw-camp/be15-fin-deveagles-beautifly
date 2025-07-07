"""Data transformers for analytics processing."""

from datetime import datetime, date
from typing import List, Dict, Any
import pandas as pd
import numpy as np
import logging

from analytics.core.config import get_settings
from analytics.ml.churn_model import predict as predict_churn
from .base import BaseTransformer

logger = logging.getLogger(__name__)


class CustomerAnalyticsTransformer(BaseTransformer):
    
    def __init__(self, config=None):
        super().__init__(config)
        self.settings = get_settings()
    
    def transform(self, data: pd.DataFrame) -> pd.DataFrame:
        if data.empty:
            return pd.DataFrame()
        
        result = data[['customer_id', 'name', 'phone', 'email', 'birth_date', 'gender']].copy()
        
        result['age'] = self._calculate_age(data['birth_date'])
        
        result['first_visit_date'] = None
        result['last_visit_date'] = None
        result['total_visits'] = 0
        result['total_amount'] = 0.0
        result['avg_visit_amount'] = 0.0
        result['lifecycle_days'] = 0
        result['days_since_last_visit'] = None
        result['visit_frequency'] = 0.0
        result['preferred_services'] = [[] for _ in range(len(result))]
        result['visits_3m'] = 0
        result['amount_3m'] = 0.0
        result['segment'] = 'new'
        result['segment_updated_at'] = datetime.now()

        # ---- Churn prediction ----
        scores, levels = predict_churn(result)
        result['churn_risk_score'] = scores
        result['churn_risk_level'] = levels

        result['updated_at'] = datetime.now()
        
        return result
    
    def _calculate_age(self, birth_dates: pd.Series) -> pd.Series:
        """나이 계산."""
        today = date.today()
        
        def calc_age(birth_date):
            if pd.isna(birth_date):
                return None
            if isinstance(birth_date, str):
                birth_date = pd.to_datetime(birth_date).date()
            elif isinstance(birth_date, datetime):
                birth_date = birth_date.date()
            
            return today.year - birth_date.year - (
                (today.month, today.day) < (birth_date.month, birth_date.day)
            )
        
        return birth_dates.apply(calc_age)


class VisitAnalyticsTransformer(BaseTransformer):
    
    def transform(self, data: pd.DataFrame) -> pd.DataFrame:
        if data.empty:
            return pd.DataFrame()
        
        result = data.copy()
        
        import numpy as np
        result['employee_id'] = data['employee_id'].fillna(value=np.nan)
        
        if 'services' in data.columns:
            result['service_categories'] = self._parse_service_list(data['services'])
            result['service_names'] = self._parse_service_list(data['services'])
        else:
            result['service_categories'] = pd.Series([[]] * len(data))
            result['service_names'] = pd.Series([[]] * len(data))
        
        result['visit_hour'] = pd.to_datetime(data['visit_date']).dt.hour
        result['visit_weekday'] = pd.to_datetime(data['visit_date']).dt.weekday
        result['visit_month'] = pd.to_datetime(data['visit_date']).dt.month
        
        result['is_first_visit'] = False
        result['days_since_previous_visit'] = None
        result['visit_sequence'] = 1
        result['created_at'] = datetime.now()
        
        return result
    
    def _parse_service_list(self, service_string: pd.Series) -> pd.Series:
        """콤마로 구분된 서비스 문자열을 리스트로 변환."""
        def parse_string(s):
            if pd.isna(s) or s == '':
                return []
            return [item.strip() for item in str(s).split(',') if item.strip()]
        
        return service_string.apply(parse_string)


class ServicePreferenceTransformer(BaseTransformer):
    
    def transform(self, data: pd.DataFrame) -> pd.DataFrame:
        if data.empty:
            return pd.DataFrame()
        
        preference_data = self._aggregate_customer_service_data(data)
        
        preference_data = self._calculate_preference_ranks(preference_data)
        
        preference_data = self._calculate_ratios(preference_data)
        
        preference_data = self._calculate_recent_activity(preference_data, data)
        
        return preference_data
    
    def _aggregate_customer_service_data(self, data: pd.DataFrame) -> pd.DataFrame:
        agg_data = data.groupby(['customer_id', 'service_id', 'service_name', 'service_category']).agg({
            'visit_id': 'count',  # total_visits
            'final_price': ['sum', 'mean'],  # total_amount, avg_amount
            'service_date': ['min', 'max']  # first_service_date, last_service_date
        }).reset_index()
        
        agg_data.columns = [
            'customer_id', 'service_id', 'service_name', 'service_category',
            'total_visits', 'total_amount', 'avg_amount',
            'first_service_date', 'last_service_date'
        ]
        
        today = datetime.now()
        agg_data['days_since_last_service'] = (
            today - pd.to_datetime(agg_data['last_service_date'])
        ).dt.days
        
        agg_data['updated_at'] = datetime.now()
        
        return agg_data
    
    def _calculate_preference_ranks(self, data: pd.DataFrame) -> pd.DataFrame:
        data['preference_rank'] = data.groupby('customer_id')['total_visits'].rank(
            method='dense', ascending=False
        ).astype(int)
        
        return data
    
    def _calculate_ratios(self, data: pd.DataFrame) -> pd.DataFrame:
        customer_totals = data.groupby('customer_id').agg({
            'total_visits': 'sum',
            'total_amount': 'sum'
        }).reset_index()
        
        customer_totals.columns = ['customer_id', 'customer_total_visits', 'customer_total_amount']
        
        data = data.merge(customer_totals, on='customer_id')
        
        data['visit_ratio'] = data['total_visits'] / data['customer_total_visits']
        data['amount_ratio'] = data['total_amount'] / data['customer_total_amount']
        
        data = data.drop(['customer_total_visits', 'customer_total_amount'], axis=1)
        
        return data
    
    def _calculate_recent_activity(self, preference_data: pd.DataFrame, raw_data: pd.DataFrame) -> pd.DataFrame:
        three_months_ago = datetime.now() - pd.Timedelta(days=90)
        
        recent_data = raw_data[pd.to_datetime(raw_data['service_date']) >= three_months_ago]
        
        if recent_data.empty:
            preference_data['recent_visits_3m'] = 0
            return preference_data
        
        recent_visits = recent_data.groupby(['customer_id', 'service_id']).size().reset_index()
        recent_visits.columns = ['customer_id', 'service_id', 'recent_visits_3m']
        
        preference_data = preference_data.merge(
            recent_visits, 
            on=['customer_id', 'service_id'], 
            how='left'
        )
        
        preference_data['recent_visits_3m'] = preference_data['recent_visits_3m'].fillna(0).astype(int)
        
        return preference_data


class ServiceTagsTransformer(BaseTransformer):
    
    def transform(self, data: pd.DataFrame) -> pd.DataFrame:
        if data.empty:
            return pd.DataFrame()
        
        top_services = self._get_top_services_per_customer(data)
        
        preferred_categories = self._get_preferred_categories(data)
        
        price_analysis = self._analyze_price_preferences(data)
        
        variety_scores = self._calculate_variety_scores(data)
        
        result = top_services.merge(preferred_categories, on='customer_id', how='outer')
        result = result.merge(price_analysis, on='customer_id', how='outer')
        result = result.merge(variety_scores, on='customer_id', how='outer')
        
        result['updated_at'] = datetime.now()
        
        return result.fillna('')
    
    def _get_top_services_per_customer(self, data: pd.DataFrame) -> pd.DataFrame:
        top_3 = data[data['preference_rank'] <= 3].copy()
        
        pivot = top_3.pivot_table(
            index='customer_id',
            columns='preference_rank',
            values='service_name',
            aggfunc='first'
        ).reset_index()
        
        pivot.columns = ['customer_id'] + [f'top_service_{i}' for i in range(1, len(pivot.columns))]
        
        for i in range(1, 4):
            col_name = f'top_service_{i}'
            if col_name not in pivot.columns:
                pivot[col_name] = None
        
        return pivot[['customer_id', 'top_service_1', 'top_service_2', 'top_service_3']]
    
    def _get_preferred_categories(self, data: pd.DataFrame) -> pd.DataFrame:
        category_visits = data.groupby(['customer_id', 'service_category'])['total_visits'].sum().reset_index()
        
        top_categories = category_visits.groupby('customer_id').apply(
            lambda x: x.nlargest(3, 'total_visits')['service_category'].tolist()
        ).reset_index()
        
        top_categories.columns = ['customer_id', 'preferred_categories']
        
        return top_categories
    
    def _analyze_price_preferences(self, data: pd.DataFrame) -> pd.DataFrame:
        avg_prices = data.groupby('customer_id')['avg_amount'].mean().reset_index()
        avg_prices.columns = ['customer_id', 'avg_service_price']
        
        def classify_price_range(price):
            if price < 50000:
                return 'low'
            elif price > 100000:
                return 'high'
            else:
                return 'medium'
        
        avg_prices['preferred_price_range'] = avg_prices['avg_service_price'].apply(classify_price_range)
        
        return avg_prices
    
    def _calculate_variety_scores(self, data: pd.DataFrame) -> pd.DataFrame:
        service_counts = data.groupby('customer_id')['service_id'].nunique().reset_index()
        service_counts.columns = ['customer_id', 'service_count']
        
        service_counts['service_variety_score'] = np.minimum(service_counts['service_count'] / 10.0, 1.0)
        
        loyalty_services = data[data['total_visits'] >= 3].groupby('customer_id')['service_name'].apply(list).reset_index()
        loyalty_services.columns = ['customer_id', 'loyalty_services']
        
        result = service_counts.merge(loyalty_services, on='customer_id', how='left')
        result['loyalty_services'] = result['loyalty_services'].fillna('').apply(lambda x: x if isinstance(x, list) else [])
        
        return result[['customer_id', 'service_variety_score', 'loyalty_services']]


class DataTransformer(BaseTransformer):
    """기본 데이터 변환기."""

    def __init__(self, config=None):
        super().__init__(config)
        self.settings = get_settings()

    def transform(self, data: Dict[str, pd.DataFrame]) -> Dict[str, pd.DataFrame]:
        """데이터 변환 수행."""
        try:
            transformed_data = {}
            
            # 고객 데이터 변환
            if 'customers' in data:
                transformed_data['customers'] = self.transform_customers(data['customers'])
            
            # 방문/예약 데이터 변환
            if 'visits' in data:
                transformed_data['visits'] = self.transform_visits(data['visits'])
            elif 'reservations' in data:
                transformed_data['visits'] = self.transform_reservations(data['reservations'])
            
            # 서비스 데이터 변환
            if 'services' in data:
                transformed_data['services'] = self.transform_services(data['services'])
            
            # 방문-서비스 상세 데이터 변환
            if 'visit_services' in data:
                transformed_data['visit_services'] = self.transform_visit_services(data['visit_services'])
            
            return transformed_data
            
        except Exception as e:
            logger.error(f"데이터 변환 중 오류 발생: {str(e)}")
            raise

    def transform_customers(self, df: pd.DataFrame) -> pd.DataFrame:
        """고객 데이터 변환."""
        try:
            # 필수 컬럼 확인
            required_columns = ['customer_id', 'name', 'phone']
            missing_columns = [col for col in required_columns if col not in df.columns]
            if missing_columns:
                raise ValueError(f"고객 데이터에 필수 컬럼이 없습니다: {missing_columns}")
            
            # 데이터 타입 변환
            df['customer_id'] = df['customer_id'].astype(str)
            if 'birth_date' in df.columns:
                df['birth_date'] = pd.to_datetime(df['birth_date'], errors='coerce')
            if 'created_at' in df.columns:
                df['created_at'] = pd.to_datetime(df['created_at'])
            if 'updated_at' in df.columns:
                df['updated_at'] = pd.to_datetime(df['updated_at'])
            
            return df
            
        except Exception as e:
            logger.error(f"고객 데이터 변환 중 오류 발생: {str(e)}")
            raise

    def transform_visits(self, df: pd.DataFrame) -> pd.DataFrame:
        """방문 데이터 변환."""
        try:
            # 필수 컬럼 확인
            required_columns = ['visit_id', 'customer_id', 'visit_date']
            missing_columns = [col for col in required_columns if col not in df.columns]
            if missing_columns:
                raise ValueError(f"방문 데이터에 필수 컬럼이 없습니다: {missing_columns}")
            
            # 데이터 타입 변환
            df['visit_id'] = df['visit_id'].astype(str)
            df['customer_id'] = df['customer_id'].astype(str)
            df['visit_date'] = pd.to_datetime(df['visit_date'])
            if 'created_at' in df.columns:
                df['created_at'] = pd.to_datetime(df['created_at'])
            if 'updated_at' in df.columns:
                df['updated_at'] = pd.to_datetime(df['updated_at'])
            
            return df
            
        except Exception as e:
            logger.error(f"방문 데이터 변환 중 오류 발생: {str(e)}")
            raise

    def transform_reservations(self, df: pd.DataFrame) -> pd.DataFrame:
        """예약 데이터를 방문 데이터 형식으로 변환."""
        try:
            # 필수 컬럼 확인
            required_columns = ['reservation_id', 'customer_id', 'reservation_start_at']
            missing_columns = [col for col in required_columns if col not in df.columns]
            if missing_columns:
                raise ValueError(f"예약 데이터에 필수 컬럼이 없습니다: {missing_columns}")
            
            # 컬럼 이름 변경
            df = df.rename(columns={
                'reservation_id': 'visit_id',
                'reservation_start_at': 'visit_date',
                'reservation_status_name': 'status'
            })
            
            # 데이터 타입 변환
            df['visit_id'] = df['visit_id'].astype(str)
            df['customer_id'] = df['customer_id'].astype(str)
            df['visit_date'] = pd.to_datetime(df['visit_date'])
            if 'created_at' in df.columns:
                df['created_at'] = pd.to_datetime(df['created_at'])
            if 'updated_at' in df.columns:
                df['updated_at'] = pd.to_datetime(df['updated_at'])
            
            return df
            
        except Exception as e:
            logger.error(f"예약 데이터 변환 중 오류 발생: {str(e)}")
            raise

    def transform_services(self, df: pd.DataFrame) -> pd.DataFrame:
        """서비스 데이터 변환."""
        try:
            # 필수 컬럼 확인
            required_columns = ['service_id', 'name', 'price']
            missing_columns = [col for col in required_columns if col not in df.columns]
            if missing_columns:
                raise ValueError(f"서비스 데이터에 필수 컬럼이 없습니다: {missing_columns}")
            
            # 데이터 타입 변환
            df['service_id'] = df['service_id'].astype(str)
            df['price'] = pd.to_numeric(df['price'], errors='coerce')
            if 'duration_minutes' in df.columns:
                df['duration_minutes'] = pd.to_numeric(df['duration_minutes'], errors='coerce')
            if 'created_at' in df.columns:
                df['created_at'] = pd.to_datetime(df['created_at'])
            if 'updated_at' in df.columns:
                df['updated_at'] = pd.to_datetime(df['updated_at'])
            
            return df
            
        except Exception as e:
            logger.error(f"서비스 데이터 변환 중 오류 발생: {str(e)}")
            raise

    def transform_visit_services(self, df: pd.DataFrame) -> pd.DataFrame:
        """방문-서비스 상세 데이터 변환."""
        try:
            # 필수 컬럼 확인
            required_columns = ['visit_service_id', 'visit_id', 'service_id']
            missing_columns = [col for col in required_columns if col not in df.columns]
            if missing_columns:
                raise ValueError(f"방문-서비스 데이터에 필수 컬럼이 없습니다: {missing_columns}")
            
            # 데이터 타입 변환
            df['visit_service_id'] = df['visit_service_id'].astype(str)
            df['visit_id'] = df['visit_id'].astype(str)
            df['service_id'] = df['service_id'].astype(str)
            if 'quantity' in df.columns:
                df['quantity'] = pd.to_numeric(df['quantity'], errors='coerce')
            if 'unit_price' in df.columns:
                df['unit_price'] = pd.to_numeric(df['unit_price'], errors='coerce')
            if 'total_price' in df.columns:
                df['total_price'] = pd.to_numeric(df['total_price'], errors='coerce')
            if 'created_at' in df.columns:
                df['created_at'] = pd.to_datetime(df['created_at'])
            if 'updated_at' in df.columns:
                df['updated_at'] = pd.to_datetime(df['updated_at'])
            
            return df
            
        except Exception as e:
            logger.error(f"방문-서비스 데이터 변환 중 오류 발생: {str(e)}")
            raise 