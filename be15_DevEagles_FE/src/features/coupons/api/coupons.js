import api from '@/plugins/axios.js';
import { getApiLogger } from '@/plugins/LoggerManager.js';
import { MappingHelpers, DEFAULT_VALUES } from '../config/mappingConfig.js';
import { useAuthStore } from '@/store/auth.js';

const logger = getApiLogger('CouponsAPI');

const BASE_URL = '/coupons';

class CouponsAPI {
  async createCoupon(couponData) {
    try {
      const authStore = useAuthStore();
      const requestData = {
        couponTitle: couponData.name,
        shopId: couponData.shopId || MappingHelpers.getCurrentShopId(authStore.shopId),
        staffId: couponData.staffId || MappingHelpers.getStaffIdByName(couponData.designer),
        primaryItemId:
          couponData.primaryItemId ||
          MappingHelpers.getItemIdByProductName(couponData.primaryProduct) ||
          MappingHelpers.getDefaultItemId(),
        secondaryItemId:
          couponData.secondaryItemId ||
          MappingHelpers.getItemIdByProductName(couponData.secondaryProduct),
        discountRate: couponData.discount,
        expirationDate: couponData.expiryDate
          ? new Date(couponData.expiryDate).toISOString().split('T')[0]
          : null,
        isActive: couponData.isActive || false,
      };

      logger.request('POST', BASE_URL, requestData);
      const response = await api.post(BASE_URL, requestData);
      logger.response('POST', BASE_URL, response.status, response.data);

      return this.transformCouponData(response.data.data);
    } catch (error) {
      logger.error('POST', BASE_URL, error);
      throw this.handleApiError(error);
    }
  }

  // 쿠폰 목록 조회 (검색)
  async getCoupons(searchParams = {}) {
    try {
      const params = {
        page: searchParams.page || 0,
        size: searchParams.size || DEFAULT_VALUES.PAGE_SIZE,
        sortBy: searchParams.sortBy || DEFAULT_VALUES.SORT_BY,
        sortDirection: searchParams.sortDirection || DEFAULT_VALUES.SORT_DIRECTION,
        ...searchParams,
      };

      logger.request('GET', BASE_URL, params);
      const response = await api.get(BASE_URL, { params });
      logger.response('GET', BASE_URL, response.status, response.data);

      const pagedData = response.data.data;
      return {
        content: pagedData.content.map(coupon => this.transformCouponData(coupon)),
        totalElements: pagedData.totalElements,
        totalPages: pagedData.totalPages,
        page: pagedData.page,
        size: pagedData.size,
      };
    } catch (error) {
      logger.error('GET', BASE_URL, error);
      throw this.handleApiError(error);
    }
  }

  // 쿠폰 ID로 조회
  async getCouponById(id) {
    try {
      const url = `${BASE_URL}/${id}`;

      logger.request('GET', url);
      const response = await api.get(url);
      logger.response('GET', url, response.status, response.data);

      return this.transformCouponData(response.data.data);
    } catch (error) {
      logger.error('GET', `${BASE_URL}/${id}`, error);
      throw this.handleApiError(error);
    }
  }

  // 쿠폰 코드로 조회
  async getCouponByCode(couponCode) {
    try {
      const url = `${BASE_URL}/code/${couponCode}`;

      logger.request('GET', url);
      const response = await api.get(url);
      logger.response('GET', url, response.status, response.data);

      return this.transformCouponData(response.data.data);
    } catch (error) {
      logger.error('GET', `${BASE_URL}/code/${couponCode}`, error);
      throw this.handleApiError(error);
    }
  }

  // 쿠폰 삭제
  async deleteCoupon(id) {
    try {
      const url = `${BASE_URL}/${id}`;

      logger.request('DELETE', url);
      const response = await api.delete(url);
      logger.response('DELETE', url, response.status, response.data);

      return response.data;
    } catch (error) {
      logger.error('DELETE', `${BASE_URL}/${id}`, error);
      throw this.handleApiError(error);
    }
  }

  // 쿠폰 상태 토글
  async toggleCouponStatus(id) {
    try {
      const url = `${BASE_URL}/${id}/toggle`;

      logger.request('PATCH', url);
      const response = await api.patch(url);
      logger.response('PATCH', url, response.status, response.data);

      return this.transformCouponData(response.data.data);
    } catch (error) {
      logger.error('PATCH', `${BASE_URL}/${id}/toggle`, error);
      throw this.handleApiError(error);
    }
  }

  // 쿠폰 유효성 검증
  async validateCoupon(validationData) {
    try {
      const url = `${BASE_URL}/validation/validate`;

      logger.request('POST', url, validationData);
      const response = await api.post(url, validationData);
      logger.response('POST', url, response.status, response.data);

      return response.data.data;
    } catch (error) {
      logger.error('POST', `${BASE_URL}/validation/validate`, error);
      throw this.handleApiError(error);
    }
  }

  // 쿠폰 적용 시뮬레이션
  async applyCouponSimulation(applicationData) {
    try {
      const url = `${BASE_URL}/validation/apply-simulation`;

      logger.request('POST', url, applicationData);
      const response = await api.post(url, applicationData);
      logger.response('POST', url, response.status, response.data);

      return response.data.data;
    } catch (error) {
      logger.error('POST', `${BASE_URL}/validation/apply-simulation`, error);
      throw this.handleApiError(error);
    }
  }

  // 백엔드 데이터를 프론트엔드 형식으로 변환
  transformCouponData(backendData) {
    return {
      id: backendData.id,
      name: backendData.couponTitle,
      couponCode: backendData.couponCode,
      category: MappingHelpers.getCategory(backendData.primaryItemId),
      designer: MappingHelpers.getStaffName(backendData.staffId),
      product: MappingHelpers.getProductName(backendData.primaryItemId),
      primaryProduct: MappingHelpers.getProductName(backendData.primaryItemId),
      secondaryProduct: MappingHelpers.getProductName(backendData.secondaryItemId),
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

  // 에러 처리
  handleApiError(error) {
    if (error.response) {
      const { status, data } = error.response;

      switch (status) {
        case 400:
          return new Error(data.message || '잘못된 요청입니다.');
        case 404:
          return new Error('쿠폰을 찾을 수 없습니다.');
        case 409:
          return new Error('이미 삭제된 쿠폰입니다.');
        case 500:
          return new Error('서버 오류가 발생했습니다.');
        default:
          return new Error(data.message || '알 수 없는 오류가 발생했습니다.');
      }
    } else if (error.request) {
      return new Error('네트워크 오류가 발생했습니다.');
    } else {
      return new Error(error.message || '오류가 발생했습니다.');
    }
  }
}

// 싱글톤 인스턴스 생성
const couponsAPI = new CouponsAPI();

export default couponsAPI;
