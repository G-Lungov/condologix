package com.condologix.application.payment;

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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    @Test
    void createPaymentShouldReturnCreated() throws Exception {
        PaymentDTO response = createPendingPaymentDTO(20L);

        when(paymentService.createPayment(any(PaymentCreateDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "buildingId": 1,
                        "billingPeriod": "2026-05",
                        "createdAt": "2026-05-01",
                        "dueDate": "2026-05-10"
                    }
                    """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(20))
                .andExpect(jsonPath("$.buildingId").value(1))
                .andExpect(jsonPath("$.billingPeriod").value("2026-05"))
                .andExpect(jsonPath("$.amount").value(250.00))
                .andExpect(jsonPath("$.interestRate").value(0.01))
                .andExpect(jsonPath("$.graceDays").value(2))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void createPaymentShouldReturnBadRequestWhenBodyIsInvalid() throws Exception {
        mockMvc.perform(post("/api/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "buildingId": -1,
                        "billingPeriod": "202605"
                    }
                    """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors").exists());

        verify(paymentService, never()).createPayment(any(PaymentCreateDTO.class));
    }

    @Test
    void createPaymentShouldReturnNotFoundWhenBuildingOrPolicyDoesNotExist() throws Exception {
        when(paymentService.createPayment(any(PaymentCreateDTO.class)))
            .thenThrow(new ResourceNotFoundException("Payment billing policy not found for building id: 1"));

        mockMvc.perform(post("/api/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "buildingId": 1,
                        "billingPeriod": "2026-05",
                        "createdAt": "2026-05-01",
                        "dueDate": "2026-05-10"
                    }
                    """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Payment billing policy not found for building id: 1"));
    }

    @Test
    void createPaymentShouldReturnConflictWhenPaymentAlreadyExists() throws Exception {
        when(paymentService.createPayment(any(PaymentCreateDTO.class)))
            .thenThrow(new IllegalStateException("Payment already exists for this building and billing period"));

        mockMvc.perform(post("/api/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "buildingId": 1,
                        "billingPeriod": "2026-05",
                        "createdAt": "2026-05-01",
                        "dueDate": "2026-05-10"
                    }
                    """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Payment already exists for this building and billing period"));
    }

    @Test
    void markPaymentAsPaidShouldReturnOk() throws Exception {
        PaymentDTO response = createPaidPaymentDTO(20L);

        when(paymentService.markPaymentAsPaid(anyLong(), any(PaymentPayDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/payments/20/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "paymentDate": "2026-05-13"
                    }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(20))
                .andExpect(jsonPath("$.status").value("PAID"))
                .andExpect(jsonPath("$.paidAt").value("2026-05-13"))
                .andExpect(jsonPath("$.interestAmount").value(2.50))
                .andExpect(jsonPath("$.totalAmount").value(252.50));
    }

    @Test
    void markPaymentAsPaidShouldReturnBadRequestWhenBodyIsInvalid() throws Exception {
        mockMvc.perform(put("/api/payments/20/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "paymentDate": null
                    }
                    """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors").exists());

        verify(paymentService, never()).markPaymentAsPaid(anyLong(), any(PaymentPayDTO.class));
    }

    @Test
    void markPaymentAsPaidShouldReturnBadRequestWhenIdIsNotPositive() throws Exception {
        mockMvc.perform(put("/api/payments/0/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "paymentDate": "2026-05-13"
                    }
                    """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors").exists());

        verify(paymentService, never()).markPaymentAsPaid(anyLong(), any(PaymentPayDTO.class));
    }

    @Test
    void markPaymentAsPaidShouldReturnNotFoundWhenPaymentDoesNotExist() throws Exception {
        when(paymentService.markPaymentAsPaid(anyLong(), any(PaymentPayDTO.class)))
            .thenThrow(new ResourceNotFoundException("Payment not found with id: 20"));

        mockMvc.perform(put("/api/payments/20/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "paymentDate": "2026-05-13"
                    }
                    """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Payment not found with id: 20"));
    }

    @Test
    void getPaymentsByBuildingShouldReturnOk() throws Exception {
        when(paymentService.getPaymentsByBuilding(1L)).thenReturn(List.of(createPendingPaymentDTO(20L)));

        mockMvc.perform(get("/api/payments/building/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(20))
                .andExpect(jsonPath("$[0].buildingId").value(1))
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test
    void getPaymentsByBuildingShouldReturnBadRequestWhenBuildingIdIsNotPositive() throws Exception {
        mockMvc.perform(get("/api/payments/building/0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors").exists());

        verify(paymentService, never()).getPaymentsByBuilding(anyLong());
    }

    @Test
    void getPaymentsByBuildingShouldReturnNotFoundWhenBuildingDoesNotExist() throws Exception {
        when(paymentService.getPaymentsByBuilding(99L))
            .thenThrow(new ResourceNotFoundException("Building not found with id: 99"));

        mockMvc.perform(get("/api/payments/building/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Building not found with id: 99"));
    }

    @Test
    void getPaymentsDueOnShouldReturnOk() throws Exception {
        when(paymentService.getPaymentsDueOn(LocalDate.of(2026, 5, 10)))
            .thenReturn(List.of(createPendingPaymentDTO(20L)));

        mockMvc.perform(get("/api/payments/due/2026-05-10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(20))
                .andExpect(jsonPath("$[0].dueDate").value("2026-05-10"));
    }

    @Test
    void getOverduePaymentsShouldReturnOk() throws Exception {
        when(paymentService.getOverduePayments(LocalDate.of(2026, 5, 20)))
            .thenReturn(List.of(createPendingPaymentDTO(20L)));

        mockMvc.perform(get("/api/payments/overdue?today=2026-05-20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(20))
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    private PaymentDTO createPendingPaymentDTO(Long id) {
        return new PaymentDTO(
            id,
            1L,
            "2026-05",
            new BigDecimal("250.00"),
            new BigDecimal("0.01"),
            2,
            null,
            null,
            LocalDate.of(2026, 5, 1),
            LocalDate.of(2026, 5, 10),
            null,
            PaymentStatus.PENDING
        );
    }

    private PaymentDTO createPaidPaymentDTO(Long id) {
        return new PaymentDTO(
            id,
            1L,
            "2026-05",
            new BigDecimal("250.00"),
            new BigDecimal("0.01"),
            2,
            new BigDecimal("2.50"),
            new BigDecimal("252.50"),
            LocalDate.of(2026, 5, 1),
            LocalDate.of(2026, 5, 10),
            LocalDate.of(2026, 5, 13),
            PaymentStatus.PAID
        );
    }
}
