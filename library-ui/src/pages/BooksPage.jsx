import React, { useState, useEffect } from 'react';
import { Plus, Edit2, Trash2, X, Search, RefreshCw, Filter, Check } from 'lucide-react';
import bookService from '../services/bookService';
import categoryService from '../services/categoryService';

const BooksPage = ({ showToast }) => {
  const [books, setBooks] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(false);
  
  // Filters state
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCategoryFilter, setSelectedCategoryFilter] = useState('');
  const [showOnlyAvailable, setShowOnlyAvailable] = useState(false);
  
  // Modals state
  const [modalOpen, setModalOpen] = useState(false);
  const [isEdit, setIsEdit] = useState(false);
  const [selectedId, setSelectedId] = useState(null);
  
  const [formData, setFormData] = useState({
    title: '',
    author: '',
    isbn: '',
    totalStock: 1,
    availableStock: 1,
    pageCount: '',
    publicationYear: '',
    categoryId: ''
  });
  
  const [fieldErrors, setFieldErrors] = useState({});

  const fetchBooks = async () => {
    setLoading(true);
    try {
      let res;
      if (showOnlyAvailable) {
        res = await bookService.getAvailable();
      } else if (selectedCategoryFilter) {
        res = await bookService.getByCategory(selectedCategoryFilter);
      } else if (searchQuery.trim()) {
        res = await bookService.searchByTitle(searchQuery.trim());
      } else {
        res = await bookService.getAll();
      }
      setBooks(res.data || []);
    } catch (err) {
      showToast(err.message || 'Kitaplar yüklenirken bir hata oluştu.', 'error');
    } finally {
      setLoading(false);
    }
  };

  const fetchCategories = async () => {
    try {
      const res = await categoryService.getAll();
      setCategories(res.data || []);
    } catch (err) {
      showToast('Kategoriler yüklenemedi.', 'error');
    }
  };

  useEffect(() => {
    fetchBooks();
  }, [selectedCategoryFilter, showOnlyAvailable]);

  useEffect(() => {
    fetchCategories();
  }, []);

  const handleSearchSubmit = (e) => {
    e.preventDefault();
    setSelectedCategoryFilter('');
    setShowOnlyAvailable(false);
    fetchBooks();
  };

  const handleClearFilters = () => {
    setSearchQuery('');
    setSelectedCategoryFilter('');
    setShowOnlyAvailable(false);
    // Note: useEffect or manual fetch:
    // since selectedCategoryFilter & showOnlyAvailable are dependencies, clearing them triggers fetchBooks.
    // If search query is also cleared, we can trigger fetchBooks manually or let the render handle it.
  };

  // Wait, if search query is cleared, trigger search reset
  useEffect(() => {
    if (searchQuery === '' && !selectedCategoryFilter && !showOnlyAvailable) {
      fetchBooks();
    }
  }, [searchQuery]);

  const handleOpenCreateModal = () => {
    setIsEdit(false);
    setSelectedId(null);
    setFormData({
      title: '',
      author: '',
      isbn: '',
      totalStock: 1,
      availableStock: 1,
      pageCount: '',
      publicationYear: '',
      categoryId: categories.length > 0 ? categories[0].id.toString() : ''
    });
    setFieldErrors({});
    setModalOpen(true);
  };

  const handleOpenEditModal = (book) => {
    setIsEdit(true);
    setSelectedId(book.id);
    setFormData({
      title: book.title || '',
      author: book.author || '',
      isbn: book.isbn || '',
      totalStock: book.totalStock || 1,
      availableStock: book.availableStock || 1,
      pageCount: book.pageCount || '',
      publicationYear: book.publicationYear || '',
      categoryId: book.categoryId ? book.categoryId.toString() : ''
    });
    setFieldErrors({});
    setModalOpen(true);
  };

  const handleDelete = async (id) => {
    if (window.confirm('Bu kitabı silmek istediğinizden emin misiniz (soft-delete)?')) {
      try {
        const res = await bookService.delete(id);
        showToast(res.message || 'Kitap başarıyla pasif hale getirildi.', 'success');
        fetchBooks();
      } catch (err) {
        showToast(err.message || 'Kitap silinirken bir hata oluştu.', 'error');
      }
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setFieldErrors({});
    
    const requestData = {
      title: formData.title,
      author: formData.author,
      isbn: formData.isbn,
      totalStock: formData.totalStock ? parseInt(formData.totalStock) : null,
      pageCount: formData.pageCount ? parseInt(formData.pageCount) : null,
      publicationYear: formData.publicationYear ? parseInt(formData.publicationYear) : null,
      categoryId: formData.categoryId ? parseInt(formData.categoryId) : null
    };

    try {
      if (isEdit) {
        const res = await bookService.update(selectedId, requestData);
        showToast(res.message || 'Kitap başarıyla güncellendi.', 'success');
      } else {
        const res = await bookService.save(requestData);
        showToast(res.message || 'Kitap başarıyla eklendi.', 'success');
      }
      setModalOpen(false);
      fetchBooks();
    } catch (err) {
      if (err.errors) {
        setFieldErrors(err.errors);
      } else {
        showToast(err.message || 'Kitap kaydedilirken hata oluştu.', 'error');
      }
    }
  };

  return (
    <div>
      <div className="page-header">
        <h1 className="page-title">Books</h1>
        <div className="page-actions">
          <button className="btn btn-secondary" onClick={fetchBooks}>
            <RefreshCw size={16} /> Yenile
          </button>
          <button className="btn btn-primary" onClick={handleOpenCreateModal}>
            <Plus size={16} /> Kitap Ekle
          </button>
        </div>
      </div>

      <div className="filter-bar">
        <form onSubmit={handleSearchSubmit} className="search-input-wrapper">
          <Search size={18} className="search-icon" />
          <input
            type="text"
            className="form-control search-input"
            placeholder="Kitap adına göre ara..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
        </form>

        <div className="filter-group">
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <Filter size={16} style={{ color: 'var(--text-secondary)' }} />
            <select
              className="select-control"
              value={selectedCategoryFilter}
              onChange={(e) => {
                setSelectedCategoryFilter(e.target.value);
                setShowOnlyAvailable(false);
                setSearchQuery('');
              }}
            >
              <option value="">Tüm Kategoriler</option>
              {categories.map((c) => (
                <option key={c.id} value={c.id}>{c.name}</option>
              ))}
            </select>
          </div>

          <button
            className={`btn ${showOnlyAvailable ? 'btn-info' : 'btn-secondary'}`}
            onClick={() => {
              setShowOnlyAvailable(!showOnlyAvailable);
              setSelectedCategoryFilter('');
              setSearchQuery('');
            }}
          >
            {showOnlyAvailable && <Check size={14} />} Sadece Stokta Olanlar
          </button>

          {(searchQuery || selectedCategoryFilter || showOnlyAvailable) && (
            <button className="btn btn-secondary" onClick={handleClearFilters}>
              Filtreleri Temizle
            </button>
          )}
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
                  <th>Kitap Adı</th>
                  <th>Yazar</th>
                  <th>ISBN</th>
                  <th>Kategori</th>
                  <th>Toplam Stok</th>
                  <th>Mevcut Stok</th>
                  <th>Sayfa Sayısı</th>
                  <th>Yayın Yılı</th>
                  <th>Durum</th>
                  <th>İşlemler</th>
                </tr>
              </thead>
              <tbody>
                {books.length === 0 ? (
                  <tr>
                    <td colSpan="11" style={{ textAlign: 'center', padding: '2rem' }}>Kitap bulunamadı.</td>
                  </tr>
                ) : (
                  books.map((book) => (
                    <tr key={book.id}>
                      <td>{book.id}</td>
                      <td style={{ fontWeight: '500', color: 'var(--text-primary)' }}>{book.title}</td>
                      <td>{book.author}</td>
                      <td><code>{book.isbn}</code></td>
                      <td>{book.categoryName || '-'}</td>
                      <td>{book.totalStock}</td>
                      <td>
                        <span className={`badge ${book.availableStock > 0 ? 'badge-success' : 'badge-danger'}`}>
                          {book.availableStock}
                        </span>
                      </td>
                      <td>{book.pageCount || '-'}</td>
                      <td>{book.publicationYear || '-'}</td>
                      <td>
                        <span className={`badge ${book.active ? 'badge-success' : 'badge-danger'}`}>
                          {book.active ? 'Aktif' : 'Pasif'}
                        </span>
                      </td>
                      <td>
                        <div style={{ display: 'flex', gap: '0.5rem' }}>
                          <button className="btn btn-secondary btn-sm" onClick={() => handleOpenEditModal(book)}>
                            <Edit2 size={12} /> Düzenle
                          </button>
                          <button className="btn btn-danger btn-sm" onClick={() => handleDelete(book.id)}>
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
          <div className="modal-content" style={{ maxWidth: '600px' }}>
            <div className="modal-header">
              <h2 className="modal-title">{isEdit ? 'Kitap Düzenle' : 'Yeni Kitap Ekle'}</h2>
              <button onClick={() => setModalOpen(false)} style={{ background: 'none', border: 'none', cursor: 'pointer' }}>
                <X size={20} />
              </button>
            </div>
            <form onSubmit={handleSubmit}>
              <div className="modal-body">
                <div className="form-grid">
                  <div className="form-group form-grid-full">
                    <label>Kitap Adı*</label>
                    <input
                      type="text"
                      className={`form-control ${fieldErrors.title ? 'is-invalid' : ''}`}
                      value={formData.title}
                      onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                    />
                    {fieldErrors.title && <span className="error-feedback">{fieldErrors.title}</span>}
                  </div>

                  <div className="form-group">
                    <label>Yazar*</label>
                    <input
                      type="text"
                      className={`form-control ${fieldErrors.author ? 'is-invalid' : ''}`}
                      value={formData.author}
                      onChange={(e) => setFormData({ ...formData, author: e.target.value })}
                    />
                    {fieldErrors.author && <span className="error-feedback">{fieldErrors.author}</span>}
                  </div>

                  <div className="form-group">
                    <label>ISBN*</label>
                    <input
                      type="text"
                      className={`form-control ${fieldErrors.isbn ? 'is-invalid' : ''}`}
                      value={formData.isbn}
                      onChange={(e) => setFormData({ ...formData, isbn: e.target.value })}
                    />
                    {fieldErrors.isbn && <span className="error-feedback">{fieldErrors.isbn}</span>}
                  </div>

                  <div className="form-group">
                    <label>Kategori*</label>
                    <select
                      className={`form-control ${fieldErrors.categoryId ? 'is-invalid' : ''}`}
                      value={formData.categoryId}
                      onChange={(e) => setFormData({ ...formData, categoryId: e.target.value })}
                    >
                      <option value="">Seçiniz...</option>
                      {categories.map((c) => (
                        <option key={c.id} value={c.id}>{c.name}</option>
                      ))}
                    </select>
                    {fieldErrors.categoryId && <span className="error-feedback">{fieldErrors.categoryId}</span>}
                  </div>

                  <div className="form-group">
                    <label>Sayfa Sayısı</label>
                    <input
                      type="number"
                      className={`form-control ${fieldErrors.pageCount ? 'is-invalid' : ''}`}
                      value={formData.pageCount}
                      onChange={(e) => setFormData({ ...formData, pageCount: e.target.value })}
                    />
                    {fieldErrors.pageCount && <span className="error-feedback">{fieldErrors.pageCount}</span>}
                  </div>

                  <div className="form-group">
                    <label>Yayın Yılı</label>
                    <input
                      type="number"
                      className={`form-control ${fieldErrors.publicationYear ? 'is-invalid' : ''}`}
                      value={formData.publicationYear}
                      onChange={(e) => setFormData({ ...formData, publicationYear: e.target.value })}
                    />
                    {fieldErrors.publicationYear && <span className="error-feedback">{fieldErrors.publicationYear}</span>}
                  </div>

                  <div className="form-group">
                    <label>Toplam Stok*</label>
                    <input
                      type="number"
                      className={`form-control ${fieldErrors.totalStock ? 'is-invalid' : ''}`}
                      value={formData.totalStock}
                      onChange={(e) => setFormData({ ...formData, totalStock: e.target.value })}
                    />
                    {fieldErrors.totalStock && <span className="error-feedback">{fieldErrors.totalStock}</span>}
                  </div>

                  {isEdit && (
                    <div className="form-group">
                      <label>Kullanılabilir Stok</label>
                      <input
                        type="number"
                        className="form-control"
                        value={formData.availableStock}
                        disabled
                      />
                    </div>
                  )}
                </div>
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

export default BooksPage;
