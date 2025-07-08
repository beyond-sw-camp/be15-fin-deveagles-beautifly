import api from '@/plugins/axios'; // 토큰 및 리프레시 포함된 axios 인스턴스 사용

// 선불권 등록 요청
export const registerPrepaidPass = async ({
  shopId,
  prepaidPassId,
  prepaidPassName,
  prepaidPassPrice,
  expirationPeriod,
  expirationPeriodType,
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
    expirationPeriodType,
    bonus,
    discountRate,
    prepaidPassMemo,
  });

  return response.data.data;
};

// 선불권 수정 요청
export const updatePrepaidPass = async ({
  prepaidPassId,
  shopId,
  prepaidPassName,
  prepaidPassPrice,
  expirationPeriod,
  expirationPeriodType,
  bonus,
  discountRate,
  prepaidPassMemo,
}) => {
  const response = await api.put(`/prepaid-pass/${prepaidPassId}`, {
    shopId,
    prepaidPassName,
    prepaidPassPrice,
    expirationPeriod,
    expirationPeriodType,
    bonus,
    discountRate,
    prepaidPassMemo,
  });

  return response.data.data;
};

// 선불권 상품 삭제 요청 (soft delete)
export const deletePrepaidPass = async prepaidPassId => {
  const response = await api.delete(`/prepaid-pass/${prepaidPassId}`);
  return response.data.data;
};

// 횟수권 전체 조회 요청
export const getSessionPass = async () => {
  const response = await api.get('/session-pass');
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
  expirationPeriodType,
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
    expirationPeriodType,
    bonus,
    discountRate,
    sessionPassMemo,
  });

  return response.data.data;
};

// 횟수권 수정 요청
export const updateSessionPass = async ({
  sessionPassId,
  shopId,
  sessionPassName,
  sessionPassPrice,
  session,
  expirationPeriod,
  expirationPeriodType,
  bonus,
  discountRate,
  sessionPassMemo,
}) => {
  const response = await api.put(`/session-pass/${sessionPassId}`, {
    shopId,
    sessionPassName,
    sessionPassPrice,
    session,
    expirationPeriod,
    expirationPeriodType,
    bonus,
    discountRate,
    sessionPassMemo,
  });

  return response.data.data;
};

// 횟수권 상품 삭제 요청 (soft delete)
export const deleteSessionPass = async sessionPassId => {
  const response = await api.delete(`/session-pass/${sessionPassId}`);
  return response.data.data;
};

// 선불권 전체 조회 요청
export const getPrepaidPass = async () => {
  const response = await api.get('/prepaid-pass');
  return response.data.data;
};

// 고객 회원권 조회

// 전체 고객 회원권 조회
export const getAllCustomerMemberships = async (page = 1, size = 10) => {
  const response = await api.get('/customer-memberships', {
    params: { page, size },
  });
  return response.data.data;
};

// 고객 회원권 필터 조회
export const getFilteredCustomerMemberships = async filter => {
  const response = await api.post('/customer-memberships/filter', filter);
  return response.data.data;
};

// 조건부 만료 예정 선불권 필터 조회
export const getFilteredExpiringPrepaidPasses = async filters => {
  const response = await api.get('/customer-expiring-prepaid-passes', {
    params: {
      minRemainingAmount: filters.minRemainingAmount,
      maxRemainingAmount: filters.maxRemainingAmount,
      startDate: filters.startDate,
      endDate: filters.endDate,
      page: filters.page ?? 1,
      size: filters.size ?? 10,
    },
  });
  return response.data.data;
};

// 조건부 만료 예정 횟수권 필터 조회
export const getFilteredExpiringSessionPasses = async filters => {
  const response = await api.get('/customer-expiring-session-passes', {
    params: {
      minRemainingAmount: filters.minRemainingAmount,
      maxRemainingAmount: filters.maxRemainingAmount,
      startDate: filters.startDate,
      endDate: filters.endDate,
      page: filters.page ?? 1,
      size: filters.size ?? 10,
    },
  });
  return response.data.data;
};

// 고객 선불권 상세 조회
export const getCustomerPrepaidPasses = async customerId => {
  const response = await api.get(`/customer-memberships/prepaid-passes/detail/${customerId}`);
  return response.data.data;
};

// 고객 횟수권 상세 조회
export const getCustomerSessionPasses = async customerId => {
  const response = await api.get(`/customer-memberships/session-passes/detail/${customerId}`);
  return response.data.data;
};

// 만료 및 사용완료 회원권 조회
// 만료 또는 소진된 회원권 목록 조회
export const getExpiredOrUsedUpMemberships = async customerId => {
  const response = await api.get(`/customer-memberships/expired-or-used-up/${customerId}`);
  return response.data.data;
};
