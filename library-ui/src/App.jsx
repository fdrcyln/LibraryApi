import React, { useState } from 'react';
import MainLayout from './layouts/MainLayout';
import DashboardPage from './pages/DashboardPage';
import BooksPage from './pages/BooksPage';
import CategoriesPage from './pages/CategoriesPage';
import MembersPage from './pages/MembersPage';
import RentalsPage from './pages/RentalsPage';
import { AlertCircle, CheckCircle } from 'lucide-react';

function App() {
  const [activeTab, setActiveTab] = useState('dashboard');
  const [toast, setToast] = useState(null);

  const showToast = (message, type = 'success') => {
    setToast({ message, type });
    setTimeout(() => {
      setToast(null);
    }, 4000);
  };

  const renderActivePage = () => {
    switch (activeTab) {
      case 'dashboard':
        return <DashboardPage showToast={showToast} />;
      case 'books':
        return <BooksPage showToast={showToast} />;
      case 'categories':
        return <CategoriesPage showToast={showToast} />;
      case 'members':
        return <MembersPage showToast={showToast} />;
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

export default App;
