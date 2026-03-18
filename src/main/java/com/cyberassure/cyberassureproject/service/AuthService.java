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
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.cyberassure.cyberassureproject.security.JwtService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
        private final JwtService jwtService;
        private final UserRepository userRepository;
        private final RoleRepository roleRepository;
        private final PasswordEncoder passwordEncoder;

        public RegisterResponse register(RegisterRequest request) {

                if (userRepository.existsByEmail(request.getEmail())) {
                        throw new EmailAlreadyExistsException(
                                        "An account with email '" + request.getEmail()
                                                        + "' already exists. Please log in or use a different email.");
                }

                Role customerRole = roleRepository.findByRoleName("ROLE_CUSTOMER")
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Customer role not configured in the system. Please contact support."));

                User user = User.builder()
                                .fullName(request.getFullName())
                                .email(request.getEmail())
                                .passwordHash(passwordEncoder.encode(request.getPassword()))
                                .companyName(request.getCompanyName())
                                .industry(request.getIndustry())
                                .companySize(request.getCompanySize())
                                .companyAddress(request.getCompanyAddress())
                                .companyWebsite(request.getCompanyWebsite())
                                .registrationNumber(request.getRegistrationNumber())
                                .annualRevenue(request.getAnnualRevenue())
                                .phoneNumber(request.getPhoneNumber())
                                .role(customerRole)
                                .createdAt(LocalDateTime.now())
                                .accountStatus("ACTIVE")
                                .build();

                userRepository.save(user);

                return RegisterResponse.builder()
                                .message("Registration successful")
                                .email(user.getEmail())
                                .build();
        }

        public LoginResponse login(LoginRequest request) {

                User user = userRepository.findByEmail(request.getEmail())
                                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

                if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
                        throw new InvalidCredentialsException("Email or password is incorrect. Please try again.");
                }

                String token = jwtService.generateToken(
                                user.getEmail(),
                                user.getRole().getRoleName());

                return LoginResponse.builder()
                                .message("Login successful")
                                .email(user.getEmail())
                                .role(user.getRole().getRoleName())
                                .token(token)
                                .build();
        }

}
