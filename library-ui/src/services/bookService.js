import axiosInstance from '../api/axiosInstance';

const bookService = {
  getAll: () => axiosInstance.get('/api/books'),
  getById: (id) => axiosInstance.get(`/api/books/${id}`),
  save: (data) => axiosInstance.post('/api/books', data),
  update: (id, data) => axiosInstance.put(`/api/books/${id}`, data),
  delete: (id) => axiosInstance.delete(`/api/books/${id}`),
  searchByTitle: (title) => axiosInstance.get(`/api/books/search?title=${encodeURIComponent(title)}`),
  getByCategory: (categoryId) => axiosInstance.get(`/api/books/category/${categoryId}`),
  getAvailable: () => axiosInstance.get('/api/books/available'),
};

export default bookService;
