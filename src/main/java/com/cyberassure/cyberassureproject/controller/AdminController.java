package com.cyberassure.cyberassureproject.controller;

import com.cyberassure.cyberassureproject.dto.CreateStaffRequest;
import com.cyberassure.cyberassureproject.entity.User;
import com.cyberassure.cyberassureproject.repository.UserRepository;
import com.cyberassure.cyberassureproject.repository.PolicySubscriptionRepository;
import com.cyberassure.cyberassureproject.repository.RiskAssessmentRepository;
import com.cyberassure.cyberassureproject.repository.ClaimRepository;
import com.cyberassure.cyberassureproject.service.AdminService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final UserRepository userRepository;
    private final PolicySubscriptionRepository subscriptionRepository;
    private final RiskAssessmentRepository riskRepository;
    private final ClaimRepository claimRepository;

    // CREATE STAFF
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create-staff")
    public ResponseEntity<User> createStaff(
            @Valid @RequestBody CreateStaffRequest request) {

        return ResponseEntity.ok(adminService.createStaff(request));
    }

    // GET ALL USERS
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    // DASHBOARD STATS
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {

        Map<String, Object> stats = new HashMap<>();

        stats.put("totalUsers", userRepository.count());
        stats.put("totalPolicies", subscriptionRepository.count());
        stats.put("riskAssessments", riskRepository.count());
        stats.put("claims", claimRepository.count());

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/customers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getCustomers() {
        return ResponseEntity.ok(adminService.getCustomers());
    }

}