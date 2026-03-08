package com.cyberassure.cyberassureproject.repository;

import com.cyberassure.cyberassureproject.entity.IncidentReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncidentReportRepository
        extends JpaRepository<IncidentReport, Long> {
}