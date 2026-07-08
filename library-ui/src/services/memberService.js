import axiosInstance from '../api/axiosInstance';

const memberService = {
  getAll: () => axiosInstance.get('/api/members'),
  getById: (id) => axiosInstance.get(`/api/members/${id}`),
  save: (data) => axiosInstance.post('/api/members', data),
  update: (id, data) => axiosInstance.put(`/api/members/${id}`, data),
  delete: (id) => axiosInstance.delete(`/api/members/${id}`),
};

export default memberService;
