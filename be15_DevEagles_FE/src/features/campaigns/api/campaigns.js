import api from '@/plugins/axios.js';
import { getApiLogger } from '@/plugins/LoggerManager.js';
// (no direct usage of formatDate in this module)

const logger = getApiLogger('CampaignsAPI');

const BASE_URL = '/campaigns';

class CampaignsAPI {
  /**
   * 캠페인 생성
   * @param {object} campaignData - 프론트 입력 데이터
   */
  async createCampaign(campaignData) {
    try {
      const requestData = this.mapToBackendCreateRequest(campaignData);

      logger.request('POST', BASE_URL, requestData);
      const response = await api.post(BASE_URL, requestData);
      logger.response('POST', BASE_URL, response.status, response.data);

      return this.transformCampaignData(response.data.data);
    } catch (error) {
      logger.error('POST', BASE_URL, error);
      throw this.handleApiError(error);
    }
  }

  /**
   * 매장별 캠페인 목록 조회
   * @param {object} params - { shopId, page, size }
   */
  async getCampaigns(params = {}) {
    try {
      const query = {
        shopId: params.shopId,
        page: params.page || 0,
        size: params.size || 10,
      };

      logger.request('GET', BASE_URL, query);
      const response = await api.get(BASE_URL, { params: query });
      logger.response('GET', BASE_URL, response.status, response.data);

      const pagedData = response.data.data;
      return {
        content: pagedData.content.map(c => this.transformCampaignData(c)),
        totalElements: pagedData.pagination?.totalItems,
        totalPages: pagedData.pagination?.totalPages,
        page: pagedData.pagination?.currentPage,
        size: params.size || 10,
      };
    } catch (error) {
      logger.error('GET', BASE_URL, error);
      throw this.handleApiError(error);
    }
  }

  /**
   * 캠페인 상세 조회
   * @param {number} id
   */
  async getCampaignById(id) {
    try {
      const url = `${BASE_URL}/${id}`;
      logger.request('GET', url);
      const response = await api.get(url);
      logger.response('GET', url, response.status, response.data);

      return this.transformCampaignData(response.data.data);
    } catch (error) {
      logger.error('GET', `${BASE_URL}/${id}`, error);
      throw this.handleApiError(error);
    }
  }

  /**
   * 캠페인 삭제 (Soft delete on backend)
   */
  async deleteCampaign(id) {
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

  /**
   * 백엔드 Campaign 응답 -> 프론트 형식 변환
   */
  transformCampaignData(data) {
    const today = new Date();
    const start = new Date(data.startDate);
    const end = new Date(data.endDate);

    let status = 'draft';
    if (data.isActive) {
      status = 'active';
    } else if (end < today) {
      status = 'completed';
    } else if (start > today) {
      status = 'scheduled';
    }

    return {
      id: data.id,
      name: data.campaignTitle,
      description: data.description,
      startDate: data.startDate,
      endDate: data.endDate,
      createdAt: data.createdAt,
      status,
      couponId: data.couponId,
      staffId: data.staffId,
      templateId: data.templateId,
      customerGradeId: data.customerGradeId,
      tagId: data.tagId,
      shopId: data.shopId,
      isActive: data.isActive,
    };
  }

  /**
   * 프론트 입력 -> 백엔드 생성 요청 DTO 변환
   */
  mapToBackendCreateRequest(form) {
    // 필요한 값 매핑. 실제 구현에서는 매핑 헬퍼를 통해 id 변환 등이 필요할 수 있음
    return {
      campaignTitle: form.name,
      description: form.description,
      startDate: form.startDate,
      endDate: form.endDate,
      messageSendAt: form.messageSendAt || new Date().toISOString(),
      staffId: form.staffId || 1,
      templateId: form.templateId || 1,
      couponId: form.couponId || 1,
      customerGradeId: form.customerGradeId || 1,
      tagId: form.tagId || 1,
      shopId: form.shopId || 1,
    };
  }

  /**
   * 공통 에러 처리
   */
  handleApiError(error) {
    if (error.response) {
      const { status, data } = error.response;
      switch (status) {
        case 400:
          return new Error(data.message || '잘못된 요청입니다.');
        case 404:
          return new Error(data.message || '캠페인을 찾을 수 없습니다.');
        case 409:
          return new Error(data.message || '이미 존재하는 캠페인입니다.');
        case 500:
          return new Error('서버 오류가 발생했습니다.');
        default:
          return new Error(data.message || '알 수 없는 오류가 발생했습니다.');
      }
    }
    return new Error('네트워크 오류가 발생했습니다.');
  }
}

export default new CampaignsAPI();
