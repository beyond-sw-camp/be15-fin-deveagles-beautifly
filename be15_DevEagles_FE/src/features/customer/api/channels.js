import api from '@/plugins/axios.js';
import { getApiLogger } from '@/plugins/LoggerManager.js';

const logger = getApiLogger('ChannelsAPI');

const BASE_URL = '/customers/acquisition-channels';

const channelsAPI = {
  /**
   * 매장별 유입채널 목록 조회
   * @param {number} shopId 매장 ID
   * @returns {Promise<Array>} 유입채널 목록
   */
  async getChannelsByShop(shopId) {
    try {
      logger.request('GET', `${BASE_URL}/all`);
      const response = await api.get(`${BASE_URL}/all`);
      logger.response('GET', `${BASE_URL}/all`, response.status, response.data);
      return response.data?.data || [];
    } catch (error) {
      logger.error('GET', `${BASE_URL}/all`, error);
      throw new Error('유입채널 목록 조회 실패');
    }
  },

  /**
   * 새 유입채널 생성
   * @param {object} params 채널 정보
   */
  async createChannel(params) {
    try {
      logger.request('POST', BASE_URL, params);
      const response = await api.post(BASE_URL, params);
      logger.response('POST', BASE_URL, response.status, response.data);
      return response.data?.data || null;
    } catch (error) {
      logger.error('POST', BASE_URL, error);
      throw new Error('유입채널 생성 실패');
    }
  },

  /**
   * 유입채널 수정
   * @param {number} channelId 채널 ID
   * @param {object} params 수정할 정보
   */
  async updateChannel(channelId, params) {
    try {
      const url = `${BASE_URL}/${channelId}`;
      logger.request('PUT', url, params);
      const response = await api.put(url, params);
      logger.response('PUT', url, response.status, response.data);
      return response.data?.data || null;
    } catch (error) {
      logger.error('PUT', `${BASE_URL}/${channelId}`, error);
      throw new Error('유입채널 수정 실패');
    }
  },

  /**
   * 유입채널 삭제
   * @param {number} channelId 채널 ID
   */
  async deleteChannel(channelId) {
    try {
      const url = `${BASE_URL}/${channelId}`;
      logger.request('DELETE', url);
      const response = await api.delete(url);
      logger.response('DELETE', url, response.status, response.data);
      return response.data;
    } catch (error) {
      logger.error('DELETE', `${BASE_URL}/${channelId}`, error);
      throw new Error('유입채널 삭제 실패');
    }
  },
};

export default channelsAPI;
