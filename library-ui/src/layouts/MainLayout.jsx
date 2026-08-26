import React from 'react';
import { useAuth } from '../context/AuthContext';
import { Book, Layers, Users, Bookmark, LayoutDashboard, LogOut, Shield, User } from 'lucide-react';

const MainLayout = ({ children, activeTab, setActiveTab }) => {
  const { user, isAdmin, logout } = useAuth();

  const allMenuItems = [
    { id: 'dashboard', label: 'Dashboard', icon: <LayoutDashboard size={20} />, adminOnly: false },
    { id: 'books', label: 'Books', icon: <Book size={20} />, adminOnly: false },
    { id: 'categories', label: 'Categories', icon: <Layers size={20} />, adminOnly: true },
    { id: 'members', label: 'Members', icon: <Users size={20} />, adminOnly: true },
    { id: 'rentals', label: 'Rentals', icon: <Bookmark size={20} />, adminOnly: false },
  ];

  // Role-based menu filtering
  const menuItems = allMenuItems.filter(item => !item.adminOnly || isAdmin);

  return (
    <div className="app-container">
      {/* Sidebar */}
      <aside className="sidebar">
        <div className="sidebar-brand">
          <Bookmark size={24} style={{ color: 'var(--primary)' }} />
          <span>Library Panel</span>
        </div>
        <nav className="sidebar-nav">
          {menuItems.map((item) => (
            <button
              key={item.id}
              className={`nav-item ${activeTab === item.id ? 'active' : ''}`}
              onClick={() => setActiveTab(item.id)}
            >
              {item.icon}
              <span>{item.label}</span>
            </button>
          ))}
        </nav>
        <div style={{ padding: '1rem', fontSize: '0.8rem', color: 'var(--text-muted)', borderTop: '1px solid rgba(255,255,255,0.05)', textAlign: 'center' }}>
          v1.0.0 &copy; 2026
        </div>
      </aside>

      {/* Main Content Area */}
      <div className="main-content">
        <header className="header">
          <div className="header-title">Library Management System</div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
            <div style={{
              display: 'flex',
              alignItems: 'center',
              gap: '0.5rem',
              fontSize: '0.85rem',
              color: '#f8fafc',
              background: 'rgba(30, 41, 59, 0.8)',
              padding: '0.4rem 0.85rem',
              borderRadius: '20px',
              border: '1px solid rgba(255, 255, 255, 0.1)'
            }}>
              {isAdmin ? (
                <Shield size={16} style={{ color: '#f59e0b' }} />
              ) : (
                <User size={16} style={{ color: '#3b82f6' }} />
              )}
              <span>{user?.email}</span>
              <span style={{
                background: isAdmin ? 'rgba(245, 158, 11, 0.2)' : 'rgba(59, 130, 246, 0.2)',
                color: isAdmin ? '#f59e0b' : '#60a5fa',
                padding: '0.15rem 0.5rem',
                borderRadius: '12px',
                fontSize: '0.75rem',
                fontWeight: '600'
              }}>
                {user?.role}
              </span>
            </div>

            <button
              onClick={logout}
              title="Çıkış Yap"
              style={{
                display: 'flex',
                alignItems: 'center',
                gap: '0.4rem',
                background: 'rgba(239, 68, 68, 0.15)',
                border: '1px solid rgba(239, 68, 68, 0.3)',
                color: '#fca5a5',
                padding: '0.4rem 0.85rem',
                borderRadius: '8px',
                fontSize: '0.85rem',
                fontWeight: '500',
                cursor: 'pointer',
                transition: 'all 0.2s ease'
              }}
            >
              <LogOut size={16} />
              <span>Çıkış</span>
            </button>
          </div>
        </header>
        <main className="page-container">
          {children}
        </main>
      </div>
    </div>
  );
};

export default MainLayout;
