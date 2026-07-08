package com.fdrcyln.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRentalRequest {

    @NotNull(message = "Kitap ID null olamaz")
    private Long bookId;

    @NotNull(message = "Üye ID null olamaz")
    private Long memberId;

    @NotNull(message = "Kiralama süresi null olamaz")
    @Min(value = 1, message = "Kiralama süresi en az 1 gün olmalıdır")
    private Integer rentalDay;
}