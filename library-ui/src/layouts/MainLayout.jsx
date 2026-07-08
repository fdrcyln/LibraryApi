import React from 'react';
import { Book, Layers, Users, Bookmark, LayoutDashboard } from 'lucide-react';

const MainLayout = ({ children, activeTab, setActiveTab }) => {
  const menuItems = [
    { id: 'dashboard', label: 'Dashboard', icon: <LayoutDashboard size={20} /> },
    { id: 'books', label: 'Books', icon: <Book size={20} /> },
    { id: 'categories', label: 'Categories', icon: <Layers size={20} /> },
    { id: 'members', label: 'Members', icon: <Users size={20} /> },
    { id: 'rentals', label: 'Rentals', icon: <Bookmark size={20} /> },
  ];

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
            <span style={{ fontSize: '0.85rem', color: 'var(--text-secondary)', background: 'var(--bg-main)', padding: '0.35rem 0.75rem', borderRadius: '20px', fontWeight: '500' }}>
              Yönetici Paneli
            </span>
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
