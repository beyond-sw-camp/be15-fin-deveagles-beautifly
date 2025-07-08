import api from '@/plugins/axios.js';
import { getApiLogger } from '@/plugins/LoggerManager.js';

const logger = getApiLogger('GradesAPI');

const BASE_URL = '/customers/grades';

class GradesAPI {
  /**
   * 매장별 고객등급 목록 조회
   * @returns {Promise<Array<{id:number,name:string,discountRate:number}>>}
   */
  async getGradesByShop() {
    try {
      const url = `${BASE_URL}/all`;
      logger.request('GET', url);
      const response = await api.get(url);
      logger.response('GET', url, response.status, response.data);
      const grades = Array.isArray(response.data?.data) ? response.data.data : [];
      return grades.map(g => ({
        id: g.id,
        gradeId: g.id,
        name: g.customerGradeName,
        gradeName: g.customerGradeName,
        card: g.discountRate,
        cash: g.discountRate,
        naverpay: g.discountRate,
        localpay: g.discountRate,
        prepaid: g.discountRate,
        session_pass: g.discountRate,
        discountRate: g.discountRate,
        is_deletable: g.customerGradeName !== '기본등급',
      }));
    } catch (error) {
      logger.error('GET', `${BASE_URL}/all`, error);
      throw new Error('고객등급 목록 조회 실패');
    }
  }

  /**
   * 고객등급 생성
   * @param {object} payload { name:string, discountRate:number, shopId:number }
   * @returns {Promise<number>} 새로 생성된 gradeId
   */
  async createGrade({ name, discountRate, shopId }) {
    try {
      const url = `${BASE_URL}`;
      const body = {
        customerGradeName: name,
        discountRate,
        shopId,
      };
      logger.request('POST', url, body);
      const res = await api.post(url, body);
      logger.response('POST', url, res.status, res.data);
      return res.data?.data; // gradeId
    } catch (error) {
      logger.error('POST', BASE_URL, error);
      throw new Error('고객등급 생성 실패');
    }
  }

  /**
   * 고객등급 수정
   * @param {number} gradeId
   * @param {object} payload { name:string, discountRate:number, shopId:number }
   */
  async updateGrade(gradeId, { name, discountRate, shopId }) {
    try {
      const url = `${BASE_URL}/${gradeId}`;
      const body = {
        customerGradeName: name,
        discountRate,
        shopId,
      };
      logger.request('PUT', url, body);
      const res = await api.put(url, body);
      logger.response('PUT', url, res.status, res.data);
    } catch (error) {
      logger.error('PUT', `${BASE_URL}/${gradeId}`, error);
      throw new Error('고객등급 수정 실패');
    }
  }

  /**
   * 고객등급 삭제
   * @param {number} gradeId
   */
  async deleteGrade(gradeId) {
    try {
      const url = `${BASE_URL}/${gradeId}`;
      logger.request('DELETE', url);
      const res = await api.delete(url);
      logger.response('DELETE', url, res.status, res.data);
    } catch (error) {
      logger.error('DELETE', `${BASE_URL}/${gradeId}`, error);
      throw new Error('고객등급 삭제 실패');
    }
  }
}

export default new GradesAPI();
