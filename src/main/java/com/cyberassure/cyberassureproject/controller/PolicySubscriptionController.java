package com.cyberassure.cyberassureproject.controller;

import com.cyberassure.cyberassureproject.dto.CreateSubscriptionRequest;
import com.cyberassure.cyberassureproject.dto.UnderwriterDecisionRequest;
import com.cyberassure.cyberassureproject.entity.PolicySubscription;
import com.cyberassure.cyberassureproject.service.PolicySubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
public class PolicySubscriptionController {

    private final PolicySubscriptionService service;

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping
    public ResponseEntity<PolicySubscription> subscribe(
            @RequestBody CreateSubscriptionRequest request) {

        return ResponseEntity.ok(service.subscribe(request));
    }

    @PreAuthorize("hasRole('UNDERWRITER')")
    @PutMapping("/{id}/review")
    public ResponseEntity<PolicySubscription> review(
            @PathVariable Long id,
            @RequestBody UnderwriterDecisionRequest request) {

        return ResponseEntity.ok(
                service.reviewSubscription(id, request));
    }

    @PreAuthorize("hasRole('UNDERWRITER')")
    @GetMapping
    public ResponseEntity<List<PolicySubscription>> getAllSubscriptions() {
        return ResponseEntity.ok(service.getAllSubscriptions());
    }
}