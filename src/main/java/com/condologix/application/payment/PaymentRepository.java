package com.condologix.application.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentModel, Long> {

    List<PaymentModel> findByBuilding (Long buildingId);
    List<PaymentModel> findByStatus (PaymentStatus status);
    List<PaymentModel> findByBuildingAndStatus (Long buildingId, PaymentStatus status);
}
