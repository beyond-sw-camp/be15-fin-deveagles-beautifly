/**
 * 데이터 포맷팅 유틸리티 함수들
 * 통일된 포맷팅 로직 제공
 */

/**
 * 통화 포맷팅 (원화)
 * @param {number} value - 포맷팅할 숫자
 * @param {Object} options - 포맷팅 옵션
 * @returns {string} 포맷된 통화 문자열
 */
export function formatCurrency(value, options = {}) {
  const { showUnit = true, shortFormat = true, decimalPlaces = 1 } = options;

  if (!value || value === 0) return '0원';

  if (shortFormat) {
    if (value >= 1000000000) {
      return `${(value / 1000000000).toFixed(decimalPlaces)}B${showUnit ? '원' : ''}`;
    }
    if (value >= 1000000) {
      return `${(value / 1000000).toFixed(decimalPlaces)}M${showUnit ? '원' : ''}`;
    }
    if (value >= 1000) {
      return `${(value / 1000).toFixed(0)}K${showUnit ? '원' : ''}`;
    }
  }

  return `${value.toLocaleString()}${showUnit ? '원' : ''}`;
}

/**
 * 퍼센트 포맷팅
 * @param {number} value - 포맷팅할 숫자
 * @param {Object} options - 포맷팅 옵션
 * @returns {string} 포맷된 퍼센트 문자열
 */
export function formatPercentage(value, options = {}) {
  const { showSign = true, decimalPlaces = 1 } = options;

  if (value == null || isNaN(value)) return '0%';

  const sign = showSign && value >= 0 ? '+' : '';
  return `${sign}${value.toFixed(decimalPlaces)}%`;
}

/**
 * 숫자를 축약된 형태로 포맷팅 (차트 축 라벨용)
 * @param {number} value - 포맷팅할 숫자
 * @returns {string} 축약된 숫자 문자열
 */
export function formatChartValue(value) {
  if (value >= 1000000) return `${(value / 1000000).toFixed(1)}M`;
  if (value >= 1000) return `${(value / 1000).toFixed(0)}K`;
  return value.toString();
}

/**
 * 날짜를 차트용으로 포맷팅
 * @param {string|Date} date - 포맷팅할 날짜
 * @param {string} format - 포맷 타입 ('short', 'long')
 * @returns {string} 포맷된 날짜 문자열
 */
export function formatChartDate(date, format = 'short') {
  const dateObj = typeof date === 'string' ? new Date(date) : date;

  if (format === 'short') {
    return `${dateObj.getMonth() + 1}/${dateObj.getDate()}`;
  }

  if (format === 'month') {
    return `${dateObj.getMonth() + 1}월`;
  }

  return dateObj.toLocaleDateString('ko-KR');
}

/**
 * 증감률 계산 및 포맷팅
 * @param {number} current - 현재 값
 * @param {number} previous - 이전 값
 * @returns {Object} 증감률 정보
 */
export function calculateGrowthRate(current, previous) {
  if (!previous || previous === 0) {
    return {
      rate: 0,
      formatted: '0%',
      type: 'neutral',
      isPositive: false,
      isNegative: false,
    };
  }

  const rate = ((current - previous) / previous) * 100;
  const isPositive = rate > 0;
  const isNegative = rate < 0;

  return {
    rate,
    formatted: formatPercentage(rate),
    type: isPositive ? 'positive' : isNegative ? 'negative' : 'neutral',
    isPositive,
    isNegative,
  };
}

/**
 * 컬렉션에서 평균값 계산
 * @param {Array} data - 데이터 배열
 * @param {string} key - 평균을 구할 키 (optional)
 * @returns {number} 평균값
 */
export function calculateAverage(data, key = null) {
  if (!data || data.length === 0) return 0;

  const values = key ? data.map(item => item[key]) : data;
  const sum = values.reduce((acc, val) => acc + (val || 0), 0);

  return sum / values.length;
}

/**
 * 컬렉션에서 총합 계산
 * @param {Array} data - 데이터 배열
 * @param {string} key - 합계를 구할 키 (optional)
 * @returns {number} 총합
 */
export function calculateTotal(data, key = null) {
  if (!data || data.length === 0) return 0;

  const values = key ? data.map(item => item[key]) : data;
  return values.reduce((acc, val) => acc + (val || 0), 0);
}
