package com.fdrcyln.service;

import com.fdrcyln.dto.request.LoginRequest;
import com.fdrcyln.dto.request.RegisterRequest;
import com.fdrcyln.dto.response.AuthResponse;
import com.fdrcyln.dto.response.UserAccountResponse;

import java.util.List;

public interface IUserAccountService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    List<UserAccountResponse> getAllUsers();

    UserAccountResponse getUserById(Long id);

    void deleteUser(Long id);
}
