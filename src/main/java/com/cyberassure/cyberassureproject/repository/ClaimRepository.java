package com.cyberassure.cyberassureproject.repository;

import com.cyberassure.cyberassureproject.entity.Claim;
import com.cyberassure.cyberassureproject.entity.ClaimStatus;
import com.cyberassure.cyberassureproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface ClaimRepository extends JpaRepository<Claim, Long> {

    List<Claim> findByStatus(ClaimStatus status);

    List<Claim> findByStatusIn(java.util.Collection<ClaimStatus> statuses);

    List<Claim> findByAssignedOfficerOrderByFiledAtDesc(User officer);

    List<Claim> findByCustomerOrderByFiledAtDesc(User customer);

    long countByCustomerAndStatus(User customer, ClaimStatus status);

    @Query("SELECT COALESCE(SUM(c.claimAmount), 0) FROM Claim c WHERE c.customer = :customer AND c.status = :status AND c.filedAt >= :from")
    BigDecimal sumClaimAmountByCustomerAndStatusSince(@Param("customer") User customer,
            @Param("status") ClaimStatus status,
            @Param("from") LocalDateTime from);

    @Query("SELECT c FROM Claim c WHERE c.incident.subscription = :subscription AND c.status = :status")
    List<Claim> findByIncidentSubscriptionAndStatus(@Param("subscription") com.cyberassure.cyberassureproject.entity.PolicySubscription subscription, @Param("status") ClaimStatus status);
}