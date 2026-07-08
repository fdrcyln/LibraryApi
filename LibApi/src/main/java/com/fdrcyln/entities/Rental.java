package com.fdrcyln.entities;

import java.time.LocalDate; 

import com.fdrcyln.enums.RentalStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "rentals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Rental extends BaseEntity {
	@ManyToOne
	@JoinColumn(name = "book_id")
	private Book book;

	@ManyToOne
	@JoinColumn(name = "member_id")
	private Member member;

	private LocalDate rentalDate;

	private LocalDate dueDate;

	private LocalDate returnDate;

	@Enumerated(EnumType.STRING)
	private RentalStatus status;

}
