package com.cyberassure.cyberassureproject.service;

import com.cyberassure.cyberassureproject.dto.CreateIncidentRequest;
import com.cyberassure.cyberassureproject.entity.*;
import com.cyberassure.cyberassureproject.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class IncidentReportService {

    private final IncidentReportRepository incidentRepository;
    private final PolicySubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    public IncidentReport reportIncident(CreateIncidentRequest request) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User customer = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        PolicySubscription subscription = subscriptionRepository
                .findById(request.getSubscriptionId())
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        if (subscription.getStatus() != SubscriptionStatus.APPROVED) {
            throw new RuntimeException("Policy is not active");
        }

        IncidentReport incident = IncidentReport.builder()
                .incidentType(request.getIncidentType())
                .description(request.getDescription())
                .estimatedLossAmount(request.getEstimatedLossAmount())
                .status(IncidentStatus.REPORTED)
                .reportedAt(LocalDateTime.now())
                .customer(customer)
                .subscription(subscription)
                .build();

        return incidentRepository.save(incident);
    }
}