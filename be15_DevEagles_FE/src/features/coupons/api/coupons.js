import api from '@/plugins/axios.js';
import { getApiLogger } from '@/plugins/LoggerManager.js';
import { useAuthStore } from '@/store/auth.js';

const logger = getApiLogger('CouponsAPI');

const BASE_URL = '/coupons';

class CouponsAPI {
  async createCoupon(couponData) {
    try {
      const authStore = useAuthStore();
      const requestData = {
        couponTitle: couponData.name,
        shopId: Number(authStore.shopId),
        primaryItemId: couponData.primaryItemId !== null ? Number(couponData.primaryItemId) : null,
        secondaryItemId:
          couponData.secondaryItemId !== null ? Number(couponData.secondaryItemId) : null,
        discountRate: couponData.discount !== null ? Number(couponData.discount) : null,
        expirationDate: couponData.expiryDate
          ? new Date(couponData.expiryDate).toISOString().split('T')[0]
          : null,
        isActive: couponData.isActive || false,
        staffId: couponData.staffId !== null ? Number(couponData.staffId) : null,
      };

      logger.request('POST', BASE_URL, requestData);
      const response = await api.post(BASE_URL, requestData);
      logger.response('POST', BASE_URL, response.status, response.data);

      return response.data.data;
    } catch (error) {
      logger.error('POST', BASE_URL, error);
      throw this.handleApiError(error);
    }
  }

  // 쿠폰 목록 조회 (검색)
  async getCoupons(searchParams = {}) {
    try {
      const params = {
        sortBy: searchParams.sortBy || 'createdAt',
        sortDirection: searchParams.sortDirection || 'desc',
      };

      // Conditionally add page and size if they exist in searchParams
      if (searchParams.page !== undefined) {
        params.page = searchParams.page;
      }
      if (searchParams.size !== undefined) {
        params.size = searchParams.size;
      }

      // Add any other searchParams that are not page, size, sortBy, sortDirection
      for (const key in searchParams) {
        if (!['page', 'size', 'sortBy', 'sortDirection'].includes(key)) {
          params[key] = searchParams[key];
        }
      }

      logger.request('GET', BASE_URL, params);
      logger.debug('getCoupons - Request Params:', params);
      const response = await api.get(BASE_URL, { params });
      logger.response('GET', BASE_URL, response.status, response.data);

      // 응답 구조 확인 및 안전한 파싱
      if (!response.data || !response.data.data) {
        throw new Error('잘못된 응답 구조입니다.');
      }

      const data = response.data.data;

      // content와 pagination 정보 추출
      const content = Array.isArray(data.content) ? data.content : [];
      const pagination = data.pagination || {};

      return {
        content: content,
        totalElements: pagination.totalItems ?? data.totalItems ?? 0,
        totalPages: pagination.totalPages ?? data.totalPages ?? 0,
        page: pagination.page ?? data.page ?? params.page,
        size: pagination.size ?? data.size ?? params.size,
        isFirst: pagination.isFirst ?? data.isFirst ?? params.page === 0,
        isLast: pagination.isLast ?? data.isLast ?? false,
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

      return response.data.data;
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

      return response.data.data;
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

      return response.data.data;
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
