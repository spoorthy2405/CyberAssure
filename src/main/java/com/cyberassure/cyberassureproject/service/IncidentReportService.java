package com.cyberassure.cyberassureproject.service;

import com.cyberassure.cyberassureproject.dto.CreateIncidentRequest;
import com.cyberassure.cyberassureproject.entity.*;
import com.cyberassure.cyberassureproject.exception.PolicyNotActiveException;
import com.cyberassure.cyberassureproject.exception.ResourceNotFoundException;
import com.cyberassure.cyberassureproject.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IncidentReportService {

        private final IncidentReportRepository incidentRepository;
        private final PolicySubscriptionRepository subscriptionRepository;
        private final UserRepository userRepository;

        // =====================================================
        // CUSTOMER REPORTS INCIDENT
        // =====================================================
        public IncidentReport reportIncident(CreateIncidentRequest request, List<MultipartFile> files) {
                String email = SecurityContextHolder.getContext().getAuthentication().getName();

                User customer = userRepository.findByEmail(email)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Authenticated user not found. Please log in again."));

                PolicySubscription subscription = subscriptionRepository
                                .findById(request.getSubscriptionId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "No subscription found with ID: " + request.getSubscriptionId()
                                                + ". Please ensure your policy is active before reporting an incident."));

                // Only ACTIVE subscriptions can raise incident reports
                if (subscription.getStatus() != SubscriptionStatus.APPROVED
                                && subscription.getStatus() != SubscriptionStatus.ACTIVE) {
                        throw new PolicyNotActiveException(
                                "Your policy subscription is currently '" + subscription.getStatus()
                                + "'. Incident reports can only be filed under an ACTIVE policy.");
                }

                List<String> savedPaths = new ArrayList<>();
                if (files != null && !files.isEmpty()) {
                        String projectRoot = System.getProperty("user.dir");
                        String uploadDir = projectRoot + "/uploads/incidents/" + customer.getUserId();
                        java.nio.file.Path dir = java.nio.file.Paths.get(uploadDir).toAbsolutePath().normalize();
                        if (!java.nio.file.Files.exists(dir)) {
                            try {
                                java.nio.file.Files.createDirectories(dir);
                            } catch (IOException e) {
                                throw new ResourceNotFoundException(
                                        "Failed to create upload directory for incident documents. Please try again.");
                            }
                        }

                        for (MultipartFile file : files) {
                                if (!file.isEmpty()) {
                                        try {
                                                String originalFilename = file.getOriginalFilename();
                                                String extension = "";
                                                if (originalFilename != null && originalFilename.contains(".")) {
                                                        extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                                                }
                                                String fileName = UUID.randomUUID().toString() + extension;
                                                java.nio.file.Path target = dir.resolve(fileName);
                                                java.nio.file.Files.copy(file.getInputStream(), target,
                                                                java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                                                savedPaths.add("uploads/incidents/" + customer.getUserId() + "/" + fileName);
                                        } catch (IOException e) {
                                                throw new ResourceNotFoundException(
                                                        "Failed to store document '" + file.getOriginalFilename()
                                                        + "'. Please check file format and try again.");
                                        }
                                }
                        }
                }

                IncidentReport incident = IncidentReport.builder()
                                .incidentType(request.getIncidentType())
                                .description(request.getDescription())
                                .estimatedLossAmount(request.getEstimatedLossAmount())
                                .status(IncidentStatus.REPORTED)
                                .reportedAt(LocalDateTime.now())
                                .customer(customer)
                                .subscription(subscription)
                                .documentPaths(savedPaths)
                                .build();

                return incidentRepository.save(incident);
        }

        // =====================================================
        // CUSTOMER VIEWS THEIR OWN INCIDENTS
        // =====================================================
        public List<IncidentReport> getMyIncidents(String email) {
                User customer = userRepository.findByEmail(email)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "User not found with email: " + email));
                return incidentRepository.findByCustomerOrderByReportedAtDesc(customer);
        }

        // =====================================================
        // CLAIMS OFFICER / ADMIN VIEWS ALL INCIDENTS
        // =====================================================
        public List<IncidentReport> getAllIncidents() {
                return incidentRepository.findAll();
        }
}