package com.fdrcyln.controller;

import com.fdrcyln.common.ApiResponse;
import com.fdrcyln.dto.request.LoginRequest;
import com.fdrcyln.dto.request.RegisterRequest;
import com.fdrcyln.dto.response.AuthResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping({"/auth", "/api/auth"})
public interface IAuthController {

    @PostMapping("/register")
    ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request);

    @PostMapping("/login")
    ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request);
}
