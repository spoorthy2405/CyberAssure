package com.cyberassure.cyberassureproject.controller;

import com.cyberassure.cyberassureproject.dto.*;
import com.cyberassure.cyberassureproject.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final ObjectMapper objectMapper;

    @GetMapping("/recommended-policies")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<PolicyResponse>> getRecommendedPolicies(Principal principal) {
        List<PolicyResponse> recommendedPolicies = customerService.getRecommendedPolicies(principal.getName());
        return ResponseEntity.ok(recommendedPolicies);
    }

    @PostMapping(value = "/apply", consumes = { "multipart/form-data" })
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<String> applyForPolicy(
            @RequestPart("data") String dataJson,
            @RequestPart(value = "proofFiles", required = false) List<MultipartFile> proofFiles,
            Principal principal) throws Exception {
        ApplyPolicyRequest request = objectMapper.readValue(dataJson, ApplyPolicyRequest.class);
        customerService.applyForPolicy(request, principal.getName(), proofFiles);
        return ResponseEntity.ok("Policy application submitted successfully and is pending review.");
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CustomerDashboardResponse> getDashboardStats(
            Principal principal) {
        return ResponseEntity.ok(customerService.getDashboardStats(principal.getName()));
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CustomerDashboardResponse> getProfile(Principal principal) {
        return ResponseEntity.ok(customerService.getDashboardStats(principal.getName()));
    }
}
