package com.cyberassure.cyberassureproject.repository;

import com.cyberassure.cyberassureproject.entity.Claim;
import com.cyberassure.cyberassureproject.entity.ClaimStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClaimRepository extends JpaRepository<Claim, Long> {

    List<Claim> findByStatus(ClaimStatus status);
}