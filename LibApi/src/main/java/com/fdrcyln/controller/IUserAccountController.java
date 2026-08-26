package com.fdrcyln.controller;

import com.fdrcyln.common.ApiResponse;
import com.fdrcyln.dto.response.UserAccountResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/api/user-accounts")
public interface IUserAccountController {

    @GetMapping
    ResponseEntity<ApiResponse<List<UserAccountResponse>>> getAll();

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<UserAccountResponse>> getById(@PathVariable Long id);

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id);
}
