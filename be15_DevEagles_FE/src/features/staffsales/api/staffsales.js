import api from '@/plugins/axios.js';

export const getStaffSales = params => api.get('/staff-sales', { params });
