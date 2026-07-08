package com.fdrcyln.controller;

import com.fdrcyln.common.ApiResponse;
import com.fdrcyln.dto.request.CreateMemberRequest;
import com.fdrcyln.dto.request.UpdateMemberRequest;
import com.fdrcyln.dto.response.MemberResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/members")
public interface IMemberController {

    @PostMapping
    ResponseEntity<ApiResponse<MemberResponse>> save(@Valid @RequestBody CreateMemberRequest request);

    @GetMapping
    ResponseEntity<ApiResponse<List<MemberResponse>>> getAll();

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<MemberResponse>> getById(@PathVariable Long id);

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<MemberResponse>> update(@PathVariable Long id, @Valid @RequestBody UpdateMemberRequest request);

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id);
}