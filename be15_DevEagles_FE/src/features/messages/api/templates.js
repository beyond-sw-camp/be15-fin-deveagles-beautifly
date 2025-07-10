import api from '@/plugins/axios.js';
import { useAuthStore } from '@/store/auth.js';

const BASE_URL = '/message/templates';

// TODO: 메시지 템플릿 조회 연동시키려고 임의로 간단하게 만듦, 나중에 수정하세요(김태환)
class TemplatesAPI {
  async getTemplates(searchParams = {}) {
    const authStore = useAuthStore();
    const params = { ...searchParams, shopId: authStore.shopId };
    const response = await api.get(BASE_URL, { params });
    return response.data.data;
  }
}

export default new TemplatesAPI();
