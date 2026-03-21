package com.condologix.application.order;

import java.util.List;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.condologix.application.resident.ResidentModel;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<OrderModel> getOrders() {
        return orderService.getOrdersForCurrentBuilding();
    }

    @GetMapping("/pending")
    public List<OrderModel> getPendingOrders() {
        return orderService.getPendingOrders();
    }

    @GetMapping("/resident/{residentId}")
    public List<OrderModel> getOrdersByResident(@PathVariable Long residentId) {
        return orderService.getOrdersByResident(residentId);
    }

    @PostMapping
    public OrderModel createOrder(@RequestBody OrderModel order) {
        return orderService.createOrder(
            order.getBuilding(),
            order.getUnit(),
            order.getResident(),
            order.getConcierge(),
            order.getSenderName(),
            order.getCarrier(),
            order.getTrackingCode(),
            order.getDescription(),
            order.getRegistrationType()
        );
    }

    @PutMapping("/{orderId}/assign-resident/{residentId}")
    public OrderModel assignResident(@PathVariable Long orderId, @PathVariable Long residentId) {
        return orderService.assignResident(orderId, residentId);
    }

    @PutMapping("/{orderId}/pickedup")
    public OrderModel markAsPickedUp(@PathVariable Long orderId) {
        return orderService.markAsPickedUp(orderId);
    }

}
