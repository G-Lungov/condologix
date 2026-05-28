package com.condologix.application.payment;

import com.condologix.application.building.BuildingModel;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaymentModelTest {

    @Test
    void markAsPaidShouldSetPaidStatusWithoutInterestWhenPaidOnTime() {
        PaymentModel payment = createPayment();

        payment.markAsPaid(LocalDate.of(2026, 5, 10));

        assertEquals(PaymentStatus.PAID, payment.getStatus());
        assertEquals(LocalDate.of(2026, 5, 10), payment.getPaidAt());
        assertEquals(0, BigDecimal.ZERO.compareTo(payment.getInterestAmount()));
        assertEquals(0, new BigDecimal("100.00").compareTo(payment.getTotalAmount()));
    }

    @Test
    void markAsPaidShouldCalculateInterestWhenPaidLate() {
        PaymentModel payment = createPayment();

        payment.markAsPaid(LocalDate.of(2026, 5, 13));

        assertEquals(PaymentStatus.PAID, payment.getStatus());
        assertEquals(0, new BigDecimal("3.00").compareTo(payment.getInterestAmount()));
        assertEquals(0, new BigDecimal("103.00").compareTo(payment.getTotalAmount()));
    }

    @Test
    void markAsPaidShouldRejectAlreadyPaidPayment() {
        PaymentModel payment = createPayment();
        payment.markAsPaid(LocalDate.of(2026, 5, 10));

        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> payment.markAsPaid(LocalDate.of(2026, 5, 11))
        );

        assertEquals("Only pending payments can be paid", exception.getMessage());
    }

    @Test
    void isOverdueShouldReturnTrueOnlyForPendingPaymentsAfterDueDate() {
        PaymentModel payment = createPayment();

        assertTrue(payment.isOverdue(LocalDate.of(2026, 5, 11)));

        payment.markAsPaid(LocalDate.of(2026, 5, 11));

        assertFalse(payment.isOverdue(LocalDate.of(2026, 5, 12)));
    }

    @Test
    void constructorShouldRejectNegativeAmount() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new PaymentModel(
                createBuilding(1L),
                "2026-05",
                new BigDecimal("-1.00"),
                new BigDecimal("0.01"),
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2026, 5, 10)
            )
        );

        assertEquals("Amount must be positive", exception.getMessage());
    }

    private PaymentModel createPayment() {
        PaymentModel payment = new PaymentModel(
            createBuilding(1L),
            "2026-05",
            new BigDecimal("100.00"),
            new BigDecimal("0.01"),
            LocalDate.of(2026, 5, 1),
            LocalDate.of(2026, 5, 10)
        );
        ReflectionTestUtils.setField(payment, "id", 20L);
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
