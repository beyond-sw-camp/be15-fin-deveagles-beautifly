import api from '@/plugins/axios.js';

export const getStaffSales = params => api.get('/staff-sales', { params });

export const getStaffDetailSales = params => api.get(`/staff-sales/details`, { params });
