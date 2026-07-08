package com.fdrcyln.service.impl;

import com.fdrcyln.dto.request.CreateCategoryRequest;
import com.fdrcyln.dto.request.UpdateCategoryRequest;
import com.fdrcyln.dto.response.CategoryResponse;
import com.fdrcyln.entities.Category;
import com.fdrcyln.exception.BadRequestException;
import com.fdrcyln.exception.ResourceNotFoundException;
import com.fdrcyln.mapper.CategoryMapper;
import com.fdrcyln.repository.BookRepository;
import com.fdrcyln.repository.ICategoryRepository;
import com.fdrcyln.service.ICategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryServiceImpl implements ICategoryService {

    private final ICategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final BookRepository bookRepository;

    public CategoryServiceImpl(ICategoryRepository categoryRepository, CategoryMapper categoryMapper, BookRepository bookRepository) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.bookRepository = bookRepository;
    }

    @Override
    @Transactional
    public CategoryResponse save(CreateCategoryRequest request) {
        String trimmedName = request.getName() != null ? request.getName().trim() : "";
        if (categoryRepository.existsByNameIgnoreCase(trimmedName)) {
            throw new BadRequestException("Bu kategori zaten mevcut.");
        }
        Category category = categoryMapper.toEntity(request);
        category.setName(trimmedName);
        if (category.getDescription() != null) {
            category.setDescription(category.getDescription().trim());
        }
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toResponse(savedCategory);
    }

    @Override
    public List<CategoryResponse> getAll() {
        return categoryRepository.findByActiveTrue()
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    @Override
    public CategoryResponse getById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kategori bulunamadı. ID: " + id));

        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional
    public CategoryResponse update(Long id, UpdateCategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kategori bulunamadı. ID: " + id));

        String trimmedName = request.getName() != null ? request.getName().trim() : "";
        if (categoryRepository.existsByNameIgnoreCaseAndIdNot(trimmedName, id)) {
            throw new BadRequestException("Bu kategori zaten mevcut.");
        }

        category.setName(trimmedName);
        category.setDescription(request.getDescription() != null ? request.getDescription().trim() : "");
        category.setActive(request.getActive());

        Category updatedCategory = categoryRepository.save(category);

        return categoryMapper.toResponse(updatedCategory);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kategori bulunamadı. ID: " + id));

        if (bookRepository.existsByCategoryIdAndActiveTrue(id)) {
            throw new BadRequestException("Bu kategoriye bağlı aktif kitaplar olduğu için kategori silinemez.");
        }

        category.setActive(false);
        categoryRepository.save(category);
    }
}