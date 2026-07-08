package com.fdrcyln.entities;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseEntity {
	private String firstName;

	private String lastName;

	@Column(unique = true, nullable = false)
	private String email;

	private String phone;

	private String address;

	private LocalDate membershipDate;
}
