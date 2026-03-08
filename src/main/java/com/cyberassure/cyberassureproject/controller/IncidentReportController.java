package com.cyberassure.cyberassureproject.controller;

import com.cyberassure.cyberassureproject.dto.CreateIncidentRequest;
import com.cyberassure.cyberassureproject.entity.IncidentReport;
import com.cyberassure.cyberassureproject.service.IncidentReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/incidents")
@RequiredArgsConstructor
public class IncidentReportController {

    private final IncidentReportService incidentService;

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping
    public ResponseEntity<IncidentReport> reportIncident(
            @Valid @RequestBody CreateIncidentRequest request) {

        return ResponseEntity.ok(
                incidentService.reportIncident(request)
        );
    }
}