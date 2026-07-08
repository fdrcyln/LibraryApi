import React, { useState, useEffect } from 'react';
import { Plus, Edit2, Trash2, X, RefreshCw } from 'lucide-react';
import categoryService from '../services/categoryService';

const CategoriesPage = ({ showToast }) => {
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(false);
  const [modalOpen, setModalOpen] = useState(false);
  const [isEdit, setIsEdit] = useState(false);
  const [selectedId, setSelectedId] = useState(null);
  
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    active: true
  });
  
  const [fieldErrors, setFieldErrors] = useState({});

  const fetchCategories = async () => {
    setLoading(true);
    try {
      const res = await categoryService.getAll();
      setCategories(res.data || []);
    } catch (err) {
      showToast(err.message || 'Kategoriler yüklenirken bir hata oluştu.', 'error');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCategories();
  }, []);

  const handleOpenCreateModal = () => {
    setIsEdit(false);
    setSelectedId(null);
    setFormData({
      name: '',
      description: '',
      active: true
    });
    setFieldErrors({});
    setModalOpen(true);
  };

  const handleOpenEditModal = (category) => {
    setIsEdit(true);
    setSelectedId(category.id);
    setFormData({
      name: category.name || '',
      description: category.description || '',
      active: category.active !== false
    });
    setFieldErrors({});
    setModalOpen(true);
  };

  const handleDelete = async (id) => {
    if (window.confirm('Bu kategoriyi silmek istediğinizden emin misiniz (soft-delete)?')) {
      try {
        const res = await categoryService.delete(id);
        showToast(res.message || 'Kategori başarıyla pasif hale getirildi.', 'success');
        fetchCategories();
      } catch (err) {
        showToast(err.message || 'Kategori silinirken bir hata oluştu.', 'error');
      }
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setFieldErrors({});
    try {
      if (isEdit) {
        const res = await categoryService.update(selectedId, {
          name: formData.name,
          description: formData.description,
          active: formData.active
        });
        showToast(res.message || 'Kategori başarıyla güncellendi.', 'success');
      } else {
        const res = await categoryService.save({
          name: formData.name,
          description: formData.description
        });
        showToast(res.message || 'Kategori başarıyla eklendi.', 'success');
      }
      setModalOpen(false);
      fetchCategories();
    } catch (err) {
      if (err.errors) {
        setFieldErrors(err.errors);
      } else {
        showToast(err.message || 'Kategori kaydedilirken hata oluştu.', 'error');
      }
    }
  };

  return (
    <div>
      <div className="page-header">
        <h1 className="page-title">Categories</h1>
        <div className="page-actions">
          <button className="btn btn-secondary" onClick={fetchCategories}>
            <RefreshCw size={16} /> Yenile
          </button>
          <button className="btn btn-primary" onClick={handleOpenCreateModal}>
            <Plus size={16} /> Kategori Ekle
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
                  <th>Kategori Adı</th>
                  <th>Açıklama</th>
                  <th>Durum</th>
                  <th>İşlemler</th>
                </tr>
              </thead>
              <tbody>
                {categories.length === 0 ? (
                  <tr>
                    <td colSpan="5" style={{ textAlign: 'center', padding: '2rem' }}>Kayıtlı kategori bulunamadı.</td>
                  </tr>
                ) : (
                  categories.map((category) => (
                    <tr key={category.id}>
                      <td>{category.id}</td>
                      <td style={{ fontWeight: '500', color: 'var(--text-primary)' }}>{category.name}</td>
                      <td>{category.description || '-'}</td>
                      <td>
                        <span className={`badge ${category.active ? 'badge-success' : 'badge-danger'}`}>
                          {category.active ? 'Aktif' : 'Pasif'}
                        </span>
                      </td>
                      <td>
                        <div style={{ display: 'flex', gap: '0.5rem' }}>
                          <button className="btn btn-secondary btn-sm" onClick={() => handleOpenEditModal(category)}>
                            <Edit2 size={12} /> Düzenle
                          </button>
                          <button className="btn btn-danger btn-sm" onClick={() => handleDelete(category.id)}>
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
              <h2 className="modal-title">{isEdit ? 'Kategori Düzenle' : 'Yeni Kategori Ekle'}</h2>
              <button onClick={() => setModalOpen(false)} style={{ background: 'none', border: 'none', cursor: 'pointer' }}>
                <X size={20} />
              </button>
            </div>
            <form onSubmit={handleSubmit}>
              <div className="modal-body">
                <div className="form-group">
                  <label>Kategori Adı*</label>
                  <input
                    type="text"
                    className={`form-control ${fieldErrors.name ? 'is-invalid' : ''}`}
                    value={formData.name}
                    onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  />
                  {fieldErrors.name && <span className="error-feedback">{fieldErrors.name}</span>}
                </div>

                <div className="form-group">
                  <label>Açıklama</label>
                  <textarea
                    rows="3"
                    className="form-control"
                    value={formData.description}
                    onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                  />
                </div>

                {isEdit && (
                  <div className="form-group" style={{ flexDirection: 'row', alignItems: 'center', gap: '0.5rem' }}>
                    <input
                      type="checkbox"
                      id="active"
                      checked={formData.active}
                      onChange={(e) => setFormData({ ...formData, active: e.target.checked })}
                    />
                    <label htmlFor="active" style={{ cursor: 'pointer' }}>Aktif Kategori</label>
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

export default CategoriesPage;
