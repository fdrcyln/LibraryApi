package com.fdrcyln.controller.impl;

import com.fdrcyln.common.ApiResponse;
import com.fdrcyln.controller.IAuthController;
import com.fdrcyln.dto.request.LoginRequest;
import com.fdrcyln.dto.request.RegisterRequest;
import com.fdrcyln.dto.response.AuthResponse;
import com.fdrcyln.service.IUserAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthControllerImpl implements IAuthController {

    private final IUserAccountService userAccountService;

    public AuthControllerImpl(IUserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @Override
    public ResponseEntity<ApiResponse<AuthResponse>> register(RegisterRequest request) {
        AuthResponse response = userAccountService.register(request);
        return ResponseEntity.ok(ApiResponse.success("Kullanıcı kaydı başarıyla oluşturuldu.", response));
    }

    @Override
    public ResponseEntity<ApiResponse<AuthResponse>> login(LoginRequest request) {
        AuthResponse response = userAccountService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Giriş başarılı.", response));
    }
}
