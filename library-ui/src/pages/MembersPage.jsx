import React, { useState, useEffect } from 'react';
import { Plus, Edit2, Trash2, X, RefreshCw } from 'lucide-react';
import memberService from '../services/memberService';

const MembersPage = ({ showToast }) => {
  const [members, setMembers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [modalOpen, setModalOpen] = useState(false);
  const [isEdit, setIsEdit] = useState(false);
  const [selectedId, setSelectedId] = useState(null);
  
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    address: '',
    active: true
  });
  
  const [fieldErrors, setFieldErrors] = useState({});

  const fetchMembers = async () => {
    setLoading(true);
    try {
      const res = await memberService.getAll();
      setMembers(res.data || []);
    } catch (err) {
      showToast(err.message || 'Üyeler yüklenirken bir hata oluştu.', 'error');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchMembers();
  }, []);

  const handleOpenCreateModal = () => {
    setIsEdit(false);
    setSelectedId(null);
    setFormData({
      firstName: '',
      lastName: '',
      email: '',
      phone: '',
      address: '',
      active: true
    });
    setFieldErrors({});
    setModalOpen(true);
  };

  const handleOpenEditModal = (member) => {
    setIsEdit(true);
    setSelectedId(member.id);
    setFormData({
      firstName: member.firstName || '',
      lastName: member.lastName || '',
      email: member.email || '',
      phone: member.phone || '',
      address: member.address || '',
      active: member.active !== false
    });
    setFieldErrors({});
    setModalOpen(true);
  };

  const handleDelete = async (id) => {
    if (window.confirm('Bu üyeyi silmek istediğinizden emin misiniz (soft-delete)?')) {
      try {
        const res = await memberService.delete(id);
        showToast(res.message || 'Üye başarıyla pasif hale getirildi.', 'success');
        fetchMembers();
      } catch (err) {
        showToast(err.message || 'Üye silinirken bir hata oluştu.', 'error');
      }
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setFieldErrors({});
    try {
      if (isEdit) {
        const res = await memberService.update(selectedId, {
          firstName: formData.firstName,
          lastName: formData.lastName,
          email: formData.email,
          phone: formData.phone,
          address: formData.address,
          active: formData.active
        });
        showToast(res.message || 'Üye başarıyla güncellendi.', 'success');
      } else {
        const res = await memberService.save({
          firstName: formData.firstName,
          lastName: formData.lastName,
          email: formData.email,
          phone: formData.phone,
          address: formData.address
        });
        showToast(res.message || 'Üye başarıyla kaydedildi.', 'success');
      }
      setModalOpen(false);
      fetchMembers();
    } catch (err) {
      if (err.errors) {
        setFieldErrors(err.errors);
      } else {
        showToast(err.message || 'Üye kaydedilirken hata oluştu.', 'error');
      }
    }
  };

  return (
    <div>
      <div className="page-header">
        <h1 className="page-title">Members</h1>
        <div className="page-actions">
          <button className="btn btn-secondary" onClick={fetchMembers}>
            <RefreshCw size={16} /> Yenile
          </button>
          <button className="btn btn-primary" onClick={handleOpenCreateModal}>
            <Plus size={16} /> Üye Ekle
          </button>
        </div>
      </div>

      {loading ? (
        <div style={{ display: 'flex', justifyContent: 'center', margin: '3rem' }}>
          <RefreshCw className="animate-spin" size={24} style={{ color: 'var(--primary)' }} />
        </div>
      ) : (
        <div className="table-wrapper">
          <div className="table-container">
            <table className="custom-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Ad Soyad</th>
                  <th>E-posta</th>
                  <th>Telefon</th>
                  <th>Adres</th>
                  <th>Katılım Tarihi</th>
                  <th>Durum</th>
                  <th>İşlemler</th>
                </tr>
              </thead>
              <tbody>
                {members.length === 0 ? (
                  <tr>
                    <td colSpan="8" style={{ textAlign: 'center', padding: '2rem' }}>Kayıtlı üye bulunamadı.</td>
                  </tr>
                ) : (
                  members.map((member) => (
                    <tr key={member.id}>
                      <td>{member.id}</td>
                      <td style={{ fontWeight: '500', color: 'var(--text-primary)' }}>
                        {member.firstName} {member.lastName}
                      </td>
                      <td>{member.email}</td>
                      <td>{member.phone || '-'}</td>
                      <td>{member.address || '-'}</td>
                      <td>{member.membershipDate || '-'}</td>
                      <td>
                        <span className={`badge ${member.active ? 'badge-success' : 'badge-danger'}`}>
                          {member.active ? 'Aktif' : 'Pasif'}
                        </span>
                      </td>
                      <td>
                        <div style={{ display: 'flex', gap: '0.5rem' }}>
                          <button className="btn btn-secondary btn-sm" onClick={() => handleOpenEditModal(member)}>
                            <Edit2 size={12} /> Düzenle
                          </button>
                          <button className="btn btn-danger btn-sm" onClick={() => handleDelete(member.id)}>
                            <Trash2 size={12} /> Sil
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {modalOpen && (
        <div className="modal-backdrop">
          <div className="modal-content">
            <div className="modal-header">
              <h2 className="modal-title">{isEdit ? 'Üye Düzenle' : 'Yeni Üye Ekle'}</h2>
              <button onClick={() => setModalOpen(false)} style={{ background: 'none', border: 'none', cursor: 'pointer' }}>
                <X size={20} />
              </button>
            </div>
            <form onSubmit={handleSubmit}>
              <div className="modal-body">
                <div className="form-grid">
                  <div className="form-group">
                    <label>Ad*</label>
                    <input
                      type="text"
                      className={`form-control ${fieldErrors.firstName ? 'is-invalid' : ''}`}
                      value={formData.firstName}
                      onChange={(e) => setFormData({ ...formData, firstName: e.target.value })}
                    />
                    {fieldErrors.firstName && <span className="error-feedback">{fieldErrors.firstName}</span>}
                  </div>

                  <div className="form-group">
                    <label>Soyad*</label>
                    <input
                      type="text"
                      className={`form-control ${fieldErrors.lastName ? 'is-invalid' : ''}`}
                      value={formData.lastName}
                      onChange={(e) => setFormData({ ...formData, lastName: e.target.value })}
                    />
                    {fieldErrors.lastName && <span className="error-feedback">{fieldErrors.lastName}</span>}
                  </div>

                  <div className="form-group form-grid-full">
                    <label>E-posta*</label>
                    <input
                      type="email"
                      className={`form-control ${fieldErrors.email ? 'is-invalid' : ''}`}
                      value={formData.email}
                      onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                    />
                    {fieldErrors.email && <span className="error-feedback">{fieldErrors.email}</span>}
                  </div>

                  <div className="form-group form-grid-full">
                    <label>Telefon</label>
                    <input
                      type="text"
                      className="form-control"
                      value={formData.phone}
                      onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                    />
                  </div>

                  <div className="form-group form-grid-full">
                    <label>Adres</label>
                    <textarea
                      rows="2"
                      className="form-control"
                      value={formData.address}
                      onChange={(e) => setFormData({ ...formData, address: e.target.value })}
                    />
                  </div>
                </div>

                {isEdit && (
                  <div className="form-group" style={{ flexDirection: 'row', alignItems: 'center', gap: '0.5rem', marginTop: '0.5rem' }}>
                    <input
                      type="checkbox"
                      id="active"
                      checked={formData.active}
                      onChange={(e) => setFormData({ ...formData, active: e.target.checked })}
                    />
                    <label htmlFor="active" style={{ cursor: 'pointer' }}>Aktif Üye</label>
                  </div>
                )}
              </div>
              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={() => setModalOpen(false)}>Vazgeç</button>
                <button type="submit" className="btn btn-primary">{isEdit ? 'Güncelle' : 'Kaydet'}</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default MembersPage;
