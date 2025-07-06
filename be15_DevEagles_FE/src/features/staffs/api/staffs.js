import api from '@/plugins/axios.js';

export const createStaff = params => api.post(`/staffs`, params);
