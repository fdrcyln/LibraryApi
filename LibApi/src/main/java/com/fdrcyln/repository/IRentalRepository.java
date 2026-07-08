package com.fdrcyln.repository;

import com.fdrcyln.entities.Rental;
import com.fdrcyln.enums.RentalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface IRentalRepository extends JpaRepository<Rental, Long> {

    List<Rental> findByStatus(RentalStatus status);

    List<Rental> findByMemberId(Long memberId);

    List<Rental> findByDueDateBeforeAndStatus(LocalDate date, RentalStatus status);

    boolean existsByBookIdAndMemberIdAndStatus(Long bookId, Long memberId, RentalStatus status);

    long countByMemberIdAndStatus(Long memberId, RentalStatus status);
}