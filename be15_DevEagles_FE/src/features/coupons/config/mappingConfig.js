/**
 * 쿠폰 관련 매핑 설정
 * 하드코딩된 값들을 중앙 집중식으로 관리
 */

// 카테고리 매핑 설정
export const CATEGORY_CONFIG = {
  THRESHOLD: 50,
  TYPES: {
    SERVICE: '시술',
    PRODUCT: '상품',
  },
};

// 기본값 설정
export const DEFAULT_VALUES = {
  SHOP_ID: 1, // TODO: getCurrentShopId() 함수로 교체 필요
  PRIMARY_ITEM_ID: 1, // TODO: getDefaultItemId() 함수로 교체 필요
  PAGE_SIZE: 10,
  SORT_BY: 'createdAt',
  SORT_DIRECTION: 'desc',
};

// 시술 서비스 매핑 (ID 1-50)
export const SERVICE_MAPPING = {
  1: '헤어컷',
  2: '펌',
  3: '염색',
  4: '네일아트',
  5: '메이크업',
  6: '피부관리',
  7: '헤어트리트먼트',
  8: '스타일링',
  9: '코팅',
  10: '마사지',
};

// 상품 매핑 (ID 51-100)
export const PRODUCT_MAPPING = {
  51: '트리트먼트',
  52: '헤드스파',
  53: '네일케어',
  54: '아이브로우',
  55: '마사지',
  56: '샴푸',
  57: '컨디셔너',
  58: '에센스',
  59: '토너',
  60: '크림',
};

// 직원 매핑
export const STAFF_MAPPING = {
  1: '김미영',
  2: '박지은',
  3: '이수진',
  4: '최민호',
  5: '정하나',
  6: '이민수',
  7: '최지영',
  8: '박서연',
  9: '김태호',
  10: '이소영',
};

// 매핑 헬퍼 함수들
export const MappingHelpers = {
  /**
   * itemId를 카테고리로 변환
   */
  getCategory(itemId) {
    if (!itemId) return '기타';
    return itemId <= CATEGORY_CONFIG.THRESHOLD
      ? CATEGORY_CONFIG.TYPES.SERVICE
      : CATEGORY_CONFIG.TYPES.PRODUCT;
  },

  /**
   * itemId를 상품명으로 변환
   */
  getProductName(itemId) {
    if (!itemId) return null;
    return SERVICE_MAPPING[itemId] || PRODUCT_MAPPING[itemId] || '기타 상품';
  },

  /**
   * staffId를 직원명으로 변환
   */
  getStaffName(staffId) {
    if (!staffId) return '전체';
    return STAFF_MAPPING[staffId] || `직원 ${staffId}`;
  },

  /**
   * 상품명을 itemId로 역변환
   */
  getItemIdByProductName(productName) {
    if (!productName) return null;

    // 시술 매핑에서 검색
    const serviceEntry = Object.entries(SERVICE_MAPPING).find(([id, name]) => name === productName);
    if (serviceEntry) return parseInt(serviceEntry[0]);

    // 상품 매핑에서 검색
    const productEntry = Object.entries(PRODUCT_MAPPING).find(([id, name]) => name === productName);
    if (productEntry) return parseInt(productEntry[0]);

    return DEFAULT_VALUES.PRIMARY_ITEM_ID;
  },

  /**
   * 직원명을 staffId로 역변환
   */
  getStaffIdByName(staffName) {
    if (!staffName || staffName === '전체') return null;

    const entry = Object.entries(STAFF_MAPPING).find(([id, name]) => name === staffName);
    return entry ? parseInt(entry[0]) : null;
  },

  /**
   * 현재 매장 ID 반환 (외부에서 전달받음)
   * @param {number|null} shopId - 현재 매장 ID (pinia auth store에서 전달)
   */
  getCurrentShopId(shopId = null) {
    return shopId || DEFAULT_VALUES.SHOP_ID;
  },

  /**
   * 기본 상품 ID 반환 (TODO: 실제 구현 필요)
   */
  getDefaultItemId() {
    // TODO: 매장별 기본 상품 설정에서 가져오기
    return DEFAULT_VALUES.PRIMARY_ITEM_ID;
  },
};

// 전체 매핑 정보 내보내기 (디버깅용)
export const getAllMappings = () => ({
  categories: CATEGORY_CONFIG,
  defaults: DEFAULT_VALUES,
  services: SERVICE_MAPPING,
  products: PRODUCT_MAPPING,
  staff: STAFF_MAPPING,
});

export default {
  CATEGORY_CONFIG,
  DEFAULT_VALUES,
  SERVICE_MAPPING,
  PRODUCT_MAPPING,
  STAFF_MAPPING,
  MappingHelpers,
  getAllMappings,
};
