package com.fdrcyln.controller.impl;

import com.fdrcyln.common.ApiResponse;
import com.fdrcyln.controller.IRentalController;
import com.fdrcyln.dto.request.CreateRentalRequest;
import com.fdrcyln.dto.response.RentalResponse;
import com.fdrcyln.service.IRentalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RentalControllerImpl implements IRentalController {

    private final IRentalService rentalService;

    public RentalControllerImpl(IRentalService rentalService) {
        this.rentalService = rentalService;
    }

    @Override
    public ResponseEntity<ApiResponse<RentalResponse>> rentBook(CreateRentalRequest request) {
        return ResponseEntity.ok(ApiResponse.success(rentalService.rentBook(request)));
    }

    @Override
    public ResponseEntity<ApiResponse<RentalResponse>> returnBook(Long id) {
        return ResponseEntity.ok(ApiResponse.success("Kitap başarıyla teslim edildi", rentalService.returnBook(id)));
    }

    @Override
    public ResponseEntity<ApiResponse<List<RentalResponse>>> getActiveRentals() {
        return ResponseEntity.ok(ApiResponse.success(rentalService.getActiveRentals()));
    }

    @Override
    public ResponseEntity<ApiResponse<List<RentalResponse>>> getRentalsByMember(Long memberId) {
        return ResponseEntity.ok(ApiResponse.success(rentalService.getRentalsByMember(memberId)));
    }

    @Override
    public ResponseEntity<ApiResponse<List<RentalResponse>>> getLateRentals() {
        return ResponseEntity.ok(ApiResponse.success(rentalService.getLateRentals()));
    }
}