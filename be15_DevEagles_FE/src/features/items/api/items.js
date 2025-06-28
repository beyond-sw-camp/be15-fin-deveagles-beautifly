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

// 1차 상품 수정 요청
export const updatePrimaryItem = async ({ primaryItemId, shopId, category, primaryItemName }) => {
  const response = await api.put(`/primary-items/${primaryItemId}`, {
    shopId,
    category,
    primaryItemName,
  });

  return response.data.data;
};

// 1차 상품 삭제 요청 (soft delete)
export const deletePrimaryItem = async primaryItemId => {
  const response = await api.delete(`/primary-items/${primaryItemId}`);
  return response.data.data;
};

// 1차 상품 전체 조회 요청
export const getPrimaryItems = async () => {
  const response = await api.get('/primary-items');
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

// 2차 상품 수정 요청
export const updateSecondaryItem = async ({
  secondaryItemId,
  primaryItemId,
  secondaryItemName,
  secondaryItemPrice,
  timeTaken,
  active,
}) => {
  const response = await api.put(`/secondary-items/${secondaryItemId}`, {
    primaryItemId,
    secondaryItemName,
    secondaryItemPrice,
    timeTaken,
    active,
  });

  return response.data.data;
};

// 2차 상품 삭제 요청 (soft delete)
export const deleteSecondaryItem = async secondaryItemId => {
  const response = await api.delete(`/secondary-items/${secondaryItemId}`);
  return response.data.data;
};

// 2차 상품 전체 조회 요청
export const getAllSecondaryItems = async () => {
  const response = await api.get('/secondary-items');
  return response.data.data;
};
