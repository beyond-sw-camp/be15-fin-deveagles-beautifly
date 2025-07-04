import api from '@/plugins/axios.js';
import { useAuthStore } from '@/store/auth.js';

const exceptToken = [
  { method: 'post', url: '/users' },
  { method: 'post', url: '/users/valid-id' },
  { method: 'post', url: '/users/valid-email' },
  { method: 'post', url: '/shops/valid-biz' },
  { method: 'get', url: '/shops/get-industry' },
  { method: 'post', url: '/auth/login' },
];

api.interceptors.request.use(
  config => {
    const authStore = useAuthStore();
    const token = authStore.accessToken;

    const shouldSkipToken = exceptToken.some(
      pattern => pattern.method === config.method && new RegExp(pattern.url).test(config.url)
    );

    if (token && !shouldSkipToken) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  },
  error => {
    return Promise.reject(error);
  }
);
export const login = params => api.post(`/auth/login`, params);

export const logout = () => api.post(`/auth/logout`);

export const checkEmail = params => api.post(`/auth/check-email`, params);

export const verifyPwd = params => api.post(`/auth/verify`, params);

export const patchPwd = params => api.patch(`/users/password`, params);

export const signUp = params => api.post(`/users`, params);

export const validId = params => api.post(`/users/valid-id`, params);

export const validEmail = params => api.post(`/users/valid-email`, params);

export const getAccount = params => api.post(`/users/account`, params);

export const patchAccount = params => api.patch(`/users/account`, params);

export const getProfile = () => api.get(`/users/profile`);

export const patchProfile = formData => api.patch(`/users/profile`, formData);

export const validBizNumber = params => api.post(`/shops/valid-biz`, params);

export const getIndustry = () => api.get(`/shops/get-industry`);
