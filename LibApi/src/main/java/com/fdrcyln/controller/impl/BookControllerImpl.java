package com.fdrcyln.controller.impl;

import com.fdrcyln.common.ApiResponse;
import com.fdrcyln.controller.IBookController;
import com.fdrcyln.dto.request.CreateBookRequest;
import com.fdrcyln.dto.request.UpdateBookRequest;
import com.fdrcyln.dto.response.BookResponse;
import com.fdrcyln.service.IBookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BookControllerImpl implements IBookController {

    private final IBookService bookService;

    public BookControllerImpl(IBookService bookService) {
        this.bookService = bookService;
    }

    @Override
    public ResponseEntity<ApiResponse<BookResponse>> save(CreateBookRequest request) {
        return ResponseEntity.ok(ApiResponse.success(bookService.save(request)));
    }

    @Override
    public ResponseEntity<ApiResponse<List<BookResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(bookService.getAll()));
    }

    @Override
    public ResponseEntity<ApiResponse<BookResponse>> getById(Long id) {
        return ResponseEntity.ok(ApiResponse.success(bookService.getById(id)));
    }

    @Override
    public ResponseEntity<ApiResponse<BookResponse>> update(Long id, UpdateBookRequest request) {
        return ResponseEntity.ok(ApiResponse.success(bookService.update(id, request)));
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> delete(Long id) {
        bookService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Kitap başarıyla pasif hale getirildi", null));
    }

    @Override
    public ResponseEntity<ApiResponse<List<BookResponse>>> searchByTitle(String title) {
        return ResponseEntity.ok(ApiResponse.success(bookService.searchByTitle(title)));
    }

    @Override
    public ResponseEntity<ApiResponse<List<BookResponse>>> getByCategoryId(Long categoryId) {
        return ResponseEntity.ok(ApiResponse.success(bookService.getByCategoryId(categoryId)));
    }

    @Override
    public ResponseEntity<ApiResponse<List<BookResponse>>> getAvailableBooks() {
        return ResponseEntity.ok(ApiResponse.success(bookService.getAvailableBooks()));
    }
}