package com.cyberassure.cyberassureproject.controller;

import com.cyberassure.cyberassureproject.dto.*;
import com.cyberassure.cyberassureproject.entity.Claim;
import com.cyberassure.cyberassureproject.service.ClaimService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/claims")
@RequiredArgsConstructor
public class ClaimController {

    private final ClaimService claimService;

    // CUSTOMER FILE CLAIM
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping
    public ResponseEntity<Claim> fileClaim(
            @Valid @RequestBody CreateClaimRequest request) {
        return ResponseEntity.ok(claimService.fileClaim(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'CLAIMS_OFFICER', 'ADMIN')")
    public ResponseEntity<Claim> getClaim(@PathVariable Long id) {
        // Implement get logic. Currently used via general lists, but good to have.
        // Returning null for now to just show endpoint exists, can implement full lookup
        // later.
        return ResponseEntity.ok(null);
    }

    // GET CLAIM ASSESSMENT
    @GetMapping("/{id}/assessment")
    @PreAuthorize("hasAnyRole('CLAIMS_OFFICER', 'ADMIN')")
    public ResponseEntity<ClaimAssessmentResult> getClaimAssessment(@PathVariable Long id) {
         return ResponseEntity.ok(claimService.assessClaim(id));
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/my")
    public ResponseEntity<List<Claim>> getMyClaims(java.security.Principal principal) {
        return ResponseEntity.ok(claimService.getClaimsByCustomer(principal.getName()));
    }

    // CLAIMS OFFICER REVIEW
    @PreAuthorize("hasRole('CLAIMS_OFFICER')")
    @PutMapping("/{id}/review")
    public ResponseEntity<Claim> reviewClaim(
            @PathVariable Long id,
            @RequestBody ClaimDecisionRequest request) {
        return ResponseEntity.ok(claimService.reviewClaim(id, request));
    }

    // SETTLE CLAIM
    @PreAuthorize("hasRole('CLAIMS_OFFICER')")
    @PutMapping("/{id}/settle")
    public ResponseEntity<Claim> settleClaim(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        BigDecimal amount = new BigDecimal(body.get("settlementAmount").toString());
        String notes = body.getOrDefault("notes", "").toString();
        return ResponseEntity.ok(claimService.settleClaim(id, amount, notes));
    }

    // ACTIVE CLAIMS (PENDING + UNDER_INVESTIGATION)
    @PreAuthorize("hasAnyRole('CLAIMS_OFFICER','ADMIN')")
    @GetMapping("/active")
    public ResponseEntity<List<Claim>> getActiveClaims() {
        return ResponseEntity.ok(claimService.getActiveClaims());
    }

    // CLAIMS OFFICER DASHBOARD — pending
    @PreAuthorize("hasAnyRole('CLAIMS_OFFICER','ADMIN')")
    @GetMapping("/pending")
    public ResponseEntity<List<Claim>> getPendingClaims() {
        return ResponseEntity.ok(claimService.getPendingClaims());
    }

    // ASSIGNED TO ME
    @PreAuthorize("hasRole('CLAIMS_OFFICER')")
    @GetMapping("/assigned-to-me")
    public ResponseEntity<List<Claim>> getAssignedClaims(java.security.Principal principal) {
        return ResponseEntity.ok(claimService.getAssignedClaims(principal.getName()));
    }

    // ALL CLAIMS
    @PreAuthorize("hasAnyRole('ADMIN','CLAIMS_OFFICER')")
    @GetMapping
    public ResponseEntity<List<Claim>> getAllClaims() {
        return ResponseEntity.ok(claimService.getAllClaims());
    }
}