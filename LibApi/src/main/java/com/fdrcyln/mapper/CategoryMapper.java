package com.fdrcyln.mapper;

import com.fdrcyln.dto.request.CreateCategoryRequest;
import com.fdrcyln.dto.response.CategoryResponse;
import com.fdrcyln.entities.Category;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public Category toEntity(CreateCategoryRequest request) {
        Category category = new Category();

        BeanUtils.copyProperties(request, category);
        category.setActive(true);

        return category;
    }

    public CategoryResponse toResponse(Category category) {
        CategoryResponse response = new CategoryResponse();

        BeanUtils.copyProperties(category, response);

        return response;
    }
}