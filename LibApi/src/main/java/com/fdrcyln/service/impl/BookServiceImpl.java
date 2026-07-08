package com.fdrcyln.service.impl;

import com.fdrcyln.dto.request.CreateBookRequest;
import com.fdrcyln.dto.request.UpdateBookRequest;
import com.fdrcyln.dto.response.BookResponse;
import com.fdrcyln.entities.Book;
import com.fdrcyln.entities.Category;
import com.fdrcyln.enums.RentalStatus;
import com.fdrcyln.exception.BadRequestException;
import com.fdrcyln.exception.ResourceNotFoundException;
import com.fdrcyln.mapper.BookMapper;
import com.fdrcyln.repository.BookRepository;
import com.fdrcyln.repository.ICategoryRepository;
import com.fdrcyln.repository.IRentalRepository;
import com.fdrcyln.service.IBookService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookServiceImpl implements IBookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final ICategoryRepository categoryRepository;
    private final IRentalRepository rentalRepository;

    public BookServiceImpl(
            BookRepository bookRepository,
            BookMapper bookMapper,
            ICategoryRepository categoryRepository,
            IRentalRepository rentalRepository
    ) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
        this.categoryRepository = categoryRepository;
        this.rentalRepository = rentalRepository;
    }

    @Override
    @Transactional
    public BookResponse save(CreateBookRequest request) {
        String trimmedIsbn = request.getIsbn() != null ? request.getIsbn().trim().replaceAll("\\s+", "") : "";
        
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Kategori bulunamadı. ID: " + request.getCategoryId()));

        java.util.List<Book> existingList = bookRepository.findByIsbnIgnoreCase(trimmedIsbn);
        if (!existingList.isEmpty()) {
            boolean anyActive = existingList.stream().anyMatch(Book::getActive);
            if (anyActive) {
                throw new BadRequestException("Bu ISBN numarasına sahip aktif bir kitap zaten mevcut.");
            } else {
                Book existingBook = existingList.get(0);
                existingBook.setActive(true);
                existingBook.setTitle(request.getTitle() != null ? request.getTitle().trim() : "");
                existingBook.setAuthor(request.getAuthor() != null ? request.getAuthor().trim() : "");
                existingBook.setIsbn(trimmedIsbn);
                existingBook.setTotalStock(request.getTotalStock());
                existingBook.setAvailableStock(request.getTotalStock());
                existingBook.setPageCount(request.getPageCount());
                existingBook.setPublicationYear(request.getPublicationYear());
                existingBook.setCategory(category);
                Book savedBook = bookRepository.save(existingBook);
                return bookMapper.toResponse(savedBook);
            }
        }

        Book book = bookMapper.toEntity(request);
        book.setIsbn(trimmedIsbn);
        if (book.getTitle() != null) {
            book.setTitle(book.getTitle().trim());
        }
        if (book.getAuthor() != null) {
            book.setAuthor(book.getAuthor().trim());
        }
        book.setCategory(category);
        book.setActive(true);

        Book savedBook = bookRepository.save(book);

        return bookMapper.toResponse(savedBook);
    }

    @Override
    public List<BookResponse> getAll() {
        return bookRepository.findByActiveTrue()
                .stream()
                .map(bookMapper::toResponse)
                .toList();
    }

    @Override
    public BookResponse getById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kitap bulunamadı. ID: " + id));

        return bookMapper.toResponse(book);
    }

    @Override
    @Transactional
    public BookResponse update(Long id, UpdateBookRequest request) {
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kitap bulunamadı. ID: " + id));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Kategori bulunamadı. ID: " + request.getCategoryId()));

        String trimmedIsbn = request.getIsbn() != null ? request.getIsbn().trim().replaceAll("\\s+", "") : "";
        if (bookRepository.existsByIsbnIgnoreCaseAndIdNotAndActiveTrue(trimmedIsbn, id)) {
            throw new BadRequestException("Bu ISBN numarasına sahip aktif bir kitap zaten mevcut.");
        }

        int borrowedStock = existingBook.getTotalStock() - existingBook.getAvailableStock();
        if (request.getTotalStock() < borrowedStock) {
            throw new BadRequestException("Toplam stok, aktif kiralanmış kitap sayısından küçük olamaz.");
        }

        existingBook.setTitle(request.getTitle() != null ? request.getTitle().trim() : "");
        existingBook.setAuthor(request.getAuthor() != null ? request.getAuthor().trim() : "");
        existingBook.setIsbn(trimmedIsbn);
        existingBook.setTotalStock(request.getTotalStock());
        existingBook.setAvailableStock(request.getTotalStock() - borrowedStock);
        existingBook.setPageCount(request.getPageCount());
        existingBook.setPublicationYear(request.getPublicationYear());
        existingBook.setCategory(category);

        Book updatedBook = bookRepository.save(existingBook);

        return bookMapper.toResponse(updatedBook);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kitap bulunamadı. ID: " + id));

        if (rentalRepository.existsByBookIdAndStatus(id, RentalStatus.ACTIVE)) {
            throw new BadRequestException("Bu kitap aktif olarak kiralandığı için silinemez.");
        }

        book.setActive(false);
        bookRepository.save(book);
    }

    @Override
    public List<BookResponse> searchByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCaseAndActiveTrue(title)
                .stream()
                .map(bookMapper::toResponse)
                .toList();
    }

    @Override
    public List<BookResponse> getByCategoryId(Long categoryId) {
        return bookRepository.findByCategoryIdAndActiveTrue(categoryId)
                .stream()
                .map(bookMapper::toResponse)
                .toList();
    }

    @Override
    public List<BookResponse> getAvailableBooks() {
        return bookRepository.findByAvailableStockGreaterThanAndActiveTrue(0)
                .stream()
                .map(bookMapper::toResponse)
                .toList();
    }
}