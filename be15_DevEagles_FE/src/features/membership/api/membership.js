import api from '@/plugins/axios'; // 토큰 및 리프레시 포함된 axios 인스턴스 사용

// 선불권 등록 요청
export const registerPrepaidPass = async ({
  shopId,
  prepaidPassId,
  prepaidPassName,
  prepaidPassPrice,
  expirationPeriod,
  bonus,
  discountRate,
  prepaidPassMemo,
}) => {
  const response = await api.post('/prepaid-pass', {
    shopId,
    prepaidPassId,
    prepaidPassName,
    prepaidPassPrice,
    expirationPeriod,
    bonus,
    discountRate,
    prepaidPassMemo,
  });

  return response.data.data;
};

// 횟수권 등록 요청
export const registerSessionPass = async ({
  shopId,
  sessionPassId,
  sessionPassName,
  sessionPassPrice,
  session,
  expirationPeriod,
  bonus,
  discountRate,
  sessionPassMemo,
}) => {
  const response = await api.post('/session-pass', {
    shopId,
    sessionPassId,
    sessionPassName,
    sessionPassPrice,
    session,
    expirationPeriod,
    bonus,
    discountRate,
    sessionPassMemo,
  });

  return response.data.data;
};

// 선불권 전체 조회 요청
export const getPrepaidPass = async () => {
  const response = await api.get('/prepaid-pass');
  return response.data.data;
};

// 횟수권 전체 조회 요청
export const getSessionPass = async () => {
  const response = await api.get('/session-pass');
  return response.data.data;
};
