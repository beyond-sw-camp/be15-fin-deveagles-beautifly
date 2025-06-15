/**
 * 날짜 포맷팅 유틸리티
 */
export const formatDate = (dateString, options = {}) => {
  if (!dateString) return '실행 없음';

  const defaultOptions = {
    month: 'short',
    day: 'numeric',
  };

  const date = new Date(dateString);
  return date.toLocaleDateString('ko-KR', { ...defaultOptions, ...options });
};

/**
 * 숫자 포맷팅 (천 단위 콤마)
 */
export const formatNumber = number => {
  if (typeof number !== 'number') return '0';
  return number.toLocaleString('ko-KR');
};

/**
 * 기간 포맷팅
 */
export const formatPeriod = (startDate, endDate) => {
  const start = new Date(startDate).toLocaleDateString('ko-KR');
  const end = new Date(endDate).toLocaleDateString('ko-KR');
  return `${start} ~ ${end}`;
};

/**
 * 상태 텍스트 매핑
 */
export const getStatusText = (status, statusMap = {}) => {
  const defaultStatusMap = {
    draft: '임시저장',
    scheduled: '예정',
    active: '진행중',
    completed: '완료',
    cancelled: '취소',
    published: '게시됨',
  };

  const combinedMap = { ...defaultStatusMap, ...statusMap };
  return combinedMap[status] || status;
};

/**
 * 상태 뱃지 타입 매핑
 */
export const getStatusBadgeType = (status, typeMap = {}) => {
  const defaultTypeMap = {
    draft: 'neutral',
    scheduled: 'warning',
    active: 'success',
    completed: 'info',
    cancelled: 'error',
    published: 'success',
  };

  const combinedMap = { ...defaultTypeMap, ...typeMap };
  return combinedMap[status] || 'neutral';
};
