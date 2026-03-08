package com.cyberassure.cyberassureproject.service;

import com.cyberassure.cyberassureproject.dto.CreateStaffRequest;
import com.cyberassure.cyberassureproject.entity.Role;
import com.cyberassure.cyberassureproject.entity.User;
import com.cyberassure.cyberassureproject.repository.RoleRepository;
import com.cyberassure.cyberassureproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public User createStaff(CreateStaffRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if (!request.getRoleName().equals("ROLE_UNDERWRITER")
                && !request.getRoleName().equals("ROLE_CLAIMS_OFFICER")) {
            throw new RuntimeException("Invalid staff role");
        }

        Role role = roleRepository.findByRoleName(request.getRoleName())
                .orElseThrow(() -> new RuntimeException("Role not found"));

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .companyName("CyberAssure Internal")
                .phoneNumber("NA")
                .role(role)
                .accountStatus("ACTIVE")
                .createdAt(LocalDateTime.now())
                .build();

        return userRepository.save(user);
    }

    public List<User> getCustomers() {

        return userRepository.findAll()
                .stream()
                .filter(u -> u.getRole().getRoleName().equals("ROLE_CUSTOMER"))
                .toList();

    }

}