
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

    // USERS
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    // DASHBOARD
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

    // CUSTOMERS
    @GetMapping("/customers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getCustomers() {
        return ResponseEntity.ok(adminService.getCustomers());
    }

    // STAFF
    @GetMapping("/staff")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getStaff() {
        return ResponseEntity.ok(adminService.getStaff());
    }

    // UNDERWRITERS
    @GetMapping("/underwriters")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getUnderwriters() {
        return ResponseEntity.ok(adminService.getUnderwriters());
    }

    // CLAIMS OFFICERS
    @GetMapping("/claims-officers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getClaimsOfficers() {
        return ResponseEntity.ok(adminService.getClaimsOfficers());
    }

    // POLICIES
    @GetMapping("/policies")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<?>> getPolicies() {
        return ResponseEntity.ok(subscriptionRepository.findAll());
    }

    // CLAIMS
    @GetMapping("/claims")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<?>> getClaims() {
        return ResponseEntity.ok(claimRepository.findAll());
    }

    // ANALYTICS
    @GetMapping("/analytics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAnalytics() {

        Map<String, Object> analytics = new HashMap<>();

        analytics.put("totalRevenue", 14200000);
        analytics.put("claimsPayout", 3800000);
        analytics.put("newPolicies", subscriptionRepository.count());
        analytics.put("resolutionTime", 4.2);

        return ResponseEntity.ok(analytics);
    }

    // RISK MONITORING API
    @GetMapping("/risk-monitoring")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getRiskMonitoring() {

        Map<String, Object> risk = new HashMap<>();

        risk.put("highRiskEntities", riskRepository.count());
        risk.put("averageRiskScore", 38);
        risk.put("totalClaims", claimRepository.count());
        risk.put("totalPolicies", subscriptionRepository.count());

        return ResponseEntity.ok(risk);
    }

}
