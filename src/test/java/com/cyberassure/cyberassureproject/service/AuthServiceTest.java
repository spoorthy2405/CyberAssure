package com.cyberassure.cyberassureproject.service;

import com.cyberassure.cyberassureproject.dto.LoginRequest;
import com.cyberassure.cyberassureproject.dto.LoginResponse;
import com.cyberassure.cyberassureproject.dto.RegisterRequest;
import com.cyberassure.cyberassureproject.dto.RegisterResponse;
import com.cyberassure.cyberassureproject.entity.Role;
import com.cyberassure.cyberassureproject.entity.User;
import com.cyberassure.cyberassureproject.exception.EmailAlreadyExistsException;
import com.cyberassure.cyberassureproject.exception.InvalidCredentialsException;
import com.cyberassure.cyberassureproject.exception.ResourceNotFoundException;
import com.cyberassure.cyberassureproject.repository.RoleRepository;
import com.cyberassure.cyberassureproject.repository.UserRepository;
import com.cyberassure.cyberassureproject.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;

    @InjectMocks private AuthService authService;

    private Role customerRole;
    private User existingUser;

    @BeforeEach
    void setUp() {
        customerRole = Role.builder().roleId(1L).roleName("ROLE_CUSTOMER").isActive(true).build();
        existingUser = User.builder()
                .userId(1L).fullName("Alice").email("alice@test.com")
                .passwordHash("$2a$10$hashed").role(customerRole).accountStatus("ACTIVE").build();
    }

    // ─── REGISTER ────────────────────────────────────────────────
    @Test
    void register_Success_ReturnsResponse() {
        RegisterRequest req = new RegisterRequest();
        req.setFullName("Alice"); req.setEmail("alice@test.com"); req.setPassword("pass123");

        when(userRepository.existsByEmail("alice@test.com")).thenReturn(false);
        when(roleRepository.findByRoleName("ROLE_CUSTOMER")).thenReturn(Optional.of(customerRole));
        when(passwordEncoder.encode("pass123")).thenReturn("$2a$10$hashed");
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        RegisterResponse response = authService.register(req);

        assertNotNull(response);
        assertEquals("Registration successful", response.getMessage());
        assertEquals("alice@test.com", response.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_ThrowsEmailAlreadyExistsException_WhenDuplicate() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("alice@test.com");

        when(userRepository.existsByEmail("alice@test.com")).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> authService.register(req));
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_ThrowsResourceNotFoundException_WhenCustomerRoleMissing() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("new@test.com"); req.setPassword("pass123");

        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(roleRepository.findByRoleName("ROLE_CUSTOMER")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authService.register(req));
    }

    // ─── LOGIN ────────────────────────────────────────────────────
    @Test
    void login_Success_ReturnsTokenResponse() {
        LoginRequest req = new LoginRequest();
        req.setEmail("alice@test.com"); req.setPassword("pass123");

        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("pass123", "$2a$10$hashed")).thenReturn(true);
        when(jwtService.generateToken("alice@test.com", "ROLE_CUSTOMER")).thenReturn("mock.jwt.token");

        LoginResponse response = authService.login(req);

        assertNotNull(response);
        assertEquals("Login successful", response.getMessage());
        assertEquals("alice@test.com", response.getEmail());
        assertEquals("ROLE_CUSTOMER", response.getRole());
        assertEquals("mock.jwt.token", response.getToken());
    }

    @Test
    void login_ThrowsInvalidCredentialsException_WhenUserNotFound() {
        LoginRequest req = new LoginRequest();
        req.setEmail("ghost@test.com"); req.setPassword("pass123");

        when(userRepository.findByEmail("ghost@test.com")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> authService.login(req));
    }

    @Test
    void login_ThrowsInvalidCredentialsException_WhenPasswordWrong() {
        LoginRequest req = new LoginRequest();
        req.setEmail("alice@test.com"); req.setPassword("wrongpass");

        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("wrongpass", "$2a$10$hashed")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(req));
    }
}
