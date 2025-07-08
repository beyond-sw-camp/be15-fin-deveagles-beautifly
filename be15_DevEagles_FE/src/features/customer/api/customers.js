import api from '@/plugins/axios.js';
import { getApiLogger } from '@/plugins/LoggerManager.js';

const logger = getApiLogger('CustomersAPI');

const BASE_URL = '/customers';

class CustomersAPI {
  /**
   * 매장별 고객 전체 목록 조회 (페이징 없음)
   * @param {number} shopId 매장 ID
   * @returns {Promise<Array>} 고객 리스트 (프론트 형식)
   */
  async getCustomersByShop(shopId) {
    try {
      const url = `${BASE_URL}/shop/${shopId}`;

      logger.request('GET', url);
      const response = await api.get(url);
      logger.response('GET', url, response.status, response.data);

      return Array.isArray(response.data?.data) ? response.data.data : [];
    } catch (error) {
      logger.error('GET', `${BASE_URL}/shop/${shopId}`, error);
      throw this.handleApiError(error);
    }
  }

  /**
   * 고객 상세 정보 조회
   * @param {number} customerId 고객 ID
   * @param {number} shopId 매장 ID
   */
  async getCustomerDetail(customerId, shopId) {
    try {
      const url = `${BASE_URL}/${customerId}`;
      const params = { shopId };

      logger.request('GET', url, params);
      const response = await api.get(url, { params });
      logger.response('GET', url, response.status, response.data);

      return response.data?.data ?? null;
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
  async updateCustomer(customerId, updateData, shopId) {
    try {
      if (!customerId) {
        throw new Error('customerId is required for updating.');
      }
      const url = `${BASE_URL}/${customerId}`;
      const params = { shopId };
      logger.request('PUT', url, updateData, params);
      const response = await api.put(url, updateData, { params });
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
  async deleteCustomer(customerId, shopId) {
    try {
      const url = `${BASE_URL}/${customerId}`;
      const params = { shopId };
      logger.request('DELETE', url, params);
      const response = await api.delete(url, { params });
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
   * @param {number} shopId 매장 ID
   */
  async addTagToCustomer(customerId, tagId, shopId) {
    try {
      const url = `${BASE_URL}/${customerId}/tags/${tagId}`;
      const params = { shopId };
      logger.request('POST', url, params);
      const response = await api.post(url, null, { params });
      logger.response('POST', url, response.status, response.data);
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
   * @param {number} shopId 매장 ID
   */
  async removeTagFromCustomer(customerId, tagId, shopId) {
    try {
      const url = `${BASE_URL}/${customerId}/tags/${tagId}`;
      const params = { shopId };
      logger.request('DELETE', url, params);
      const response = await api.delete(url, { params });
      logger.response('DELETE', url, response.status, response.data);
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
   * @param {number} shopId 매장 ID
   * @returns {Promise<string[]>} 제안 문자열 배열
   */
  async autocomplete(prefix, shopId) {
    try {
      const url = `${BASE_URL}/elasticsearch/autocomplete`;
      const params = { prefix, shopId };
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
   * @param {number} shopId 매장 ID
   * @returns {Promise<Array>} 검색 결과 리스트 (간략 정보)
   */
  async searchByKeyword(keyword, shopId) {
    try {
      const url = `${BASE_URL}/elasticsearch/search`;
      const params = { keyword, shopId };
      logger.request('GET', url, params);
      const response = await api.get(url, { params });
      logger.response('GET', url, response.status, response.data);
      const results = Array.isArray(response.data?.data) ? response.data.data : [];
      return results.map(r => ({
        customer_id: r.customerId,
        customer_name: r.customerName,
        phone_number: r.phoneNumber,
        customer_grade_id: r.customerGradeId,
        customer_grade_name: r.customerGradeName,
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
