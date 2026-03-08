package com.cyberassure.cyberassureproject.config;

import com.cyberassure.cyberassureproject.entity.Role;
import com.cyberassure.cyberassureproject.entity.User;
import com.cyberassure.cyberassureproject.repository.RoleRepository;
import com.cyberassure.cyberassureproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedRoles();
        seedAdmin();
    }

    private void seedRoles() {

        createRoleIfNotExists("ROLE_ADMIN", "System Administrator");
        createRoleIfNotExists("ROLE_CUSTOMER", "Customer User");
        createRoleIfNotExists("ROLE_UNDERWRITER", "Policy Approval Officer");
        createRoleIfNotExists("ROLE_CLAIMS_OFFICER", "Claims Processing Officer");
    }

    private void createRoleIfNotExists(String name, String desc) {
        if (!roleRepository.existsByRoleName(name)) {
            roleRepository.save(
                    Role.builder()
                            .roleName(name)
                            .roleDescription(desc)
                            .isActive(true)
                            .build()
            );
        }
    }

    private void seedAdmin() {

        if (!userRepository.existsByEmail("admin@cyberassure.com")) {

            Role adminRole = roleRepository
                    .findByRoleName("ROLE_ADMIN")  // IMPORTANT FIX
                    .orElseThrow(() -> new RuntimeException("ROLE_ADMIN not found"));

            User admin = User.builder()
                    .fullName("System Administrator")
                    .email("admin@cyberassure.com")
                    .passwordHash(passwordEncoder.encode("Admin@123"))
                    .companyName("CyberAssure Platform")
                    .phoneNumber("9999999999")
                    .role(adminRole)
                    .accountStatus("ACTIVE")
                    .createdAt(LocalDateTime.now())
                    .build();

            userRepository.save(admin);

            System.out.println("Default ADMIN created.");
        }
    }
}