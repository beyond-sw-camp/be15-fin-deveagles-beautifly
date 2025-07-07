import api from '@/plugins/axios.js';
import { getApiLogger } from '@/plugins/LoggerManager.js';

const logger = getApiLogger('GradesAPI');

const BASE_URL = '/customers/grades';

class GradesAPI {
  /**
   * 매장별 고객등급 목록 조회
   * @param {number} shopId
   * @returns {Promise<Array<{id:number,name:string,discountRate:number}>>}
   */
  async getGradesByShop(shopId) {
    try {
      const url = `${BASE_URL}/shop/${shopId}`;
      logger.request('GET', url);
      const response = await api.get(url);
      logger.response('GET', url, response.status, response.data);
      const grades = Array.isArray(response.data?.data) ? response.data.data : [];
      return grades.map(g => ({
        id: g.id,
        name: g.customerGradeName,
        discountRate: g.discountRate,
      }));
    } catch (error) {
      logger.error('GET', `${BASE_URL}/shop/${shopId}`, error);
      throw new Error('고객등급 목록 조회 실패');
    }
  }
}

export default new GradesAPI();
