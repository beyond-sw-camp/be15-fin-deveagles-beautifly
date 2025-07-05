/**
 * 쿠폰 데이터 변환 서비스
 * 단일 책임: 백엔드-프론트엔드 데이터 형식 변환
 */
class CouponDataTransformer {
  constructor(mappingConfig = {}) {
    this.categoryMapping = mappingConfig.categoryMapping || this.getDefaultCategoryMapping();
    this.productMapping = mappingConfig.productMapping || this.getDefaultProductMapping();
    this.staffMapping = mappingConfig.staffMapping || this.getDefaultStaffMapping();
  }

  /**
   * 백엔드 데이터를 프론트엔드 형식으로 변환
   */
  transformFromBackend(backendData) {
    if (!backendData) return null;

    return {
      id: backendData.id,
      name: backendData.couponTitle,
      couponCode: backendData.couponCode,
      category: this.mapItemIdToCategory(backendData.primaryItemId),
      designer: this.mapStaffIdToDesigner(backendData.staffId),
      product: this.mapItemIdToProduct(backendData.primaryItemId),
      primaryProduct: this.mapItemIdToProduct(backendData.primaryItemId),
      secondaryProduct: this.mapItemIdToProduct(backendData.secondaryItemId),
      discount: backendData.discountRate,
      expiryDate: backendData.expirationDate,
      isActive: backendData.isActive,
      createdAt: backendData.createdAt,
      isExpired: backendData.isExpired,
      isDeleted: backendData.isDeleted,
      shopId: backendData.shopId,
      staffId: backendData.staffId,
      primaryItemId: backendData.primaryItemId,
      secondaryItemId: backendData.secondaryItemId,
    };
  }

  /**
   * 프론트엔드 데이터를 백엔드 형식으로 변환
   */
  transformToBackend(frontendData) {
    if (!frontendData) return null;

    return {
      couponTitle: frontendData.name,
      shopId: frontendData.shopId || 1,
      staffId: frontendData.staffId || null,
      primaryItemId:
        frontendData.primaryItemId || this.mapProductToItemId(frontendData.primaryProduct),
      secondaryItemId:
        frontendData.secondaryItemId ||
        (frontendData.secondaryProduct
          ? this.mapProductToItemId(frontendData.secondaryProduct)
          : null),
      discountRate: frontendData.discount,
      expirationDate: frontendData.expiryDate
        ? new Date(frontendData.expiryDate).toISOString().split('T')[0]
        : null,
      isActive: frontendData.isActive || false,
    };
  }

  // 카테고리 매핑
  mapItemIdToCategory(itemId) {
    if (!itemId) return '기타';
    return this.categoryMapping.getCategory(itemId);
  }

  // 상품 매핑
  mapItemIdToProduct(itemId) {
    if (!itemId) return null;
    return this.productMapping.getProduct(itemId);
  }

  mapProductToItemId(product) {
    if (!product) return null;
    return this.productMapping.getItemId(product);
  }

  // 직원 매핑
  mapStaffIdToDesigner(staffId) {
    if (!staffId) return '전체';
    return this.staffMapping.getDesigner(staffId);
  }

  mapDesignerToStaffId(designer) {
    if (!designer || designer === '전체') return null;
    return this.staffMapping.getStaffId(designer);
  }

  // 기본 매핑 설정
  getDefaultCategoryMapping() {
    return {
      getCategory: itemId => (itemId <= 50 ? '시술' : '상품'),
    };
  }

  getDefaultProductMapping() {
    const serviceMapping = {
      1: '헤어컷',
      2: '펌',
      3: '염색',
      4: '네일아트',
      5: '메이크업',
      6: '피부관리',
    };
    const productMapping = {
      51: '트리트먼트',
      52: '헤드스파',
      53: '네일케어',
      54: '아이브로우',
      55: '마사지',
    };
    const reverseMapping = {
      ...Object.fromEntries(Object.entries(serviceMapping).map(([k, v]) => [v, parseInt(k)])),
      ...Object.fromEntries(Object.entries(productMapping).map(([k, v]) => [v, parseInt(k)])),
    };

    return {
      getProduct: itemId => serviceMapping[itemId] || productMapping[itemId] || '기타 상품',
      getItemId: product => reverseMapping[product] || 1,
    };
  }

  getDefaultStaffMapping() {
    const mapping = {
      1: '김미영',
      2: '박지은',
      3: '이수진',
      4: '최민호',
      5: '정하나',
    };
    const reverseMapping = Object.fromEntries(
      Object.entries(mapping).map(([k, v]) => [v, parseInt(k)])
    );

    return {
      getDesigner: staffId => mapping[staffId] || `직원 ${staffId}`,
      getStaffId: designer => reverseMapping[designer] || null,
    };
  }
}

export default CouponDataTransformer;
