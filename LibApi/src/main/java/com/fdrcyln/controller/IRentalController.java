package com.fdrcyln.controller;

import com.fdrcyln.common.ApiResponse;
import com.fdrcyln.dto.request.CreateRentalRequest;
import com.fdrcyln.dto.response.RentalResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/rentals")
public interface IRentalController {

    @PostMapping
    ResponseEntity<ApiResponse<RentalResponse>> rentBook(@Valid @RequestBody CreateRentalRequest request);

    @PutMapping("/{id}/return")
    ResponseEntity<ApiResponse<RentalResponse>> returnBook(@PathVariable Long id);

    @GetMapping("/active")
    ResponseEntity<ApiResponse<List<RentalResponse>>> getActiveRentals();

    @GetMapping("/member/{memberId}")
    ResponseEntity<ApiResponse<List<RentalResponse>>> getRentalsByMember(@PathVariable Long memberId);

    @GetMapping("/late")
    ResponseEntity<ApiResponse<List<RentalResponse>>> getLateRentals();
}