package com.cyberassure.cyberassureproject.service;

import com.cyberassure.cyberassureproject.dto.CreateIncidentRequest;
import com.cyberassure.cyberassureproject.entity.*;
import com.cyberassure.cyberassureproject.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
                                .orElseThrow(() -> new RuntimeException("User not found"));

                PolicySubscription subscription = subscriptionRepository
                                .findById(request.getSubscriptionId())
                                .orElseThrow(() -> new RuntimeException("Subscription not found"));

                if (subscription.getStatus() != SubscriptionStatus.APPROVED) {
                        throw new RuntimeException("Policy is not active");
                }

                List<String> savedPaths = new ArrayList<>();
                if (files != null && !files.isEmpty()) {
                        String uploadDir = "uploads/incidents/" + customer.getUserId() + "/";
                        File directory = new File(uploadDir);
                        if (!directory.exists())
                                directory.mkdirs();

                        for (MultipartFile file : files) {
                                if (!file.isEmpty()) {
                                        try {
                                                String originalFilename = file.getOriginalFilename();
                                                String extension = "";
                                                if (originalFilename != null && originalFilename.contains(".")) {
                                                        extension = originalFilename
                                                                        .substring(originalFilename.lastIndexOf("."));
                                                }
                                                String fileName = UUID.randomUUID().toString() + extension;
                                                Path filePath = Paths.get(uploadDir + fileName);
                                                Files.write(filePath, file.getBytes());
                                                savedPaths.add(filePath.toString());
                                        } catch (IOException e) {
                                                throw new RuntimeException("Failed to store file", e);
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
                                .orElseThrow(() -> new RuntimeException("User not found"));
                return incidentRepository.findByCustomerOrderByReportedAtDesc(customer);
        }

        // =====================================================
        // CLAIMS OFFICER / ADMIN VIEWS ALL INCIDENTS
        // =====================================================
        public List<IncidentReport> getAllIncidents() {
                return incidentRepository.findAll();
        }
}