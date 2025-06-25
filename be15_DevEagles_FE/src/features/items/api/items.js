import api from '@/plugins/axios'; // 토큰 및 리프레시 포함된 axios 인스턴스 사용

// 1차 상품 등록 요청
export const registerPrimaryItem = async ({ shopId, category, primaryItemName }) => {
  const response = await api.post('/primary-items', {
    shopId,
    category,
    primaryItemName,
  });

  return response.data.data; // ApiResponse의 data 반환
};
