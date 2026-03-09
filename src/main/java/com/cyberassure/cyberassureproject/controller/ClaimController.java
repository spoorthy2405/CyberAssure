package com.cyberassure.cyberassureproject.controller;

import com.cyberassure.cyberassureproject.dto.*;
import com.cyberassure.cyberassureproject.entity.Claim;
import com.cyberassure.cyberassureproject.service.ClaimService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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

        return ResponseEntity.ok(
                claimService.fileClaim(request));
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

        return ResponseEntity.ok(
                claimService.reviewClaim(id, request));
    }

    // CLAIMS OFFICER DASHBOARD
    @PreAuthorize("hasAnyRole('CLAIMS_OFFICER','ADMIN')")
    @GetMapping("/pending")
    public ResponseEntity<List<Claim>> getPendingClaims() {
        return ResponseEntity.ok(
                claimService.getPendingClaims());
    }

    @PreAuthorize("hasAnyRole('ADMIN','CLAIMS_OFFICER')")
    @GetMapping
    public ResponseEntity<List<Claim>> getAllClaims() {
        return ResponseEntity.ok(claimService.getAllClaims());
    }
}