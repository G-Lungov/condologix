package com.condologix.application.payment;

import com.condologix.application.building.BuildingModel;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PaymentServiceTest {

    private final PaymentRepository paymentRepository = mock(PaymentRepository.class);
    private final PaymentService paymentService = new PaymentService(paymentRepository);

    @Test
    void createPaymentShouldReturnCreatedPayment() {
        PaymentModel payment = createPayment(20L);

        when(paymentRepository.save(payment)).thenReturn(payment);

        PaymentModel createdPayment = paymentService.createPayment(payment);

        assertEquals(20L, createdPayment.getId());
        assertEquals(PaymentStatus.PENDING, createdPayment.getStatus());
        verify(paymentRepository).save(payment);
    }

    @Test
    void createPaymentShouldThrowConflictWhenPaymentAlreadyExistsForPeriod() {
        PaymentModel payment = createPayment(20L);

        when(paymentRepository.save(payment)).thenThrow(new DataIntegrityViolationException("constraint"));

        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> paymentService.createPayment(payment)
        );

        assertEquals("Payment already exists for this building and billing period", exception.getMessage());
    }

    @Test
    void payPaymentShouldMarkExistingPaymentAsPaid() {
        PaymentModel payment = createPayment(20L);

        when(paymentRepository.findById(20L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(payment)).thenReturn(payment);

        PaymentModel paidPayment = paymentService.payPayment(20L, LocalDate.of(2026, 5, 10));

        assertEquals(PaymentStatus.PAID, paidPayment.getStatus());
        assertEquals(LocalDate.of(2026, 5, 10), paidPayment.getPaidAt());
        verify(paymentRepository).save(payment);
    }

    @Test
    void payPaymentShouldThrowNotFoundWhenPaymentDoesNotExist() {
        when(paymentRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> paymentService.payPayment(99L, LocalDate.of(2026, 5, 10))
        );

        assertEquals("Payment not found: 99", exception.getMessage());
    }

    @Test
    void getPaymentsDueOnShouldDelegateToRepository() {
        LocalDate dueDate = LocalDate.of(2026, 5, 10);
        PaymentModel payment = createPayment(20L);

        when(paymentRepository.findByDueDate(dueDate)).thenReturn(List.of(payment));

        List<PaymentModel> payments = paymentService.getPaymentsDueOn(dueDate);

        assertEquals(1, payments.size());
        assertEquals(20L, payments.get(0).getId());
    }

    @Test
    void getOverduePaymentsShouldQueryPendingPaymentsDueBeforeToday() {
        LocalDate today = LocalDate.of(2026, 5, 20);
        PaymentModel payment = createPayment(20L);

        when(paymentRepository.findByDueDateLessThanEqualAndStatus(today, PaymentStatus.PENDING))
            .thenReturn(List.of(payment));

        List<PaymentModel> payments = paymentService.getOverduePayments(today);

        assertEquals(1, payments.size());
        assertEquals(PaymentStatus.PENDING, payments.get(0).getStatus());
    }

    private PaymentModel createPayment(Long id) {
        PaymentModel payment = new PaymentModel(
            createBuilding(1L),
            "2026-05",
            new BigDecimal("100.00"),
            new BigDecimal("0.01"),
            LocalDate.of(2026, 5, 1),
            LocalDate.of(2026, 5, 10)
        );
        ReflectionTestUtils.setField(payment, "id", id);
        return payment;
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
