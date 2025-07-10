import api from '@/plugins/axios.js';
import { getApiLogger } from '@/plugins/LoggerManager.js';

const logger = getApiLogger('CustomersAPI');

const BASE_URL = '/customers';

class CustomersAPI {
  /**
   * 매장별 고객 전체 목록 조회 (페이징 없음)
   * @returns {Promise<Array>} 고객 리스트 (프론트 형식)
   */
  async getCustomersByShop() {
    try {
      const url = `${BASE_URL}/list`;

      logger.request('GET', url);
      const response = await api.get(url);
      logger.response('GET', url, response.status, response.data);

      return Array.isArray(response.data?.data) ? response.data.data : [];
    } catch (error) {
      logger.error('GET', `${BASE_URL}/list`, error);
      throw this.handleApiError(error);
    }
  }

  /**
   * 고객 상세 정보 조회
   * @param {number} customerId 고객 ID
   */
  async getCustomerDetail(customerId) {
    try {
      const url = `${BASE_URL}/${customerId}`;

      logger.request('GET', url);
      const response = await api.get(url);
      logger.response('GET', url, response.status, response.data);

      const detail = response.data?.data;
      if (!detail) return null;

      // 백엔드 응답을 프론트엔드 형식으로 변환
      return {
        customerId: detail.customerId,
        customerName: detail.customerName,
        phoneNumber: detail.phoneNumber,
        gender: detail.gender,
        birthdate: detail.birthdate,
        staff: detail.staff
          ? { staffId: detail.staff.staffId, staffName: detail.staff.staffName }
          : undefined,
        customerGrade: detail.customerGrade
          ? {
              customerGradeId: detail.customerGrade.customerGradeId,
              customerGradeName: detail.customerGrade.customerGradeName,
              discountRate: detail.customerGrade.discountRate,
            }
          : undefined,
        acquisitionChannel: detail.acquisitionChannel
          ? {
              acquisitionChannelId: detail.acquisitionChannel.acquisitionChannelId,
              acquisitionChannelName: detail.acquisitionChannel.acquisitionChannelName,
            }
          : undefined,
        tags: detail.tags || [],
        memo: detail.memo,
        marketingConsent: detail.marketingConsent || false,
        notificationConsent: detail.notificationConsent || false,
        lastMessageSentAt: detail.lastMessageSentAt,
        marketingConsentedAt: detail.marketingConsentedAt,
        modifiedAt: detail.modifiedAt,
        shopId: detail.shopId,
        createdAt: detail.createdAt,
        recentVisitDate: detail.recentVisitDate,
        visitCount: detail.visitCount || 0,
        noshowCount: detail.noshowCount || 0,
        totalRevenue: detail.totalRevenue || 0,
        remainingPrepaidAmount: detail.remainingPrepaidAmount || 0,
        customerSessionPasses: [], // TODO: 백엔드에서 추가 필요
      };
    } catch (error) {
      logger.error('GET', `${BASE_URL}/${customerId}`, error);
      throw this.handleApiError(error);
    }
  }

  /**
   * 고객 생성
   * @param {object} customerData 프론트 입력 데이터
   */
  async createCustomer(customerData) {
    try {
      logger.request('POST', BASE_URL, customerData);
      const response = await api.post(BASE_URL, customerData);
      logger.response('POST', BASE_URL, response.status, response.data);
      return response.data?.data ?? null;
    } catch (error) {
      logger.error('POST', BASE_URL, error);
      throw this.handleApiError(error);
    }
  }

  /**
   * 고객 수정
   */
  async updateCustomer(customerId, updateData) {
    try {
      if (!customerId) {
        throw new Error('customerId is required for updating.');
      }
      const url = `${BASE_URL}/${customerId}`;
      logger.request('PUT', url, updateData);
      const response = await api.put(url, updateData);
      logger.response('PUT', url, response.status, response.data);
      return response.data?.data ?? null;
    } catch (error) {
      logger.error('PUT', `${BASE_URL}/${customerId}`, error);
      throw this.handleApiError(error);
    }
  }

  /**
   * 고객 삭제 (soft delete)
   */
  async deleteCustomer(customerId) {
    try {
      const url = `${BASE_URL}/${customerId}`;
      logger.request('DELETE', url);
      const response = await api.delete(url);
      logger.response('DELETE', url, response.status, response.data);
      return response.data;
    } catch (error) {
      logger.error('DELETE', `${BASE_URL}/${customerId}`, error);
      throw this.handleApiError(error);
    }
  }

  /**
   * 고객에게 태그 추가
   * @param {number} customerId 고객 ID
   * @param {number} tagId 태그 ID
   */
  async addTagToCustomer(customerId, tagId) {
    try {
      const url = `${BASE_URL}/${customerId}/tags/${tagId}`;
      logger.request('POST', url);
      const response = await api.post(url);
      logger.response('POST', url, response.status, response.data);
      // 태그 추가 후 고객 정보 갱신
      await this.getCustomerDetail(customerId);
      return response.data;
    } catch (error) {
      logger.error('POST', `${BASE_URL}/${customerId}/tags/${tagId}`, error);
      throw this.handleApiError(error);
    }
  }

  /**
   * 고객에게서 태그 제거
   * @param {number} customerId 고객 ID
   * @param {number} tagId 태그 ID
   */
  async removeTagFromCustomer(customerId, tagId) {
    try {
      const url = `${BASE_URL}/${customerId}/tags/${tagId}`;
      logger.request('DELETE', url);
      const response = await api.delete(url);
      logger.response('DELETE', url, response.status, response.data);
      // 태그 제거 후 고객 정보 갱신
      await this.getCustomerDetail(customerId);
      return response.data;
    } catch (error) {
      logger.error('DELETE', `${BASE_URL}/${customerId}/tags/${tagId}`, error);
      throw this.handleApiError(error);
    }
  }

  /**
   * 백엔드 CustomerListResponse -> 프론트 데이터 포맷 변환
   */

  /**
   * API 에러 처리 공통 함수
   */
  handleApiError(error) {
    if (error.response) {
      const { status, data } = error.response;
      switch (status) {
        case 400:
          return new Error(data.message || '잘못된 요청입니다.');
        case 404:
          return new Error(data.message || '고객을 찾을 수 없습니다.');
        case 409:
          return new Error(data.message || '이미 존재하는 고객입니다.');
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

  /**
   * 고객명/전화번호 자동완성
   * @param {string} prefix 검색어 접두사
   * @returns {Promise<string[]>} 제안 문자열 배열
   */
  async autocomplete(prefix) {
    try {
      const url = `${BASE_URL}/elasticsearch/autocomplete`;
      const params = { prefix };
      logger.request('GET', url, params);
      const response = await api.get(url, { params });
      logger.response('GET', url, response.status, response.data);
      return Array.isArray(response.data?.data) ? response.data.data : [];
    } catch (error) {
      logger.error('GET', `${BASE_URL}/elasticsearch/autocomplete`, error);
      throw this.handleApiError(error);
    }
  }

  /**
   * 키워드로 고객 간단 검색 (페이징 없음)
   * @param {string} keyword 검색 키워드
   * @returns {Promise<Array>} 검색 결과 리스트 (간략 정보)
   */
  async searchByKeyword(keyword) {
    try {
      const url = `${BASE_URL}/elasticsearch/search`;
      const params = { keyword };
      logger.request('GET', url, params);
      const response = await api.get(url, { params });
      logger.response('GET', url, response.status, response.data);
      const results = Array.isArray(response.data?.data) ? response.data.data : [];
      return results.map(r => ({
        customerId: r.customerId || r.customer_id,
        customer_id: r.customerId || r.customer_id,
        customer_name: r.customerName || r.customer_name,
        phone_number: r.phoneNumber || r.phone_number,
        customer_grade_id: r.customerGradeId || r.customer_grade_id,
        customer_grade_name: r.customerGradeName || r.customer_grade_name,
        gender: r.gender,
      }));
    } catch (error) {
      logger.error('GET', `${BASE_URL}/elasticsearch/search`, error);
      throw this.handleApiError(error);
    }
  }
}

// 싱글톤 인스턴스
const customersAPI = new CustomersAPI();
export default customersAPI;
