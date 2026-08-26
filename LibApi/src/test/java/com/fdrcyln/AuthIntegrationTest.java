package com.fdrcyln;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fdrcyln.Starter.LibApiApplication;
import com.fdrcyln.dto.request.LoginRequest;
import com.fdrcyln.dto.request.RegisterRequest;
import com.fdrcyln.entities.UserAccount;
import com.fdrcyln.enums.Role;
import com.fdrcyln.repository.UserAccountRepository;
import com.fdrcyln.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = LibApiApplication.class)
@Transactional
public class AuthIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    private UserAccount userAccount;
    private UserAccount adminAccount;
    private String userToken;
    private String adminToken;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        userAccountRepository.deleteAll();

        // Create normal user
        userAccount = new UserAccount();
        userAccount.setEmail("testuser@test.com");
        userAccount.setPassword(passwordEncoder.encode("password123"));
        userAccount.setRole(Role.USER);
        userAccount.setActive(true);
        userAccountRepository.save(userAccount);

        // Create admin user
        adminAccount = new UserAccount();
        adminAccount.setEmail("adminuser@test.com");
        adminAccount.setPassword(passwordEncoder.encode("admin123"));
        adminAccount.setRole(Role.ADMIN);
        adminAccount.setActive(true);
        userAccountRepository.save(adminAccount);

        userToken = jwtUtils.generateTokenFromEmail(userAccount.getEmail(), Role.USER.name(), userAccount.getId());
        adminToken = jwtUtils.generateTokenFromEmail(adminAccount.getEmail(), Role.ADMIN.name(), adminAccount.getId());
    }

    @Test
    @DisplayName("Başarılı Register işlemi sonucu kullanıcının rolü otomatik USER olmalıdır")
    void testRegisterSuccess() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("newuser@test.com", "password123");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.email", is("newuser@test.com")))
                .andExpect(jsonPath("$.data.role", is("USER")))
                .andExpect(jsonPath("$.data.accessToken", notNullValue()));
    }

    @Test
    @DisplayName("Duplicate email ile kayıt denenirse 400 BadRequest dönmelidir")
    void testRegisterDuplicateEmail() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("testuser@test.com", "password123");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("zaten kullanılmaktadır")));
    }

    @Test
    @DisplayName("Doğru email ve password ile başarılı login gerçekleşmelidir")
    void testLoginSuccess() throws Exception {
        LoginRequest loginRequest = new LoginRequest("testuser@test.com", "password123");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.role", is("USER")))
                .andExpect(jsonPath("$.data.accessToken", notNullValue()));
    }

    @Test
    @DisplayName("Yanlış password ile login denenirse 401 Unauthorized dönmelidir")
    void testLoginWrongPassword() throws Exception {
        LoginRequest loginRequest = new LoginRequest("testuser@test.com", "wrongpassword");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Sistemde bulunmayan email ile login denenirse 401 Unauthorized dönmelidir")
    void testLoginNonExistingEmail() throws Exception {
        LoginRequest loginRequest = new LoginRequest("nobody@test.com", "password123");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Token olmadan korumalı endpoint'e erişim 401 Unauthorized olmalıdır")
    void testProtectedEndpointWithoutToken() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("USER rolü ADMIN endpoint'ine erişmeye çalışınca 403 Forbidden dönmelidir")
    void testUserAccessAdminEndpointForbidden() throws Exception {
        mockMvc.perform(get("/api/members")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("ADMIN rolü ADMIN endpoint'ine başarıyla erişebilmelidir")
    void testAdminAccessAdminEndpointSuccess() throws Exception {
        mockMvc.perform(get("/api/members")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }
}
