package com.condologix.application.order;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderModel, Long>{

    List<OrderModel> findByBuildingId(Long buildingId);
    List<OrderModel> findByBuildingIdAndStatus(Long buildingId, OrderStatus status);
    List<OrderModel> findByResidentId(Long residentId);
    List<OrderModel> findByPendingResidentIdAndStatus(Long residentId, OrderStatus status);
    List<OrderModel> findByBuildingIdAndResidentIsNull(Long buildingId);
    Optional<OrderModel> findByTrackingCode(String trackingCode);

}
