package com.cyberassure.cyberassureproject.repository;

import com.cyberassure.cyberassureproject.entity.RiskAssessment;
import com.cyberassure.cyberassureproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RiskAssessmentRepository extends JpaRepository<RiskAssessment, Long> {
    Optional<RiskAssessment>
    findTopByCustomerOrderByCreatedAtDesc(User customer);
}