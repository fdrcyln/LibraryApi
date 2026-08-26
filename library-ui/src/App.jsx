import React, { useState } from 'react';
import { AuthProvider, useAuth } from './context/AuthContext';
import MainLayout from './layouts/MainLayout';
import LoginPage from './pages/LoginPage';
import DashboardPage from './pages/DashboardPage';
import BooksPage from './pages/BooksPage';
import CategoriesPage from './pages/CategoriesPage';
import MembersPage from './pages/MembersPage';
import RentalsPage from './pages/RentalsPage';
import { AlertCircle, CheckCircle } from 'lucide-react';

function MainApp() {
  const { isAuthenticated, isAdmin } = useAuth();
  const [activeTab, setActiveTab] = useState('dashboard');
  const [toast, setToast] = useState(null);

  const showToast = (message, type = 'success') => {
    setToast({ message, type });
    setTimeout(() => {
      setToast(null);
    }, 4000);
  };

  if (!isAuthenticated) {
    return <LoginPage />;
  }

  const renderActivePage = () => {
    switch (activeTab) {
      case 'dashboard':
        return <DashboardPage showToast={showToast} />;
      case 'books':
        return <BooksPage showToast={showToast} />;
      case 'categories':
        return isAdmin ? (
          <CategoriesPage showToast={showToast} />
        ) : (
          <div style={{ padding: '2rem', textAlign: 'center', color: '#fca5a5' }}>
            <h2>403 - Yetkisiz Erişim</h2>
            <p>Kategori yönetimi ekranına sadece ADMIN kullanıcılar erişebilir.</p>
          </div>
        );
      case 'members':
        return isAdmin ? (
          <MembersPage showToast={showToast} />
        ) : (
          <div style={{ padding: '2rem', textAlign: 'center', color: '#fca5a5' }}>
            <h2>403 - Yetkisiz Erişim</h2>
            <p>Üye yönetimi ekranına sadece ADMIN kullanıcılar erişebilir.</p>
          </div>
        );
      case 'rentals':
        return <RentalsPage showToast={showToast} />;
      default:
        return <DashboardPage showToast={showToast} />;
    }
  };

  return (
    <>
      <MainLayout activeTab={activeTab} setActiveTab={setActiveTab}>
        {renderActivePage()}
      </MainLayout>

      {/* Toast Notifications */}
      {toast && (
        <div className="toast-container">
          <div className={`toast toast-${toast.type}`}>
            {toast.type === 'success' ? <CheckCircle size={20} /> : <AlertCircle size={20} />}
            <span>{toast.message}</span>
          </div>
        </div>
      )}
    </>
  );
}

function App() {
  return (
    <AuthProvider>
      <MainApp />
    </AuthProvider>
  );
}

export default App;
