package com.condologix.application.order;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@Validated
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/building/{buildingId}")
    public ResponseEntity<List<OrderDTO>> getOrders(@PathVariable @Positive Long buildingId) {
        List<OrderDTO> orders = orderService.getOrdersForBuilding(buildingId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/building/{buildingId}/pending")
    public ResponseEntity<List<OrderDTO>> getPendingOrders(@PathVariable @Positive Long buildingId) {
        List<OrderDTO> orders = orderService.getPendingOrdersForBuilding(buildingId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/building/{buildingId}/resident/{residentId}")
    public ResponseEntity<List<OrderDTO>> getOrdersByResident(
        @PathVariable @Positive Long buildingId,
        @PathVariable @Positive Long residentId
    ) {
        List<OrderDTO> orders = orderService.getOrdersByBuildingAndResident(buildingId, residentId);
        return ResponseEntity.ok(orders);
    }

    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody OrderCreateDTO orderDTO) {
        OrderDTO createdOrder = orderService.createOrder(orderDTO);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    @PutMapping("/{orderId}/assign-resident/{residentId}")
    public ResponseEntity<OrderDTO> assignResident(
        @PathVariable @Positive Long orderId,
        @PathVariable @Positive Long residentId
    ) {
        OrderDTO updatedOrder = orderService.assignResident(orderId, residentId);
        return ResponseEntity.ok(updatedOrder);
    }

    @PutMapping("/{orderId}/pickedup")
    public ResponseEntity<OrderDTO> markAsPickedUp(@PathVariable @Positive Long orderId) {
        OrderDTO updatedOrder = orderService.markAsPickedUp(orderId);
        return ResponseEntity.ok(updatedOrder);
    }

}
