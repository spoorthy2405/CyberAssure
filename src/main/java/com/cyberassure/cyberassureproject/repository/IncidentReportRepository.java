package com.cyberassure.cyberassureproject.repository;

import com.cyberassure.cyberassureproject.entity.IncidentReport;
import com.cyberassure.cyberassureproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidentReportRepository
                extends JpaRepository<IncidentReport, Long> {

        List<IncidentReport> findByCustomerOrderByReportedAtDesc(User customer);
}