import React, { useState, useEffect } from 'react';
import { Book, Layers, Users, Bookmark, AlertTriangle, RefreshCw } from 'lucide-react';
import bookService from '../services/bookService';
import categoryService from '../services/categoryService';
import memberService from '../services/memberService';
import rentalService from '../services/rentalService';

const DashboardPage = ({ showToast }) => {
  const [stats, setStats] = useState({
    booksCount: 0,
    categoriesCount: 0,
    membersCount: 0,
    activeRentalsCount: 0,
    lateRentalsCount: 0,
  });
  const [loading, setLoading] = useState(true);

  const fetchStats = async () => {
    setLoading(true);
    try {
      const [booksRes, categoriesRes, membersRes, activeRes, lateRes] = await Promise.all([
        bookService.getAll(),
        categoryService.getAll(),
        memberService.getAll(),
        rentalService.getActiveRentals(),
        rentalService.getLateRentals(),
      ]);

      setStats({
        booksCount: booksRes.data?.length || 0,
        categoriesCount: categoriesRes.data?.length || 0,
        membersCount: membersRes.data?.length || 0,
        activeRentalsCount: activeRes.data?.length || 0,
        lateRentalsCount: lateRes.data?.length || 0,
      });
    } catch (err) {
      showToast(err.message || 'Veriler yüklenirken bir hata oluştu', 'error');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchStats();
  }, []);

  if (loading) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '50vh' }}>
        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '1rem' }}>
          <RefreshCw className="animate-spin" size={32} style={{ color: 'var(--primary)' }} />
          <p style={{ color: 'var(--text-secondary)' }}>Veriler yükleniyor...</p>
        </div>
      </div>
    );
  }

  const statCards = [
    {
      title: 'Toplam Kitap',
      value: stats.booksCount,
      icon: <Book size={24} style={{ color: 'var(--primary)' }} />,
      desc: 'Sistemde kayıtlı aktif kitaplar',
    },
    {
      title: 'Toplam Kategori',
      value: stats.categoriesCount,
      icon: <Layers size={24} style={{ color: 'var(--info)' }} />,
      desc: 'Aktif kitap kategorileri',
    },
    {
      title: 'Toplam Üye',
      value: stats.membersCount,
      icon: <Users size={24} style={{ color: 'var(--success)' }} />,
      desc: 'Aktif kayıtlı kütüphane üyeleri',
    },
    {
      title: 'Aktif Kiralama',
      value: stats.activeRentalsCount,
      icon: <Bookmark size={24} style={{ color: 'var(--warning)' }} />,
      desc: 'Teslim edilmemiş aktif kiralamalar',
    },
    {
      title: 'Gecikmiş Kiralama',
      value: stats.lateRentalsCount,
      icon: <AlertTriangle size={24} style={{ color: 'var(--danger)' }} />,
      desc: 'İade tarihi geçmiş kiralamalar',
    },
  ];

  return (
    <div>
      <div className="page-header">
        <h1 className="page-title">Dashboard</h1>
        <button className="btn btn-secondary" onClick={fetchStats}>
          <RefreshCw size={16} /> Yenile
        </button>
      </div>

      <div className="card-grid">
        {statCards.map((card, idx) => (
          <div className="card" key={idx}>
            <div style={{ display: 'flex', justifyContent: 'between', alignItems: 'center', width: '100%' }}>
              <span className="card-title">{card.title}</span>
              <div style={{ marginLeft: 'auto', background: 'var(--bg-main)', padding: '0.5rem', borderRadius: '8px' }}>
                {card.icon}
              </div>
            </div>
            <div className="card-value">{card.value}</div>
            <div className="card-footer">{card.desc}</div>
          </div>
        ))}
      </div>

      <div style={{ marginTop: '2rem', padding: '2rem', background: '#fff', borderRadius: '12px', border: '1px solid var(--border-color)' }}>
        <h2 style={{ marginBottom: '1rem', color: 'var(--text-primary)' }}>Kütüphane Yönetim Sistemine Hoş Geldiniz</h2>
        <p style={{ color: 'var(--text-secondary)', fontSize: '0.95rem', maxWidth: '800px' }}>
          Soldaki menüyü kullanarak kitap ekleme/silme/güncelleme yapabilir, kategorileri ve üye kayıtlarını yönetebilir, 
          kitap kiralama ve teslim alma işlemlerini kolaylıkla takip edebilirsiniz.
        </p>
      </div>
    </div>
  );
};

export default DashboardPage;
