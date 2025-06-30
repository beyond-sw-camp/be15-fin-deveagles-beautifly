import api from '@/plugins/axios.js';

const exceptToken = [
  { method: 'post', url: '/users' },
  { method: 'post', url: '/valid-id' },
  { method: 'post', url: '/valid-email' },
  { method: 'post', url: '/valid-biz' },
  { method: 'get', url: '/get-industry' },
  { method: 'post', url: '/auth/login' },
];

export const signUp = params => api.post(`/users`, params);

export const validId = params => api.post(`/valid-id`, params);

export const validEmail = params => api.post(`/valid-email`, params);

export const validBizNumber = params => api.post(`/valid-biz`, params);

export const getIndustry = () => api.get(`/get-industry`);

export const login = params => api.post(`/auth/login`, params);

export const getAccount = params => api.post(`/account`, params);

export const patchAccount = params => api.patch(`/account`, params);
