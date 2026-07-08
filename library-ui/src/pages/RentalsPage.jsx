import React, { useState, useEffect } from 'react';
import { Plus, CheckCircle, Search, RefreshCw, X, Calendar, User, BookOpen } from 'lucide-react';
import rentalService from '../services/rentalService';
import bookService from '../services/bookService';
import memberService from '../services/memberService';

const RentalsPage = ({ showToast }) => {
  const [activeRentals, setActiveRentals] = useState([]);
  const [lateRentals, setLateRentals] = useState([]);
  const [memberRentals, setMemberRentals] = useState([]);
  
  const [availableBooks, setAvailableBooks] = useState([]);
  const [members, setMembers] = useState([]);
  
  const [loading, setLoading] = useState(false);
  const [activeSubTab, setActiveSubTab] = useState('active'); // 'active', 'late', 'member-search'
  
  const [searchMemberId, setSearchMemberId] = useState('');
  const [searchedMemberName, setSearchedMemberName] = useState('');
  
  // Modal state
  const [modalOpen, setModalOpen] = useState(false);
  const [formData, setFormData] = useState({
    bookId: '',
    memberId: '',
    rentalDay: 14,
  });
  
  const [fieldErrors, setFieldErrors] = useState({});
  const [rentingLoading, setRentingLoading] = useState(false);
  const [returningId, setReturningId] = useState(null);

  const fetchRentals = async () => {
    setLoading(true);
    try {
      if (activeSubTab === 'active') {
        const res = await rentalService.getActiveRentals();
        setActiveRentals(res.data || []);
      } else if (activeSubTab === 'late') {
        const res = await rentalService.getLateRentals();
        setLateRentals(res.data || []);
      }
    } catch (err) {
      showToast(err.message || 'Kiralama bilgileri yüklenemedi.', 'error');
    } finally {
      setLoading(false);
    }
  };

  const fetchAvailableBooksAndMembers = async () => {
    try {
      const [booksRes, membersRes] = await Promise.all([
        bookService.getAvailable(),
        memberService.getAll()
      ]);
      const activeAvailBooks = (booksRes.data || []).filter(b => b.active);
      const activeMembers = (membersRes.data || []).filter(m => m.active);
      setAvailableBooks(activeAvailBooks);
      setMembers(activeMembers);
      
      if (activeAvailBooks.length > 0 && activeMembers.length > 0) {
        setFormData({
          bookId: activeAvailBooks[0].id.toString(),
          memberId: activeMembers[0].id.toString(),
          rentalDay: 14
        });
      }
    } catch (err) {
      showToast('Kitap ve Üye verileri yüklenemedi.', 'error');
    }
  };

  useEffect(() => {
    fetchRentals();
  }, [activeSubTab]);

  useEffect(() => {
    fetchAvailableBooksAndMembers();
  }, [modalOpen]);

  const handleMemberSearch = async (e) => {
    e.preventDefault();
    if (!searchMemberId.trim()) return;
    
    setLoading(true);
    try {
      const res = await rentalService.getRentalsByMember(searchMemberId);
      const list = res.data || [];
      setMemberRentals(list);
      
      if (list.length > 0) {
        setSearchedMemberName(list[0].memberFullName);
      } else {
        setSearchedMemberName(`ID: ${searchMemberId}`);
      }
    } catch (err) {
      showToast(err.message || 'Üye geçmişi yüklenemedi.', 'error');
      setMemberRentals([]);
      setSearchedMemberName('');
    } finally {
      setLoading(false);
    }
  };

  const handleReturn = async (id) => {
    if (window.confirm('Bu kitabı teslim almak istediğinizden emin misiniz?')) {
      setReturningId(id);
      try {
        const res = await rentalService.returnBook(id);
        showToast(res.message || 'Kitap başarıyla teslim alındı.', 'success');
        fetchRentals();
        if (searchMemberId) {
          // Refresh search result if active
          const searchRes = await rentalService.getRentalsByMember(searchMemberId);
          setMemberRentals(searchRes.data || []);
        }
      } catch (err) {
        showToast(err.message || 'Teslim işlemi başarısız.', 'error');
      } finally {
        setReturningId(null);
      }
    }
  };

  const handleRentSubmit = async (e) => {
    e.preventDefault();
    if (rentingLoading) return;
    setFieldErrors({});
    setRentingLoading(true);

    try {
      const res = await rentalService.rentBook({
        bookId: parseInt(formData.bookId),
        memberId: parseInt(formData.memberId),
        rentalDay: parseInt(formData.rentalDay)
      });
      showToast(res.message || 'Kitap kiralama işlemi başarılı.', 'success');
      setModalOpen(false);
      fetchRentals();
    } catch (err) {
      if (err.errors) {
        setFieldErrors(err.errors);
      } else {
        showToast(err.message || 'Kiralama işlemi başarısız.', 'error');
      }
    } finally {
      setRentingLoading(false);
    }
  };

  const renderTable = (list) => {
    return (
      <div className="table-wrapper">
        <div className="table-container">
          <table className="custom-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Kitap Adı</th>
                <th>Üye Ad Soyad</th>
                <th>Kiralama Tarihi</th>
                <th>Son Teslim Tarihi</th>
                <th>Teslim Tarihi</th>
                <th>Durum</th>
                <th>İşlemler</th>
              </tr>
            </thead>
            <tbody>
              {list.length === 0 ? (
                <tr>
                  <td colSpan="8" style={{ textAlign: 'center', padding: '2rem' }}>Kiralama kaydı bulunamadı.</td>
                </tr>
              ) : (
                list.map((rental) => (
                  <tr key={rental.id}>
                    <td>{rental.id}</td>
                    <td style={{ fontWeight: '500', color: 'var(--text-primary)' }}>{rental.bookTitle}</td>
                    <td>{rental.memberFullName}</td>
                    <td>{rental.rentalDate}</td>
                    <td>{rental.dueDate}</td>
                    <td>{rental.returnDate || '-'}</td>
                    <td>
                      <span className={`badge ${
                        rental.status === 'RETURNED' ? 'badge-success' : 
                        rental.status === 'LATE' ? 'badge-danger' : 
                        'badge-warning'
                      }`}>
                        {rental.status === 'ACTIVE' ? 'Aktif' : 
                         rental.status === 'LATE' ? 'Gecikmiş' : 'Teslim Edildi'}
                      </span>
                    </td>
                    <td>
                      {rental.status !== 'RETURNED' && (
                        <button 
                          className="btn btn-success btn-sm" 
                          onClick={() => handleReturn(rental.id)}
                          disabled={returningId === rental.id || rentingLoading}
                        >
                          <CheckCircle size={12} /> {returningId === rental.id ? 'İade alınıyor...' : 'İade Al'}
                        </button>
                      )}
                      {rental.status === 'RETURNED' && '-'}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    );
  };

  return (
    <div>
      <div className="page-header">
        <h1 className="page-title">Rentals</h1>
        <div className="page-actions">
          <button className="btn btn-secondary" onClick={fetchRentals}>
            <RefreshCw size={16} /> Yenile
          </button>
          <button className="btn btn-primary" onClick={() => setModalOpen(true)}>
            <Plus size={16} /> Kitap Kirala
          </button>
        </div>
      </div>

      {/* Tabs */}
      <div style={{ display: 'flex', borderBottom: '1px solid var(--border-color)', marginBottom: '1.5rem', gap: '1rem' }}>
        <button
          onClick={() => setActiveSubTab('active')}
          style={{
            padding: '0.75rem 1rem',
            background: 'none',
            border: 'none',
            borderBottom: activeSubTab === 'active' ? '2px solid var(--primary)' : 'none',
            color: activeSubTab === 'active' ? 'var(--primary)' : 'var(--text-secondary)',
            fontWeight: activeSubTab === 'active' ? '600' : '500',
            cursor: 'pointer'
          }}
        >
          Aktif Kiralamalar
        </button>
        <button
          onClick={() => setActiveSubTab('late')}
          style={{
            padding: '0.75rem 1rem',
            background: 'none',
            border: 'none',
            borderBottom: activeSubTab === 'late' ? '2px solid var(--primary)' : 'none',
            color: activeSubTab === 'late' ? 'var(--primary)' : 'var(--text-secondary)',
            fontWeight: activeSubTab === 'late' ? '600' : '500',
            cursor: 'pointer'
          }}
        >
          Geciken Kiralamalar
        </button>
        <button
          onClick={() => setActiveSubTab('member-search')}
          style={{
            padding: '0.75rem 1rem',
            background: 'none',
            border: 'none',
            borderBottom: activeSubTab === 'member-search' ? '2px solid var(--primary)' : 'none',
            color: activeSubTab === 'member-search' ? 'var(--primary)' : 'var(--text-secondary)',
            fontWeight: activeSubTab === 'member-search' ? '600' : '500',
            cursor: 'pointer'
          }}
        >
          Üye Geçmişi Sorgula
        </button>
      </div>

      {activeSubTab === 'member-search' && (
        <div className="filter-bar">
          <form onSubmit={handleMemberSearch} style={{ display: 'flex', gap: '0.75rem', width: '100%', maxWidth: '400px' }}>
            <input
              type="number"
              className="form-control"
              placeholder="Üye ID giriniz..."
              value={searchMemberId}
              onChange={(e) => setSearchMemberId(e.target.value)}
              required
            />
            <button type="submit" className="btn btn-primary" style={{ flexShrink: 0 }}>
              <Search size={16} /> Sorgula
            </button>
          </form>
          {searchedMemberName && (
            <div style={{ fontWeight: '500', color: 'var(--text-secondary)' }}>
              Sorgulanan Üye: <span style={{ color: 'var(--text-primary)' }}>{searchedMemberName}</span>
            </div>
          )}
        </div>
      )}

      {loading ? (
        <div style={{ display: 'flex', justifyContent: 'center', margin: '3rem' }}>
          <RefreshCw className="animate-spin" size={24} style={{ color: 'var(--primary)' }} />
        </div>
      ) : (
        <>
          {activeSubTab === 'active' && renderTable(activeRentals)}
          {activeSubTab === 'late' && renderTable(lateRentals)}
          {activeSubTab === 'member-search' && renderTable(memberRentals)}
        </>
      )}

      {modalOpen && (
        <div className="modal-backdrop">
          <div className="modal-content">
            <div className="modal-header">
              <h2 className="modal-title">Kitap Kirala</h2>
              <button onClick={() => setModalOpen(false)} style={{ background: 'none', border: 'none', cursor: 'pointer' }}>
                <X size={20} />
              </button>
            </div>
            <form onSubmit={handleRentSubmit}>
              <div className="modal-body">
                {availableBooks.length === 0 ? (
                  <p style={{ color: 'var(--danger)', textAlign: 'center', padding: '1rem' }}>
                    Kiralama yapılabilecek müsait kitap bulunmamaktadır. (Stokta olan aktif kitap yok)
                  </p>
                ) : members.length === 0 ? (
                  <p style={{ color: 'var(--danger)', textAlign: 'center', padding: '1rem' }}>
                    Sistemde aktif kayıtlı üye bulunmamaktadır.
                  </p>
                ) : (
                  <>
                    <div className="form-group">
                      <label><BookOpen size={14} style={{ marginRight: '4px' }} /> Kiralanacak Kitap*</label>
                      <select
                        className={`form-control ${fieldErrors.bookId ? 'is-invalid' : ''}`}
                        value={formData.bookId}
                        onChange={(e) => setFormData({ ...formData, bookId: e.target.value })}
                      >
                        {availableBooks.map((b) => (
                          <option key={b.id} value={b.id}>
                            {b.title} (Yazar: {b.author} - Stok: {b.availableStock})
                          </option>
                        ))}
                      </select>
                      {fieldErrors.bookId && <span className="error-feedback">{fieldErrors.bookId}</span>}
                    </div>

                    <div className="form-group">
                      <label><User size={14} style={{ marginRight: '4px' }} /> Kirayı Alan Üye*</label>
                      <select
                        className={`form-control ${fieldErrors.memberId ? 'is-invalid' : ''}`}
                        value={formData.memberId}
                        onChange={(e) => setFormData({ ...formData, memberId: e.target.value })}
                      >
                        {members.map((m) => (
                          <option key={m.id} value={m.id}>
                            {m.firstName} {m.lastName} (ID: {m.id})
                          </option>
                        ))}
                      </select>
                      {fieldErrors.memberId && <span className="error-feedback">{fieldErrors.memberId}</span>}
                    </div>

                    <div className="form-group">
                      <label><Calendar size={14} style={{ marginRight: '4px' }} /> Kiralama Süresi (Gün)*</label>
                      <input
                        type="number"
                        min="1"
                        className={`form-control ${fieldErrors.rentalDay ? 'is-invalid' : ''}`}
                        value={formData.rentalDay}
                        onChange={(e) => setFormData({ ...formData, rentalDay: e.target.value })}
                      />
                      {fieldErrors.rentalDay && <span className="error-feedback">{fieldErrors.rentalDay}</span>}
                    </div>
                  </>
                )}
              </div>
              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={() => setModalOpen(false)} disabled={rentingLoading}>Vazgeç</button>
                <button 
                  type="submit" 
                  className="btn btn-primary" 
                  disabled={availableBooks.length === 0 || members.length === 0 || rentingLoading}
                >
                  {rentingLoading ? 'Kiralanıyor...' : 'Kirala'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default RentalsPage;
