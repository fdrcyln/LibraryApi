package com.fdrcyln.service.impl;

import com.fdrcyln.dto.request.LoginRequest;
import com.fdrcyln.dto.request.RegisterRequest;
import com.fdrcyln.dto.response.AuthResponse;
import com.fdrcyln.dto.response.UserAccountResponse;
import com.fdrcyln.entities.UserAccount;
import com.fdrcyln.enums.Role;
import com.fdrcyln.exception.BadRequestException;
import com.fdrcyln.exception.ResourceNotFoundException;
import com.fdrcyln.mapper.UserAccountMapper;
import com.fdrcyln.repository.UserAccountRepository;
import com.fdrcyln.security.JwtUtils;
import com.fdrcyln.security.UserAccountDetails;
import com.fdrcyln.service.IUserAccountService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserAccountServiceImpl implements IUserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final UserAccountMapper userAccountMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public UserAccountServiceImpl(
            UserAccountRepository userAccountRepository,
            UserAccountMapper userAccountMapper,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtUtils jwtUtils
    ) {
        this.userAccountRepository = userAccountRepository;
        this.userAccountMapper = userAccountMapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userAccountRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Bu e-posta adresi zaten kullanılmaktadır: " + request.getEmail());
        }

        UserAccount userAccount = userAccountMapper.toEntity(request);
        userAccount.setPassword(passwordEncoder.encode(request.getPassword()));
        userAccount.setRole(Role.USER); // Normal kayıtlarda otomatik USER rolü atanır

        UserAccount savedUser = userAccountRepository.save(userAccount);

        String token = jwtUtils.generateTokenFromEmail(savedUser.getEmail(), savedUser.getRole().name(), savedUser.getId());

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .role(savedUser.getRole())
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtUtils.generateJwtToken(authentication);

        UserAccountDetails userDetails = (UserAccountDetails) authentication.getPrincipal();

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .role(userDetails.getRole())
                .userId(userDetails.getId())
                .email(userDetails.getUsername())
                .build();
    }

    @Override
    public List<UserAccountResponse> getAllUsers() {
        return userAccountRepository.findAll().stream()
                .map(userAccountMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserAccountResponse getUserById(Long id) {
        UserAccount userAccount = userAccountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı ID: " + id));
        return userAccountMapper.toResponse(userAccount);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        UserAccount userAccount = userAccountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı ID: " + id));
        userAccount.setActive(false);
        userAccountRepository.save(userAccount);
    }
}
