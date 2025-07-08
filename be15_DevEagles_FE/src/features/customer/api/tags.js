import api from '@/plugins/axios.js';
import { getApiLogger } from '@/plugins/LoggerManager.js';

const logger = getApiLogger('TagsAPI');

const BASE_URL = '/customers/tags';

class TagsAPI {
  /**
   * 매장별 태그 목록 조회
   * @returns {Promise<Array<{tag_id:number,tag_name:string,color_code:string}>>}
   */
  async getTagsByShop() {
    try {
      logger.request('GET', BASE_URL);
      const response = await api.get(BASE_URL);
      logger.response('GET', BASE_URL, response.status, response.data);
      const tags = Array.isArray(response.data?.data) ? response.data.data : [];
      return tags.map(t => ({
        tagId: t.tagId,
        tagName: t.tagName,
        colorCode: t.colorCode,
      }));
    } catch (error) {
      logger.error('GET', BASE_URL, error);
      throw new Error('태그 목록 조회 실패');
    }
  }

  /**
   * 태그 생성
   * @param {{tagName:string, colorCode:string, shopId:number}} payload
   * @returns {Promise<number>} 새 태그 ID
   */
  async createTag({ tagName, colorCode, shopId }) {
    try {
      logger.request('POST', BASE_URL);
      const body = { tagName, colorCode, shopId };
      const res = await api.post(BASE_URL, body);
      logger.response('POST', BASE_URL, res.status, res.data);
      return res.data?.data;
    } catch (error) {
      logger.error('POST', BASE_URL, error);
      throw new Error('태그 생성 실패');
    }
  }

  /**
   * 태그 수정
   * @param {number} tagId
   * @param {{tagName:string, colorCode:string, shopId:number}} payload
   */
  async updateTag(tagId, { tagName, colorCode, shopId }) {
    try {
      const url = `${BASE_URL}/${tagId}`;
      const body = { tagName, colorCode, shopId };
      logger.request('PUT', url, body);
      const res = await api.put(url, body);
      logger.response('PUT', url, res.status, res.data);
    } catch (error) {
      logger.error('PUT', `${BASE_URL}/${tagId}`, error);
      throw new Error('태그 수정 실패');
    }
  }

  /**
   * 태그 삭제
   * @param {number} tagId
   */
  async deleteTag(tagId) {
    try {
      const url = `${BASE_URL}/${tagId}`;
      logger.request('DELETE', url);
      const res = await api.delete(url);
      logger.response('DELETE', url, res.status, res.data);
    } catch (error) {
      logger.error('DELETE', `${BASE_URL}/${tagId}`, error);
      throw new Error('태그 삭제 실패');
    }
  }
}

export default new TagsAPI();
