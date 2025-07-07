import api from '@/plugins/axios.js';

export const createStaff = params => api.post(`/staffs`, params);

export const getStaff = params => api.get(`/staffs`, { params });
