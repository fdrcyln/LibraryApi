package com.fdrcyln.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fdrcyln.entities.Book;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByActiveTrue();
    List<Book> findByTitleContainingIgnoreCaseAndActiveTrue(String title);
    List<Book> findByCategoryIdAndActiveTrue(Long categoryId);
    List<Book> findByAvailableStockGreaterThanAndActiveTrue(Integer stock);
    boolean existsByCategoryIdAndActiveTrue(Long categoryId);
    boolean existsByIsbn(String isbn);
    boolean existsByIsbnAndIdNot(String isbn, Long id);
    java.util.List<Book> findByIsbnIgnoreCase(String isbn);
    boolean existsByIsbnIgnoreCaseAndActiveTrue(String isbn);
    boolean existsByIsbnIgnoreCaseAndIdNotAndActiveTrue(String isbn, Long id);
}
