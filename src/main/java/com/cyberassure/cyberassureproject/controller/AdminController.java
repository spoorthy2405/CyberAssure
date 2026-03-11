package com.cyberassure.cyberassureproject.controller;

import com.cyberassure.cyberassureproject.dto.CreateStaffRequest;
import com.cyberassure.cyberassureproject.dto.*;
import com.cyberassure.cyberassureproject.entity.*;
import com.cyberassure.cyberassureproject.entity.Claim;
import com.cyberassure.cyberassureproject.repository.*;
import com.cyberassure.cyberassureproject.service.AdminService;
import com.cyberassure.cyberassureproject.entity.ClaimStatus;
import com.cyberassure.cyberassureproject.dto.CyberPolicyRequest;
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

        // USER COUNTS
        stats.put("totalUsers", userRepository.count());
        stats.put("totalCustomers", userRepository.findAll().stream()
                .filter(u -> u.getRole().getRoleName().equals("ROLE_CUSTOMER")).count());
        stats.put("totalUnderwriters", userRepository.findAll().stream()
                .filter(u -> u.getRole().getRoleName().equals("ROLE_UNDERWRITER")).count());
        stats.put("totalClaimsOfficers", userRepository.findAll().stream()
                .filter(u -> u.getRole().getRoleName().equals("ROLE_CLAIMS_OFFICER")).count());

        // POLICY & RISK
        stats.put("totalPolicies", subscriptionRepository.count());
        stats.put("riskAssessments", riskRepository.count());

        // CLAIM STATS
        stats.put("totalClaims", claimRepository.count());
        stats.put("activeClaims", claimRepository.findAll().stream()
                .filter(c -> c.getStatus() == ClaimStatus.PENDING).count());
        stats.put("approvedClaims", claimRepository.findAll().stream()
                .filter(c -> c.getStatus() == ClaimStatus.APPROVED).count());
        stats.put("rejectedClaims", claimRepository.findAll().stream()
                .filter(c -> c.getStatus() == ClaimStatus.REJECTED).count());

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

    @PutMapping("/staff/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateStaff(@PathVariable Long id, @RequestBody UpdateStaffRequest request) {
        return ResponseEntity.ok(adminService.updateStaff(id, request));
    }

    @PutMapping("/staff/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateStaff(@PathVariable Long id) {
        adminService.deactivateStaff(id);
        return ResponseEntity.noContent().build();
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
    public ResponseEntity<List<Claim>> getClaims() {
        return ResponseEntity.ok(claimRepository.findAll().stream()
                .sorted(java.util.Comparator.comparing(Claim::getFiledAt, java.util.Comparator.nullsLast(java.util.Comparator.reverseOrder())))
                .collect(java.util.stream.Collectors.toList()));
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

    // CYBER POLICIES (CATALOG)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/cyber-policies")
    public ResponseEntity<List<CyberPolicy>> getAllCyberPolicies() {
        return ResponseEntity.ok(adminService.getAllCyberPolicies());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/cyber-policies")
    public ResponseEntity<CyberPolicy> createCyberPolicy(@RequestBody CyberPolicyRequest request) {
        return ResponseEntity.ok(adminService.createCyberPolicy(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/cyber-policies/{id}")
    public ResponseEntity<CyberPolicy> updateCyberPolicy(@PathVariable Long id,
            @RequestBody CyberPolicyRequest request) {
        return ResponseEntity.ok(adminService.updateCyberPolicy(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/cyber-policies/{id}")
    public ResponseEntity<Void> deleteCyberPolicy(@PathVariable Long id) {
        adminService.deleteCyberPolicy(id);
        return ResponseEntity.noContent().build();
    }

    // ASSIGN UNDERWRITER
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/subscriptions/{id}/assign")
    public ResponseEntity<Map<String, String>> assignUnderwriter(@PathVariable Long id,
            @RequestBody Map<String, Object> payload) {
        Object underwriterIdObj = payload.get("underwriterId");
        if (underwriterIdObj == null) {
            throw new IllegalArgumentException("underwriterId is required");
        }
        Long underwriterId = Long.valueOf(underwriterIdObj.toString());
        adminService.assignUnderwriter(id, underwriterId);
        return ResponseEntity.ok(Map.of("message", "Success"));
    }

    // ASSIGN CLAIMS OFFICER
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/claims/{id}/assign")
    public ResponseEntity<Map<String, String>> assignClaimsOfficer(@PathVariable Long id,
            @RequestBody Map<String, Object> payload) {
        Object officerIdObj = payload.get("officerId");
        if (officerIdObj == null) {
            throw new IllegalArgumentException("officerId is required");
        }
        Long officerId = Long.valueOf(officerIdObj.toString());
        adminService.assignClaimsOfficer(id, officerId);
        return ResponseEntity.ok(Map.of("message", "Success"));
    }
}
