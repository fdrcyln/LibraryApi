package com.fdrcyln.service;

import com.fdrcyln.dto.request.CreateRentalRequest;
import com.fdrcyln.dto.response.RentalResponse;

import java.util.List;

public interface IRentalService {

    RentalResponse rentBook(CreateRentalRequest request);

    RentalResponse returnBook(Long rentalId);

    List<RentalResponse> getActiveRentals();

    List<RentalResponse> getRentalsByMember(Long memberId);

    List<RentalResponse> getLateRentals();
}