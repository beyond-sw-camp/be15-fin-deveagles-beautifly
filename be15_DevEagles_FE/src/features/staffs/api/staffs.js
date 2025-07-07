import api from '@/plugins/axios.js';

export const createStaff = params => api.post(`/staffs`, params);

export const getStaff = params => api.get(`/staffs`, { params });

export const getStaffDetail = staffId => api.get(`/staffs/${staffId}`);

export const putStaffDetail = ({ staffId, formData }) =>
  api.post(`/staffs/${staffId}`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
