package com.fdrcyln.controller.impl;

import com.fdrcyln.common.ApiResponse;
import com.fdrcyln.controller.ICategoryController;
import com.fdrcyln.dto.request.CreateCategoryRequest;
import com.fdrcyln.dto.request.UpdateCategoryRequest;
import com.fdrcyln.dto.response.CategoryResponse;
import com.fdrcyln.service.ICategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CategoryControllerImpl implements ICategoryController {

    private final ICategoryService categoryService;

    public CategoryControllerImpl(ICategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Override
    public ResponseEntity<ApiResponse<CategoryResponse>> save(CreateCategoryRequest request) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.save(request)));
    }

    @Override
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getAll()));
    }

    @Override
    public ResponseEntity<ApiResponse<CategoryResponse>> getById(Long id) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getById(id)));
    }

    @Override
    public ResponseEntity<ApiResponse<CategoryResponse>> update(Long id, UpdateCategoryRequest request) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.update(id, request)));
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> delete(Long id) {
        categoryService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Kategori başarıyla pasif hale getirildi", null));
    }
}