package com.fdrcyln.repository;

import com.fdrcyln.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ICategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByActiveTrue();
    boolean existsByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
    java.util.Optional<Category> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCaseAndActiveTrue(String name);
    boolean existsByNameIgnoreCaseAndIdNotAndActiveTrue(String name, Long id);
}