import api from '@/plugins/axios.js';

export const getStaffSales = params => api.get('/staff-sales', { params });

export const getStaffDetailSales = params => api.get(`/staff-sales/details`, { params });

export const getStaffTargetSales = params => api.get(`/staff-sales/targets`, { params });

export const getIncentives = () => api.get(`/staff-sales/incentive`);

export const setIncentive = params => api.post(`/staff-sales/incentive`, params);

export const getSalesTarget = params => api.get(`/staff-sales/sales-target`, { params });

export const setSalesTarget = params => api.post('/staff-sales/sales-target', params);
