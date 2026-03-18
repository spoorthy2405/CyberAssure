package com.cyberassure.cyberassureproject.controller;
import com.cyberassure.cyberassureproject.dto.LoginRequest;
import com.cyberassure.cyberassureproject.dto.LoginResponse;
import com.cyberassure.cyberassureproject.dto.RegisterRequest;
import com.cyberassure.cyberassureproject.dto.RegisterResponse;
import com.cyberassure.cyberassureproject.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        return ResponseEntity.ok(authService.register(request));
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(authService.login(request));
    }
}
