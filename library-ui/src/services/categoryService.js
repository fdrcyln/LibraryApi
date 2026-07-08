import axiosInstance from '../api/axiosInstance';

const categoryService = {
  getAll: () => axiosInstance.get('/api/categories'),
  getById: (id) => axiosInstance.get(`/api/categories/${id}`),
  save: (data) => axiosInstance.post('/api/categories', data),
  update: (id, data) => axiosInstance.put(`/api/categories/${id}`, data),
  delete: (id) => axiosInstance.delete(`/api/categories/${id}`),
};

export default categoryService;
