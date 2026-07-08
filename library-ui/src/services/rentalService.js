import axiosInstance from '../api/axiosInstance';

const rentalService = {
  rentBook: (data) => axiosInstance.post('/api/rentals', data),
  returnBook: (id) => axiosInstance.put(`/api/rentals/${id}/return`),
  getActiveRentals: () => axiosInstance.get('/api/rentals/active'),
  getRentalsByMember: (memberId) => axiosInstance.get(`/api/rentals/member/${memberId}`),
  getLateRentals: () => axiosInstance.get('/api/rentals/late'),
};

export default rentalService;
