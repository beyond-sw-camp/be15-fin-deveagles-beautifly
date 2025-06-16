/**
 * 차트 관련 상수 정의
 * 매직 넘버와 하드코딩된 값들을 상수로 관리
 */

// 서비스 카테고리 정보
export const SERVICE_CATEGORIES = {
  HAIR: {
    name: '헤어 서비스',
    color: '#FFD700',
    dataKey: 'hair',
  },
  NAIL: {
    name: '네일 아트',
    color: '#FF69B4',
    dataKey: 'nail',
  },
  SKINCARE: {
    name: '스킨케어',
    color: '#00BFFF',
    dataKey: 'skincare',
  },
  MAKEUP: {
    name: '메이크업',
    color: '#1E90FF',
    dataKey: 'makeup',
  },
  OTHER: {
    name: '기타',
    color: '#00CED1',
    dataKey: 'other',
  },
};

// 차트 색상 팔레트
export const CHART_COLORS = {
  PRIMARY: ['#FFD700', '#FF69B4', '#00BFFF', '#1E90FF', '#00CED1'],
  SECONDARY: ['#3B82F6', '#10B981', '#F59E0B', '#EF4444', '#8B5CF6', '#EC4899'],
  GRADIENT: {
    MONTHLY_BAR: [
      { offset: 0, color: '#FF6B6B' },
      { offset: 0.5, color: '#4ECDC4' },
      { offset: 1, color: '#45B7D1' },
    ],
  },
};

// 차트 레이아웃 설정
export const CHART_LAYOUT = {
  GRID: {
    TOP: 80,
    LEFT: 50,
    RIGHT: 50,
    BOTTOM: 50,
  },
  LEGEND: {
    TOP: 35,
    BOTTOM: 10,
    ITEM_GAP: 20,
    ITEM_WIDTH: 12,
    ITEM_HEIGHT: 12,
  },
  PIE: {
    RADIUS: ['40%', '70%'],
    CENTER: ['50%', '45%'],
  },
};

// 툴팁 스타일
export const TOOLTIP_STYLES = {
  LIGHT: {
    backgroundColor: 'rgba(255, 255, 255, 0.95)',
    borderColor: '#E5E7EB',
    textColor: '#374151',
    borderWidth: 1,
    borderRadius: 12,
    padding: [12, 16],
    extraCssText: 'box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1); backdrop-filter: blur(10px);',
  },
  DARK: {
    backgroundColor: 'rgba(31, 41, 55, 0.95)',
    borderColor: '#4B5563',
    textColor: '#F9FAFB',
    borderWidth: 1,
    borderRadius: 12,
    padding: [12, 16],
    extraCssText: 'box-shadow: 0 10px 25px rgba(0, 0, 0, 0.3); backdrop-filter: blur(10px);',
  },
};

// 축 스타일
export const AXIS_STYLES = {
  LIGHT: {
    axisLabel: { color: '#6B7280', fontSize: 11 },
    axisLine: { color: '#E5E7EB' },
    axisTick: { color: '#E5E7EB' },
    splitLine: { color: '#F3F4F6' },
  },
  DARK: {
    axisLabel: { color: '#D1D5DB', fontSize: 11 },
    axisLine: { color: '#4B5563' },
    axisTick: { color: '#4B5563' },
    splitLine: { color: '#374151' },
  },
};

// 필터 옵션
export const FILTER_OPTIONS = {
  PERIOD: [
    { value: '7d', label: '최근 7일' },
    { value: '30d', label: '최근 30일' },
    { value: '90d', label: '최근 90일' },
    { value: '1y', label: '최근 1년' },
  ],
  CATEGORY: [
    { value: 'all', label: '전체 서비스' },
    { value: 'hair', label: '헤어 서비스' },
    { value: 'nail', label: '네일 아트' },
    { value: 'skincare', label: '스킨케어' },
    { value: 'makeup', label: '메이크업' },
    { value: 'other', label: '기타 서비스' },
  ],
};

// 애니메이션 설정
export const ANIMATION = {
  DURATION: 300,
  EASING: 'ease-in-out',
  DELAY: 50,
};

// 반응형 브레이크포인트
export const BREAKPOINTS = {
  MOBILE: 768,
  TABLET: 1024,
  DESKTOP: 1280,
};
