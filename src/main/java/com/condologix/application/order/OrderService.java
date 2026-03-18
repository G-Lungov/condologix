package com.condologix.application.order;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.condologix.application.building.BuildingModel;
import com.condologix.application.concierge.ConciergeModel;
import com.condologix.application.resident.ResidentModel;
import com.condologix.application.unit.UnitModel;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    public OrderService (OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public OrderModel createOrder(
        BuildingModel building,
        UnitModel unit,
        ResidentModel resident,
        ConciergeModel concierge,
        String senderName,
        String carrier,
        String trackingCode,
        String description,
        OrderRegistrationType registrationType
    )  {
        OrderModel order = new OrderModel(
            building,
            unit,
            resident,
            concierge,
            senderName,
            carrier,
            trackingCode,
            description,
            registrationType
        );

        OrderModel savedOrder = orderRepository.save(order);
        if (resident != null) {
            notifyResident(savedOrder);
            savedOrder.markAsNotified();
        }
        return savedOrder;
    }

    public OrderModel assignResident(Long orderId, ResidentModel resident) {
        OrderModel order = orderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + orderId));
        order.assignResident(resident);
        notifyResident(order);
        order.markAsNotified();
        return orderRepository.save(order);
    }

    public OrderModel markAsPickedUp(Long orderId) {
        OrderModel order = orderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + orderId));
        order.markAsPickedUp();
        return orderRepository.save(order);
    }

    public void notifyResident(OrderModel order) {
            // Integrate with notification.
    }

    // Temporary method to get current building ID. In a real application, this would be retrieved from JWT or session context.
    private Long getCurrentBuildingId() {
        return 1L;
    }
    // Temporary method to get current building ID. In a real application, this would be retrieved from JWT or session context.

    public List<OrderModel> getOrdersForCurrentBuilding() {
        Long buildingId = getCurrentBuildingId();
        return orderRepository.findByBuildingId(buildingId);
    }

    public List<OrderModel> getPendingOrders(Long buildingId) {
        return orderRepository.findByBuildingIdAndStatus(buildingId, OrderStatus.NOTIFIED);
    }

    public List<OrderModel> getOrdersByResident(Long residentId) {
        return orderRepository.findByResidentId(residentId);
    }
}
