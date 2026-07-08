package com.fdrcyln.service;

import com.fdrcyln.dto.request.CreateCategoryRequest;
import com.fdrcyln.dto.request.UpdateCategoryRequest;
import com.fdrcyln.dto.response.CategoryResponse;

import java.util.List;

public interface ICategoryService {

    CategoryResponse save(CreateCategoryRequest request);

    List<CategoryResponse> getAll();

    CategoryResponse getById(Long id);

    CategoryResponse update(Long id, UpdateCategoryRequest request);

    void delete(Long id);
}