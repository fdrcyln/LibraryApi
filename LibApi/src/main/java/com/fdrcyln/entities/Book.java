package com.fdrcyln.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Book extends BaseEntity {
	private String title;

	private String author;

	@Column(unique = true, nullable = false)
	private String isbn;

	private Integer totalStock;

	private Integer availableStock;

	private Integer pageCount;

	private Integer publicationYear;

	@ManyToOne
	@JoinColumn(name = "category_id")
	private Category category;
}
