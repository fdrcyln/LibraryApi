package com.fdrcyln.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fdrcyln.dto.request.CreateBookRequest;
import com.fdrcyln.dto.request.UpdateBookRequest;
import com.fdrcyln.dto.response.BookResponse;

@Service
public interface IBookService {
    BookResponse save(CreateBookRequest request);

    List<BookResponse> getAll();

    BookResponse getById(Long id);

    BookResponse update(Long id, UpdateBookRequest request);

    void delete(Long id);

    List<BookResponse> searchByTitle(String title);

    List<BookResponse> getByCategoryId(Long categoryId);

    List<BookResponse> getAvailableBooks();
}
