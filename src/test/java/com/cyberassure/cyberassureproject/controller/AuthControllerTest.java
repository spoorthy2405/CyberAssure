package com.cyberassure.cyberassureproject.controller;

import com.cyberassure.cyberassureproject.dto.LoginRequest;
import com.cyberassure.cyberassureproject.dto.LoginResponse;
import com.cyberassure.cyberassureproject.dto.RegisterRequest;
import com.cyberassure.cyberassureproject.dto.RegisterResponse;
import com.cyberassure.cyberassureproject.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void register_Success() {
        RegisterRequest req = new RegisterRequest();
        req.setFullName("Test User");
        req.setEmail("test@test.com");

        RegisterResponse resp = RegisterResponse.builder()
                .message("Registration successful").email("test@test.com").build();

        when(authService.register(any(RegisterRequest.class))).thenReturn(resp);

        ResponseEntity<RegisterResponse> response = authController.register(req);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Registration successful", response.getBody().getMessage());
    }

    @Test
    void login_Success() {
        LoginRequest req = new LoginRequest();
        req.setEmail("test@test.com");

        LoginResponse resp = LoginResponse.builder()
                .message("Login successful").token("mock.jwt.token").build();

        when(authService.login(any(LoginRequest.class))).thenReturn(resp);

        ResponseEntity<LoginResponse> response = authController.login(req);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("mock.jwt.token", response.getBody().getToken());
    }
}
