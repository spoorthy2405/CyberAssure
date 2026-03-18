package com.cyberassure.cyberassureproject.repository;

import com.cyberassure.cyberassureproject.entity.CyberPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CyberPolicyRepository extends JpaRepository<CyberPolicy, Long> {

    List<CyberPolicy> findBySectorIgnoreCaseOrSectorIgnoreCase(String sector1, String sector2);
}