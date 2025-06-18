"""Data transformers for analytics processing."""

from datetime import datetime, date
from typing import List, Dict, Any
import pandas as pd
import numpy as np

from analytics.core.config import get_settings
from .base import BaseTransformer


class CustomerAnalyticsTransformer(BaseTransformer):
    """고객 분석 데이터 변환기."""
    
    def __init__(self, config=None):
        super().__init__(config)
        self.settings = get_settings()
    
    def transform(self, data: pd.DataFrame) -> pd.DataFrame:
        """고객 데이터를 분석용 형태로 변환."""
        if data.empty:
            return pd.DataFrame()
        
        # 기본 고객 정보
        result = data[['customer_id', 'name', 'phone', 'email', 'birth_date', 'gender']].copy()
        
        # 나이 계산
        result['age'] = self._calculate_age(data['birth_date'])
        
        # 기본값 설정
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
        result['churn_risk_score'] = 0.0
        result['churn_risk_level'] = 'low'
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
    """방문 분석 데이터 변환기."""
    
    def transform(self, data: pd.DataFrame) -> pd.DataFrame:
        """방문 데이터를 분석용 형태로 변환."""
        if data.empty:
            return pd.DataFrame()
        
        result = data.copy()
        
        # 서비스 목록 파싱
        result['service_categories'] = self._parse_service_list(data['service_categories'])
        result['service_names'] = self._parse_service_list(data['service_names'])
        
        # 방문 시간 분석
        result['visit_hour'] = pd.to_datetime(data['visit_date']).dt.hour
        result['visit_weekday'] = pd.to_datetime(data['visit_date']).dt.weekday
        result['visit_month'] = pd.to_datetime(data['visit_date']).dt.month
        
        # 기본값 설정
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
    """서비스 선호도 분석 변환기."""
    
    def transform(self, data: pd.DataFrame) -> pd.DataFrame:
        """방문-서비스 데이터를 고객별 선호도로 변환."""
        if data.empty:
            return pd.DataFrame()
        
        # 고객별 서비스별 집계
        preference_data = self._aggregate_customer_service_data(data)
        
        # 선호도 순위 계산
        preference_data = self._calculate_preference_ranks(preference_data)
        
        # 비율 계산
        preference_data = self._calculate_ratios(preference_data)
        
        # 최근 활동 계산
        preference_data = self._calculate_recent_activity(preference_data, data)
        
        return preference_data
    
    def _aggregate_customer_service_data(self, data: pd.DataFrame) -> pd.DataFrame:
        """고객별 서비스별 데이터 집계."""
        agg_data = data.groupby(['customer_id', 'service_id', 'service_name', 'service_category']).agg({
            'visit_id': 'count',  # total_visits
            'final_price': ['sum', 'mean'],  # total_amount, avg_amount
            'visit_date': ['min', 'max']  # first_service_date, last_service_date
        }).reset_index()
        
        # 컬럼명 정리
        agg_data.columns = [
            'customer_id', 'service_id', 'service_name', 'service_category',
            'total_visits', 'total_amount', 'avg_amount',
            'first_service_date', 'last_service_date'
        ]
        
        # 마지막 이용일로부터 경과 일수
        today = datetime.now()
        agg_data['days_since_last_service'] = (
            today - pd.to_datetime(agg_data['last_service_date'])
        ).dt.days
        
        agg_data['updated_at'] = datetime.now()
        
        return agg_data
    
    def _calculate_preference_ranks(self, data: pd.DataFrame) -> pd.DataFrame:
        """고객별 선호도 순위 계산."""
        # 방문 횟수 기준으로 순위 매기기
        data['preference_rank'] = data.groupby('customer_id')['total_visits'].rank(
            method='dense', ascending=False
        ).astype(int)
        
        return data
    
    def _calculate_ratios(self, data: pd.DataFrame) -> pd.DataFrame:
        """고객별 방문/금액 비율 계산."""
        # 고객별 총 방문 횟수와 총 금액
        customer_totals = data.groupby('customer_id').agg({
            'total_visits': 'sum',
            'total_amount': 'sum'
        }).reset_index()
        
        customer_totals.columns = ['customer_id', 'customer_total_visits', 'customer_total_amount']
        
        # 원본 데이터와 병합
        data = data.merge(customer_totals, on='customer_id')
        
        # 비율 계산
        data['visit_ratio'] = data['total_visits'] / data['customer_total_visits']
        data['amount_ratio'] = data['total_amount'] / data['customer_total_amount']
        
        # 임시 컬럼 제거
        data = data.drop(['customer_total_visits', 'customer_total_amount'], axis=1)
        
        return data
    
    def _calculate_recent_activity(self, preference_data: pd.DataFrame, raw_data: pd.DataFrame) -> pd.DataFrame:
        """최근 3개월 활동 계산."""
        three_months_ago = datetime.now() - pd.Timedelta(days=90)
        
        # 최근 3개월 데이터 필터링
        recent_data = raw_data[pd.to_datetime(raw_data['visit_date']) >= three_months_ago]
        
        if recent_data.empty:
            preference_data['recent_visits_3m'] = 0
            return preference_data
        
        # 최근 3개월 방문 횟수 집계
        recent_visits = recent_data.groupby(['customer_id', 'service_id']).size().reset_index()
        recent_visits.columns = ['customer_id', 'service_id', 'recent_visits_3m']
        
        # 병합
        preference_data = preference_data.merge(
            recent_visits, 
            on=['customer_id', 'service_id'], 
            how='left'
        )
        
        # 결측값 처리
        preference_data['recent_visits_3m'] = preference_data['recent_visits_3m'].fillna(0).astype(int)
        
        return preference_data


class ServiceTagsTransformer(BaseTransformer):
    """서비스 태그 생성 변환기."""
    
    def transform(self, data: pd.DataFrame) -> pd.DataFrame:
        """고객별 선호 서비스 태그 생성."""
        if data.empty:
            return pd.DataFrame()
        
        # 고객별 상위 3개 서비스 추출
        top_services = self._get_top_services_per_customer(data)
        
        # 선호 카테고리 계산
        preferred_categories = self._get_preferred_categories(data)
        
        # 가격대 분석
        price_analysis = self._analyze_price_preferences(data)
        
        # 다양성 점수 계산
        variety_scores = self._calculate_variety_scores(data)
        
        # 모든 데이터 병합
        result = top_services.merge(preferred_categories, on='customer_id', how='outer')
        result = result.merge(price_analysis, on='customer_id', how='outer')
        result = result.merge(variety_scores, on='customer_id', how='outer')
        
        result['updated_at'] = datetime.now()
        
        return result.fillna('')
    
    def _get_top_services_per_customer(self, data: pd.DataFrame) -> pd.DataFrame:
        """고객별 상위 3개 서비스 추출."""
        # 선호도 순위 1-3위만 필터링
        top_3 = data[data['preference_rank'] <= 3].copy()
        
        # 피벗 테이블로 변환
        pivot = top_3.pivot_table(
            index='customer_id',
            columns='preference_rank',
            values='service_name',
            aggfunc='first'
        ).reset_index()
        
        # 컬럼명 변경
        pivot.columns = ['customer_id'] + [f'top_service_{i}' for i in range(1, len(pivot.columns))]
        
        # 1-3위 컬럼이 없는 경우 추가
        for i in range(1, 4):
            col_name = f'top_service_{i}'
            if col_name not in pivot.columns:
                pivot[col_name] = None
        
        return pivot[['customer_id', 'top_service_1', 'top_service_2', 'top_service_3']]
    
    def _get_preferred_categories(self, data: pd.DataFrame) -> pd.DataFrame:
        """고객별 선호 카테고리 계산."""
        # 고객별 카테고리별 방문 횟수
        category_visits = data.groupby(['customer_id', 'service_category'])['total_visits'].sum().reset_index()
        
        # 고객별 상위 카테고리들 추출 (방문 횟수 기준)
        top_categories = category_visits.groupby('customer_id').apply(
            lambda x: x.nlargest(3, 'total_visits')['service_category'].tolist()
        ).reset_index()
        
        top_categories.columns = ['customer_id', 'preferred_categories']
        
        return top_categories
    
    def _analyze_price_preferences(self, data: pd.DataFrame) -> pd.DataFrame:
        """고객별 가격대 선호도 분석."""
        # 고객별 평균 서비스 가격
        avg_prices = data.groupby('customer_id')['avg_amount'].mean().reset_index()
        avg_prices.columns = ['customer_id', 'avg_service_price']
        
        # 가격대 분류
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
        """고객별 서비스 다양성 점수 계산."""
        # 고객별 이용한 서비스 개수
        service_counts = data.groupby('customer_id')['service_id'].nunique().reset_index()
        service_counts.columns = ['customer_id', 'service_count']
        
        # 다양성 점수 (최대 10개 서비스 기준으로 정규화)
        service_counts['service_variety_score'] = np.minimum(service_counts['service_count'] / 10.0, 1.0)
        
        # 충성 서비스 (3회 이상 이용한 서비스들)
        loyalty_services = data[data['total_visits'] >= 3].groupby('customer_id')['service_name'].apply(list).reset_index()
        loyalty_services.columns = ['customer_id', 'loyalty_services']
        
        # 병합
        result = service_counts.merge(loyalty_services, on='customer_id', how='left')
        result['loyalty_services'] = result['loyalty_services'].fillna('').apply(lambda x: x if isinstance(x, list) else [])
        
        return result[['customer_id', 'service_variety_score', 'loyalty_services']] 