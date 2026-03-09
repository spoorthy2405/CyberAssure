package com.cyberassure.cyberassureproject.controller;

import com.cyberassure.cyberassureproject.dto.CyberPolicyRequest;
import com.cyberassure.cyberassureproject.dto.PolicyResponse;
import com.cyberassure.cyberassureproject.service.CyberPolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/policies")
@RequiredArgsConstructor
public class CyberPolicyController {

    private final CyberPolicyService service;

    // Admin creates a policy
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PolicyResponse> createPolicy(@RequestBody CyberPolicyRequest request) {
        return ResponseEntity.ok(service.createPolicy(request));
    }

    // Admin updates a policy
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PolicyResponse> updatePolicy(@PathVariable Long id,
            @RequestBody CyberPolicyRequest request) {
        return ResponseEntity.ok(service.updatePolicy(id, request));
    }

    // Admin deletes a policy
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePolicy(@PathVariable Long id) {
        service.deletePolicy(id);
        return ResponseEntity.noContent().build();
    }

    // All authenticated users can browse policies
    @GetMapping
    public ResponseEntity<List<PolicyResponse>> getAllPolicies() {
        return ResponseEntity.ok(service.getAllPolicies());
    }
}