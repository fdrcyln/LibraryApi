package com.fdrcyln.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCategoryRequest {

    @NotBlank(message = "Kategori adı boş olamaz")
    private String name;

    private String description;
}