import axios from 'axios';

const axiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '',
  headers: {
    'Content-Type': 'application/json',
  },
});

axiosInstance.interceptors.response.use(
  (response) => {
    // Returns the custom ApiResponse directly
    return response.data;
  },
  (error) => {
    let errorData = {
      message: 'Sunucu ile iletişim kurulurken bir hata oluştu.',
      errors: null,
      status: 500
    };

    if (error.response && error.response.data) {
      errorData.message = error.response.data.message || errorData.message;
      errorData.errors = error.response.data.errors || null;
      errorData.status = error.response.data.status || error.response.status;
    }

    return Promise.reject(errorData);
  }
);

export default axiosInstance;
