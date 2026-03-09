package com.cyberassure.cyberassureproject.repository;

import com.cyberassure.cyberassureproject.entity.PolicySubscription;
import com.cyberassure.cyberassureproject.entity.User;
import com.cyberassure.cyberassureproject.entity.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PolicySubscriptionRepository extends JpaRepository<PolicySubscription, Long> {

    List<PolicySubscription> findByCustomerAndStatus(User customer, SubscriptionStatus status);

    List<PolicySubscription> findByCustomer(User customer);

    List<PolicySubscription> findByAssignedUnderwriter(User underwriter);
}