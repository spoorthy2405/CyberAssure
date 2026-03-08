package com.cyberassure.cyberassureproject.controller;

import com.cyberassure.cyberassureproject.dto.CreatePolicyRequest;
import com.cyberassure.cyberassureproject.dto.PolicyResponse;
import com.cyberassure.cyberassureproject.service.CyberPolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/policies")
@RequiredArgsConstructor
public class CyberPolicyController {

    private final CyberPolicyService service;

    @PostMapping
    public ResponseEntity<PolicyResponse> createPolicy(
            @RequestBody CreatePolicyRequest request) {

        return ResponseEntity.ok(service.createPolicy(request));
    }

    @GetMapping
    public ResponseEntity<List<PolicyResponse>> getAllPolicies() {

        return ResponseEntity.ok(service.getAllPolicies());
    }
}