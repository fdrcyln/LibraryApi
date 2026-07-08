package com.fdrcyln.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fdrcyln.common.ApiResponse;
import com.fdrcyln.dto.request.CreateBookRequest;
import com.fdrcyln.dto.request.UpdateBookRequest;
import com.fdrcyln.dto.response.BookResponse;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/api/books")
public interface IBookController {
    @PostMapping
    ResponseEntity<ApiResponse<BookResponse>> save(@Valid @RequestBody CreateBookRequest request);

    @GetMapping
    ResponseEntity<ApiResponse<List<BookResponse>>> getAll();

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<BookResponse>> getById(@PathVariable Long id);

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<BookResponse>> update(@PathVariable Long id, @Valid @RequestBody UpdateBookRequest request);

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id);

    @GetMapping("/search")
    ResponseEntity<ApiResponse<List<BookResponse>>> searchByTitle(@RequestParam String title);

    @GetMapping("/category/{categoryId}")
    ResponseEntity<ApiResponse<List<BookResponse>>> getByCategoryId(@PathVariable Long categoryId);

    @GetMapping("/available")
    ResponseEntity<ApiResponse<List<BookResponse>>> getAvailableBooks();
}
