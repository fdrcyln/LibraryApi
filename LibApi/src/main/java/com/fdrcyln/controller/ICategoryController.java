package com.fdrcyln.controller;

import com.fdrcyln.common.ApiResponse;
import com.fdrcyln.dto.request.CreateCategoryRequest;
import com.fdrcyln.dto.request.UpdateCategoryRequest;
import com.fdrcyln.dto.response.CategoryResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/categories")
public interface ICategoryController {

    @PostMapping
    ResponseEntity<ApiResponse<CategoryResponse>> save(@Valid @RequestBody CreateCategoryRequest request);

    @GetMapping
    ResponseEntity<ApiResponse<List<CategoryResponse>>> getAll();

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<CategoryResponse>> getById(@PathVariable Long id);

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<CategoryResponse>> update(@PathVariable Long id, @Valid @RequestBody UpdateCategoryRequest request);

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id);
}