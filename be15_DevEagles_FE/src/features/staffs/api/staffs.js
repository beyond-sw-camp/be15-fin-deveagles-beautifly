import api from '@/plugins/axios.js';

export const createStaff = params => api.post(`/staffs`, params);

export const getStaff = ({ page = 1, size = 1000, isActive = true, keyword = '' }) =>
  api.get(`/staffs`, {
    params: {
      page,
      size,
      isActive,
      keyword,
    },
  });

export const getStaffById = id => api.get(`/staffs/${id}`);

export const updateStaff = (id, params) => api.put(`/staffs/${id}`, params);

export const deleteStaff = id => api.delete(`/staffs/${id}`);
