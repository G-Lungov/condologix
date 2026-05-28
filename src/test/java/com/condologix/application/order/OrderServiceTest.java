package com.condologix.application.order;

import com.condologix.application.building.BuildingModel;
import com.condologix.application.building.BuildingRepository;
import com.condologix.application.concierge.ConciergeModel;
import com.condologix.application.concierge.ConciergeRepository;
import com.condologix.application.exception.ResourceNotFoundException;
import com.condologix.application.resident.ResidentModel;
import com.condologix.application.resident.ResidentRepository;
import com.condologix.application.unit.UnitModel;
import com.condologix.application.unit.UnitRepository;
import com.condologix.application.unit.UnitType;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrderServiceTest {

    private final OrderRepository orderRepository = mock(OrderRepository.class);
    private final BuildingRepository buildingRepository = mock(BuildingRepository.class);
    private final UnitRepository unitRepository = mock(UnitRepository.class);
    private final ConciergeRepository conciergeRepository = mock(ConciergeRepository.class);
    private final ResidentRepository residentRepository = mock(ResidentRepository.class);
    private final OrderService orderService = new OrderService(
        orderRepository,
        buildingRepository,
        unitRepository,
        conciergeRepository,
        residentRepository
    );

    @Test
    void createOrderShouldReturnCreatedWithoutResident() {
        BuildingModel building = createBuilding(1L);
        UnitModel unit = createUnit(5L, building);
        ConciergeModel concierge = createConcierge(3L, building);
        OrderCreateDTO request = createOrderRequest(null);

        when(buildingRepository.findById(1L)).thenReturn(Optional.of(building));
        when(unitRepository.findById(5L)).thenReturn(Optional.of(unit));
        when(conciergeRepository.findById(3L)).thenReturn(Optional.of(concierge));
        when(orderRepository.save(any(OrderModel.class))).thenAnswer(invocation -> {
            OrderModel order = invocation.getArgument(0);
            ReflectionTestUtils.setField(order, "id", 10L);
            return order;
        });

        OrderDTO createdOrder = orderService.createOrder(request);

        assertEquals(10L, createdOrder.id());
        assertEquals(1L, createdOrder.buildingId());
        assertEquals(5L, createdOrder.unitId());
        assertNull(createdOrder.residentId());
        assertEquals(OrderStatus.RECEIVED, createdOrder.status());
        verify(orderRepository).save(any(OrderModel.class));
    }

    @Test
    void createOrderShouldNotifyWhenResidentIsProvided() {
        BuildingModel building = createBuilding(1L);
        UnitModel unit = createUnit(5L, building);
        ConciergeModel concierge = createConcierge(3L, building);
        ResidentModel resident = createResident(8L, unit);
        OrderCreateDTO request = createOrderRequest(8L);

        when(buildingRepository.findById(1L)).thenReturn(Optional.of(building));
        when(unitRepository.findById(5L)).thenReturn(Optional.of(unit));
        when(conciergeRepository.findById(3L)).thenReturn(Optional.of(concierge));
        when(residentRepository.findById(8L)).thenReturn(Optional.of(resident));
        when(orderRepository.save(any(OrderModel.class))).thenAnswer(invocation -> {
            OrderModel order = invocation.getArgument(0);
            ReflectionTestUtils.setField(order, "id", 10L);
            return order;
        });

        OrderDTO createdOrder = orderService.createOrder(request);

        assertEquals(8L, createdOrder.residentId());
        assertEquals(OrderStatus.NOTIFIED, createdOrder.status());
        verify(orderRepository, times(2)).save(any(OrderModel.class));
    }

    @Test
    void createOrderShouldThrowNotFoundWhenBuildingDoesNotExist() {
        OrderCreateDTO request = createOrderRequest(null);

        when(buildingRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> orderService.createOrder(request)
        );

        assertEquals("Building not found with id: 1", exception.getMessage());
        verify(orderRepository, never()).save(any(OrderModel.class));
    }

    @Test
    void createOrderShouldThrowNotFoundWhenResidentDoesNotExist() {
        BuildingModel building = createBuilding(1L);
        UnitModel unit = createUnit(5L, building);
        ConciergeModel concierge = createConcierge(3L, building);
        OrderCreateDTO request = createOrderRequest(8L);

        when(buildingRepository.findById(1L)).thenReturn(Optional.of(building));
        when(unitRepository.findById(5L)).thenReturn(Optional.of(unit));
        when(conciergeRepository.findById(3L)).thenReturn(Optional.of(concierge));
        when(residentRepository.findById(8L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> orderService.createOrder(request)
        );

        assertEquals("Resident not found with id: 8", exception.getMessage());
        verify(orderRepository, never()).save(any(OrderModel.class));
    }

    @Test
    void assignResidentShouldReturnNotifiedOrder() {
        BuildingModel building = createBuilding(1L);
        UnitModel unit = createUnit(5L, building);
        OrderModel order = createOrder(10L, building, unit, null);
        ResidentModel resident = createResident(8L, unit);

        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));
        when(residentRepository.findById(8L)).thenReturn(Optional.of(resident));
        when(orderRepository.save(order)).thenReturn(order);

        OrderDTO updatedOrder = orderService.assignResident(10L, 8L);

        assertEquals(8L, updatedOrder.residentId());
        assertEquals(OrderStatus.NOTIFIED, updatedOrder.status());
        verify(orderRepository).save(order);
    }

    @Test
    void assignResidentShouldRejectResidentFromAnotherUnit() {
        BuildingModel building = createBuilding(1L);
        UnitModel orderUnit = createUnit(5L, building);
        UnitModel otherUnit = createUnit(6L, building);
        OrderModel order = createOrder(10L, building, orderUnit, null);
        ResidentModel resident = createResident(8L, otherUnit);

        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));
        when(residentRepository.findById(8L)).thenReturn(Optional.of(resident));

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> orderService.assignResident(10L, 8L)
        );

        assertEquals("Resident does not belong to the unit", exception.getMessage());
        verify(orderRepository, never()).save(any(OrderModel.class));
    }

    @Test
    void markAsPickedUpShouldReturnPickedUpOrder() {
        BuildingModel building = createBuilding(1L);
        UnitModel unit = createUnit(5L, building);
        OrderModel order = createOrder(10L, building, unit, null);

        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        OrderDTO pickedUpOrder = orderService.markAsPickedUp(10L);

        assertEquals(10L, pickedUpOrder.id());
        assertEquals(OrderStatus.PICKED_UP, pickedUpOrder.status());
        verify(orderRepository).save(order);
    }

    @Test
    void getOrdersForBuildingShouldMapRepositoryResults() {
        BuildingModel building = createBuilding(1L);
        UnitModel unit = createUnit(5L, building);
        OrderModel order = createOrder(10L, building, unit, null);

        when(orderRepository.findByBuildingId(1L)).thenReturn(List.of(order));

        List<OrderDTO> orders = orderService.getOrdersForBuilding(1L);

        assertEquals(1, orders.size());
        assertEquals(10L, orders.get(0).id());
        assertEquals(1L, orders.get(0).buildingId());
    }

    private OrderCreateDTO createOrderRequest(Long residentId) {
        return new OrderCreateDTO(
            1L,
            5L,
            residentId,
            3L,
            "Store",
            "Carrier",
            "TRACK123",
            "Small package",
            OrderRegistrationType.MANUAL
        );
    }

    private OrderModel createOrder(Long id, BuildingModel building, UnitModel unit, ResidentModel resident) {
        OrderModel order = new OrderModel(
            building,
            unit,
            resident,
            createConcierge(3L, building),
            "Store",
            "Carrier",
            "TRACK123",
            "Small package",
            OrderRegistrationType.MANUAL
        );
        ReflectionTestUtils.setField(order, "id", id);
        return order;
    }

    private ResidentModel createResident(Long id, UnitModel unit) {
        ResidentModel resident = new ResidentModel(unit, "John Doe", "john@example.com", "11999999999");
        ReflectionTestUtils.setField(resident, "id", id);
        return resident;
    }

    private ConciergeModel createConcierge(Long id, BuildingModel building) {
        ConciergeModel concierge = new ConciergeModel(building, "Jane Concierge", "11988887777");
        ReflectionTestUtils.setField(concierge, "id", id);
        return concierge;
    }

    private UnitModel createUnit(Long id, BuildingModel building) {
        UnitModel unit = new UnitModel(building, (short) 101, "A", UnitType.RESIDENTIAL);
        ReflectionTestUtils.setField(unit, "id", id);
        return unit;
    }

    private BuildingModel createBuilding(Long id) {
        BuildingModel building = new BuildingModel(
            "12345678000199",
            "Condo One LTDA",
            "Condo One",
            "11999999999",
            "condo@example.com",
            "Main Street",
            123,
            "12345678",
            "Downtown",
            "Sao Paulo",
            "SP"
        );
        ReflectionTestUtils.setField(building, "id", id);
        return building;
    }
}
