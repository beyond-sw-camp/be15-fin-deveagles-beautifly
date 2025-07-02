import api from '@/plugins/axios.js';
import { useAuthStore } from '@/store/auth.js';

const exceptToken = [
  { method: 'post', url: '/user' },
  { method: 'post', url: '/user/valid-id' },
  { method: 'post', url: '/user/valid-email' },
  { method: 'post', url: '/shop/valid-biz' },
  { method: 'get', url: '/shop/get-industry' },
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

export const signUp = params => api.post(`/user`, params);

export const validId = params => api.post(`/user/valid-id`, params);

export const validEmail = params => api.post(`/user/valid-email`, params);

export const getAccount = params => api.post(`/user/account`, params);

export const patchAccount = params => api.patch(`/user/account`, params);

export const getProfile = () => api.get(`/user/profile`);

export const patchProfile = formData => api.patch(`/user/profile`, formData);

export const validBizNumber = params => api.post(`/shop/valid-biz`, params);

export const getIndustry = () => api.get(`/shop/get-industry`);
