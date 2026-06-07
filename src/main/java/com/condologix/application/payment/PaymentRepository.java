package com.condologix.application.payment;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<PaymentModel, Long> {

    boolean existsByBuildingIdAndBillingPeriod(Long buildingId, String billingPeriod);
    List<PaymentModel> findByBuildingId(Long buildingId);
    List<PaymentModel> findByDueDate(LocalDate dueDate);
    List<PaymentModel> findByDueDateBeforeAndStatus(LocalDate dueDate, PaymentStatus status);
    List<PaymentModel> findByStatus(PaymentStatus status);
    List<PaymentModel> findByBuildingIdAndStatus(Long buildingId, PaymentStatus status);
}
