import api from '@/plugins/axios.js';
import { getApiLogger } from '@/plugins/LoggerManager.js';

const logger = getApiLogger('TagsAPI');

const BASE_URL = '/customers/tags';

class TagsAPI {
  /**
   * 매장별 태그 목록 조회
   * @param {number} shopId
   * @returns {Promise<Array<{tag_id:number,tag_name:string,color_code:string}>>}
   */
  async getTagsByShop(shopId) {
    try {
      const params = { shopId };
      logger.request('GET', BASE_URL, params);
      const response = await api.get(BASE_URL, { params });
      logger.response('GET', BASE_URL, response.status, response.data);
      const tags = Array.isArray(response.data?.data) ? response.data.data : [];
      return tags.map(t => ({ tag_id: t.tagId, tag_name: t.tagName, color_code: t.colorCode }));
    } catch (error) {
      logger.error('GET', BASE_URL, error);
      throw new Error('태그 목록 조회 실패');
    }
  }
}

export default new TagsAPI();
