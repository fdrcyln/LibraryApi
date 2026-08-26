import React, { createContext, useState, useContext, useEffect } from 'react';
import authService from '../services/authService';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [token, setToken] = useState(() => localStorage.getItem('token') || null);
  const [user, setUser] = useState(() => {
    const savedUser = localStorage.getItem('user');
    return savedUser ? JSON.parse(savedUser) : null;
  });
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const handleUnauthorized = () => {
      logout();
    };

    window.addEventListener('auth:unauthorized', handleUnauthorized);
    return () => {
      window.removeEventListener('auth:unauthorized', handleUnauthorized);
    };
  }, []);

  const login = async (email, password) => {
    setLoading(true);
    try {
      const response = await authService.login({ email, password });
      if (response && response.success && response.data) {
        const { accessToken, role, userId, email: userEmail } = response.data;
        const userData = { id: userId, email: userEmail, role };

        setToken(accessToken);
        setUser(userData);

        localStorage.setItem('token', accessToken);
        localStorage.setItem('user', JSON.stringify(userData));
        return { success: true, message: response.message };
      } else {
        return { success: false, message: response?.message || 'Giriş başarısız oldu.' };
      }
    } catch (error) {
      return {
        success: false,
        message: error.message || 'Giriş yapılırken bir hata oluştu.'
      };
    } finally {
      setLoading(false);
    }
  };

  const register = async (email, password) => {
    setLoading(true);
    try {
      const response = await authService.register({ email, password });
      if (response && response.success && response.data) {
        const { accessToken, role, userId, email: userEmail } = response.data;
        const userData = { id: userId, email: userEmail, role };

        setToken(accessToken);
        setUser(userData);

        localStorage.setItem('token', accessToken);
        localStorage.setItem('user', JSON.stringify(userData));
        return { success: true, message: response.message };
      } else {
        return { success: false, message: response?.message || 'Kayıt olunamadı.' };
      }
    } catch (error) {
      return {
        success: false,
        message: error.message || 'Kayıt olunurken bir hata oluştu.'
      };
    } finally {
      setLoading(false);
    }
  };

  const logout = () => {
    setToken(null);
    setUser(null);
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  };

  const value = {
    token,
    user,
    role: user?.role || null,
    isAuthenticated: !!token && !!user,
    isAdmin: user?.role === 'ADMIN',
    loading,
    login,
    register,
    logout
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export default AuthContext;
