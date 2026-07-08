package com.fdrcyln.dto.response;

import com.fdrcyln.enums.RentalStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class RentalResponse {

    private Long id;

    private Long bookId;

    private String bookTitle;

    private Long memberId;

    private String memberFullName;

    private LocalDate rentalDate;

    private LocalDate dueDate;

    private LocalDate returnDate;

    private RentalStatus status;

    private Boolean active;
}