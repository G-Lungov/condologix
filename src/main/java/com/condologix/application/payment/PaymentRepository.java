package com.condologix.application.payment;


import java.util.List;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<PaymentModel, Long> {

    List<PaymentModel> findByBuildingId (Long buildingId);
    List<PaymentModel> findByDueDate (LocalDate dueDate);
    List<PaymentModel> findByDueDateLessThanEqual (LocalDate dueDate);
    List<PaymentModel> findByStatus (PaymentStatus status);
    List<PaymentModel> findByDueDateLessThanEqualAndStatus (LocalDate dueDate, PaymentStatus status);
    List<PaymentModel> findByBuildingIdAndStatus (Long buildingId, PaymentStatus status);
}
