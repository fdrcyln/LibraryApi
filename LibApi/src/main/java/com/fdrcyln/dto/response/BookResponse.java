package com.fdrcyln.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookResponse {

    private Long id;

    private String title;

    private String author;

    private String isbn;

    private Integer totalStock;

    private Integer availableStock;

    private Integer pageCount;

    private Integer publicationYear;

    private Boolean active;
    
    private Long categoryId;

    private String categoryName;
}