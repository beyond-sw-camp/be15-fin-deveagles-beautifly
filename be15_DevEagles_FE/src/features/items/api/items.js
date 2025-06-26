import api from '@/plugins/axios'; // 토큰 및 리프레시 포함된 axios 인스턴스 사용

// 1차 상품 등록 요청
export const registerPrimaryItem = async ({ shopId, category, primaryItemName }) => {
  const response = await api.post('/primary-items', {
    shopId,
    category,
    primaryItemName,
  });

  return response.data.data;
};

// 2차 상품 등록 요청
export const registerSecondaryItem = async ({
  primaryItemId,
  secondaryItemName,
  secondaryItemPrice,
  timeTaken,
}) => {
  const response = await api.post('/secondary-items', {
    primaryItemId,
    secondaryItemName,
    secondaryItemPrice,
    timeTaken,
  });

  return response.data.data;
};

// 1차 상품 수정 요청
export const updatePrimaryItem = async ({ primaryItemId, shopId, category, primaryItemName }) => {
  const response = await api.put(`/primary-items/${primaryItemId}`, {
    shopId,
    category,
    primaryItemName,
  });

  return response.data.data;
};

// 1차 상품 전체 조회 요청
export const getPrimaryItems = async () => {
  const response = await api.get('/primary-items');
  return response.data.data; // 상품 리스트 배열
};
