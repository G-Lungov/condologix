package com.condologix.application.order;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.condologix.application.building.BuildingRepository;
import com.condologix.application.building.BuildingModel;
import com.condologix.application.concierge.ConciergeModel;
import com.condologix.application.concierge.ConciergeRepository;
import com.condologix.application.exception.ResourceNotFoundException;
import com.condologix.application.resident.ResidentModel;
import com.condologix.application.resident.ResidentRepository;
import com.condologix.application.unit.UnitModel;
import com.condologix.application.unit.UnitRepository;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final BuildingRepository buildingRepository;
    private final UnitRepository unitRepository;
    private final ConciergeRepository conciergeRepository;
    private final ResidentRepository residentRepository;

    public OrderService(
        OrderRepository orderRepository,
        BuildingRepository buildingRepository,
        UnitRepository unitRepository,
        ConciergeRepository conciergeRepository,
        ResidentRepository residentRepository
    ) {
        this.orderRepository = orderRepository;
        this.buildingRepository = buildingRepository;
        this.unitRepository = unitRepository;
        this.conciergeRepository = conciergeRepository;
        this.residentRepository = residentRepository;
    }

    public OrderDTO createOrder(OrderCreateDTO orderDTO) {
        BuildingModel building = buildingRepository.findById(orderDTO.buildingId())
            .orElseThrow(() -> new ResourceNotFoundException("Building not found with id: " + orderDTO.buildingId()));
        UnitModel unit = unitRepository.findById(orderDTO.unitId())
            .orElseThrow(() -> new ResourceNotFoundException("Unit not found with id: " + orderDTO.unitId()));
        ConciergeModel concierge = conciergeRepository.findById(orderDTO.conciergeId())
            .orElseThrow(() -> new ResourceNotFoundException("Concierge not found with id: " + orderDTO.conciergeId()));
        ResidentModel resident = null;
        if (orderDTO.residentId() != null) {
            resident = residentRepository.findById(orderDTO.residentId())
                .orElseThrow(() -> new ResourceNotFoundException("Resident not found with id: " + orderDTO.residentId()));
        }

        OrderModel order = new OrderModel(
            building,
            unit,
            resident,
            concierge,
            orderDTO.senderName(),
            orderDTO.carrier(),
            orderDTO.trackingCode(),
            orderDTO.description(),
            orderDTO.registrationType()
        );

        OrderModel savedOrder = orderRepository.save(order);
        if (resident != null) {
            notifyResident(savedOrder);
            savedOrder.markAsNotified();
            savedOrder = orderRepository.save(savedOrder);
        }
        return toDTO(savedOrder);
    }

    public OrderDTO assignResident(Long orderId, Long residentId) {
        OrderModel order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        ResidentModel resident = residentRepository.findById(residentId)
            .orElseThrow(() -> new ResourceNotFoundException("Resident not found with id: " + residentId));

        order.assignResident(resident);
        notifyResident(order);
        if (order.getStatus() == OrderStatus.RECEIVED) {
            order.markAsNotified();
        }

        OrderModel savedOrder = orderRepository.save(order);
        return toDTO(savedOrder);
    }

    public OrderDTO markAsPickedUp(Long orderId) {
        OrderModel order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        order.markAsPickedUp();
        OrderModel savedOrder = orderRepository.save(order);
        return toDTO(savedOrder);
    }

    public void notifyResident(OrderModel order) {
            // Integrate with notification.
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersForBuilding(Long buildingId) {
        return orderRepository.findByBuildingId(buildingId)
            .stream()
            .map(this::toDTO)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> getPendingOrdersForBuilding(Long buildingId) {
        return orderRepository.findByBuildingIdAndStatus(buildingId, OrderStatus.NOTIFIED)
            .stream()
            .map(this::toDTO)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByBuildingAndResident(Long buildingId, Long residentId) {
        return orderRepository.findByBuildingIdAndResidentId(buildingId, residentId)
            .stream()
            .map(this::toDTO)
            .toList();
    }

    private OrderDTO toDTO(OrderModel order) {
        return new OrderDTO(
            order.getId(),
            order.getBuilding().getId(),
            order.getUnit().getId(),
            order.getResident() != null ? order.getResident().getId() : null,
            order.getConcierge().getId(),
            order.getSenderName(),
            order.getCarrier(),
            order.getTrackingCode(),
            order.getDescription(),
            order.getArrivedAt(),
            order.getNotifiedAt(),
            order.getPickedUpAt(),
            order.getRegistrationType(),
            order.getStatus()
        );
    }
}
