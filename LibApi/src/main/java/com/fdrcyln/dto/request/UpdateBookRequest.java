package com.fdrcyln.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateBookRequest {

    @NotBlank(message = "Kitap adı boş olamaz")
    private String title;

    @NotBlank(message = "Yazar adı boş olamaz")
    private String author;

    @NotBlank(message = "ISBN boş olamaz")
    private String isbn;

    @NotNull(message = "Toplam stok null olamaz")
    @Min(value = 1, message = "Toplam stok en az 1 olmalıdır")
    private Integer totalStock;

    @Min(value = 1, message = "Sayfa sayısı en az 1 olmalıdır")
    private Integer pageCount;

    @Min(value = 1000, message = "Yayın yılı geçersiz")
    private Integer publicationYear;
    
    @NotNull(message = "Kategori ID null olamaz")
    private Long categoryId;
}
