import axios from 'axios';

const axiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request Interceptor: Automatically inject JWT Bearer Token into headers
axiosInstance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response Interceptor: Handle response data & global auth errors (e.g. 401)
axiosInstance.interceptors.response.use(
  (response) => {
    // Returns the custom ApiResponse data directly
    return response.data;
  },
  (error) => {
    let errorData = {
      message: 'Sunucu ile iletişim kurulurken bir hata oluştu.',
      errors: null,
      status: 500
    };

    if (error.response) {
      if (error.response.status === 401) {
        // Clear stored token and user if unauthorized
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        window.dispatchEvent(new Event('auth:unauthorized'));
      }
      if (error.response.data) {
        errorData.message = error.response.data.message || errorData.message;
        errorData.errors = error.response.data.errors || null;
        errorData.status = error.response.data.status || error.response.status;
      } else {
        errorData.status = error.response.status;
      }
    } else if (error.request) {
      errorData.message = 'Sunucuya ulaşılamıyor. Lütfen internet bağlantınızı veya API sunucusunu kontrol edin.';
    }

    return Promise.reject(errorData);
  }
);

export default axiosInstance;
