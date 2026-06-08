package com.condologix.application.payment;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentBillingPolicyRepository extends JpaRepository<PaymentBillingPolicy, Long> {

    Optional<PaymentBillingPolicy> findTopByBuildingIdAndValidFromLessThanEqualOrderByValidFromDesc(
        Long buildingId,
        LocalDate validFrom
    );
}
