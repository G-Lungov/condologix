package com.condologix.application.order;

import com.condologix.application.exception.GlobalExceptionHandler;
import com.condologix.application.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Test
    void createOrderShouldReturnCreated() throws Exception {
        OrderDTO response = createOrderResponse(OrderStatus.RECEIVED);

        when(orderService.createOrder(any(OrderCreateDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "buildingId": 1,
                        "unitId": 5,
                        "residentId": null,
                        "conciergeId": 3,
                        "senderName": "Store",
                        "carrier": "Carrier",
                        "trackingCode": "TRACK123",
                        "description": "Small package",
                        "registrationType": "MANUAL"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(10))
            .andExpect(jsonPath("$.buildingId").value(1))
            .andExpect(jsonPath("$.unitId").value(5))
            .andExpect(jsonPath("$.status").value("RECEIVED"));
    }

    @Test
    void createOrderShouldReturnBadRequestWhenBodyIsInvalid() throws Exception {
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "buildingId": -1,
                        "unitId": 0,
                        "conciergeId": null,
                        "senderName": "",
                        "carrier": "",
                        "trackingCode": "",
                        "registrationType": null
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Validation failed"))
            .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    void createOrderShouldReturnNotFoundWhenServiceThrowsNotFound() throws Exception {
        when(orderService.createOrder(any(OrderCreateDTO.class)))
            .thenThrow(new ResourceNotFoundException("Unit not found with id: 99"));

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "buildingId": 1,
                        "unitId": 99,
                        "residentId": null,
                        "conciergeId": 3,
                        "senderName": "Store",
                        "carrier": "Carrier",
                        "trackingCode": "TRACK123",
                        "description": "Small package",
                        "registrationType": "MANUAL"
                    }
                    """))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Unit not found with id: 99"));
    }

    @Test
    void getOrdersShouldReturnOk() throws Exception {
        when(orderService.getOrdersForBuilding(1L)).thenReturn(List.of(createOrderResponse(OrderStatus.RECEIVED)));

        mockMvc.perform(get("/api/orders/building/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(10));
    }

    @Test
    void getPendingOrdersShouldReturnOk() throws Exception {
        when(orderService.getPendingOrdersForBuilding(1L)).thenReturn(List.of(createOrderResponse(OrderStatus.NOTIFIED)));

        mockMvc.perform(get("/api/orders/building/1/pending"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].status").value("NOTIFIED"));
    }

    @Test
    void getOrdersByResidentShouldReturnOk() throws Exception {
        when(orderService.getOrdersByBuildingAndResident(1L, 8L)).thenReturn(List.of(createOrderResponse(OrderStatus.NOTIFIED)));

        mockMvc.perform(get("/api/orders/building/1/resident/8"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].residentId").value(8));
    }

    @Test
    void assignResidentShouldReturnOk() throws Exception {
        OrderDTO response = createOrderResponse(OrderStatus.NOTIFIED);

        when(orderService.assignResident(anyLong(), anyLong())).thenReturn(response);

        mockMvc.perform(put("/api/orders/10/assign-resident/8"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(10))
            .andExpect(jsonPath("$.status").value("NOTIFIED"));
    }

    @Test
    void assignResidentShouldReturnNotFoundWhenServiceThrowsNotFound() throws Exception {
        when(orderService.assignResident(anyLong(), anyLong()))
            .thenThrow(new ResourceNotFoundException("Order not found with id: 10"));

        mockMvc.perform(put("/api/orders/10/assign-resident/8"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Order not found with id: 10"));
    }

    @Test
    void markAsPickedUpShouldReturnOk() throws Exception {
        OrderDTO response = createOrderResponse(OrderStatus.PICKED_UP);

        when(orderService.markAsPickedUp(10L)).thenReturn(response);

        mockMvc.perform(put("/api/orders/10/pickedup"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("PICKED_UP"));
    }

    @Test
    void markAsPickedUpShouldReturnNotFoundWhenServiceThrowsNotFound() throws Exception {
        when(orderService.markAsPickedUp(10L))
            .thenThrow(new ResourceNotFoundException("Order not found with id: 10"));

        mockMvc.perform(put("/api/orders/10/pickedup"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Order not found with id: 10"));
    }

    private OrderDTO createOrderResponse(OrderStatus status) {
        return new OrderDTO(
            10L,
            1L,
            5L,
            8L,
            3L,
            "Store",
            "Carrier",
            "TRACK123",
            "Small package",
            LocalDateTime.of(2026, 5, 28, 10, 0),
            status == OrderStatus.NOTIFIED || status == OrderStatus.PICKED_UP
                ? LocalDateTime.of(2026, 5, 28, 10, 5)
                : null,
            status == OrderStatus.PICKED_UP ? LocalDateTime.of(2026, 5, 28, 11, 0) : null,
            OrderRegistrationType.MANUAL,
            status
        );
    }
}
