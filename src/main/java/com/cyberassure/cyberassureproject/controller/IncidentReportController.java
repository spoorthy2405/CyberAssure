package com.cyberassure.cyberassureproject.controller;

import com.cyberassure.cyberassureproject.dto.CreateIncidentRequest;
import com.cyberassure.cyberassureproject.entity.IncidentReport;
import com.cyberassure.cyberassureproject.service.IncidentReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/incidents")
@RequiredArgsConstructor
public class IncidentReportController {

        private final IncidentReportService incidentService;

        // Customer reports an incident with optional document uploads
        @PostMapping(consumes = "multipart/form-data")
        @PreAuthorize("hasRole('CUSTOMER')")
        public ResponseEntity<IncidentReport> reportIncident(
                        @RequestPart("data") CreateIncidentRequest request,
                        @RequestPart(value = "files", required = false) List<MultipartFile> files) {
                return ResponseEntity.ok(incidentService.reportIncident(request, files));
        }

        // Customer views their own incidents
        @GetMapping("/my")
        @PreAuthorize("hasRole('CUSTOMER')")
        public ResponseEntity<List<IncidentReport>> getMyIncidents(Authentication auth) {
                return ResponseEntity.ok(incidentService.getMyIncidents(auth.getName()));
        }

        // Claims officer or admin views all incidents
        @GetMapping
        @PreAuthorize("hasAnyRole('CLAIMS_OFFICER', 'ADMIN')")
        public ResponseEntity<List<IncidentReport>> getAllIncidents() {
                return ResponseEntity.ok(incidentService.getAllIncidents());
        }
}