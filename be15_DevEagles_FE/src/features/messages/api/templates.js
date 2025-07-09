import api from '@/plugins/axios.js';

const BASE_URL = '/templates';

// TODO: 메시지 템플릿 조회 연동시키려고 임의로 간단하게 만듦, 나중에 수정하세요(김태환)
class TemplatesAPI {
  async getTemplates({ shopId, ...searchParams } = {}) {
    const params = {
      ...searchParams,
    };
    if (shopId) params.shopId = shopId;
    const response = await api.get(BASE_URL, { params });
    return (response.data.data?.content || []).map(t => ({
      id: t.id,
      name: t.templateTitle || t.name,
      type: t.type,
      content: t.content,
      createdAt: t.createdAt,
    }));
  }
}

export default new TemplatesAPI();
