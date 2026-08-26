package com.fdrcyln.controller.impl;

import com.fdrcyln.common.ApiResponse;
import com.fdrcyln.controller.IUserAccountController;
import com.fdrcyln.dto.response.UserAccountResponse;
import com.fdrcyln.service.IUserAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserAccountControllerImpl implements IUserAccountController {

    private final IUserAccountService userAccountService;

    public UserAccountControllerImpl(IUserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserAccountResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(userAccountService.getAllUsers()));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserAccountResponse>> getById(Long id) {
        return ResponseEntity.ok(ApiResponse.success(userAccountService.getUserById(id)));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(Long id) {
        userAccountService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("Kullanıcı hesabı pasifleştirildi.", null));
    }
}
