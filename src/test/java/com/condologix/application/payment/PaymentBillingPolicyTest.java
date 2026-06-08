package com.condologix.application.payment;

import com.condologix.application.building.BuildingModel;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PaymentBillingPolicyTest {

    @Test
    void constructorShouldCreatePolicy() {
        PaymentBillingPolicy policy = createPolicy();

        assertEquals(1L, policy.getBuilding().getId());
        assertEquals(0, new BigDecimal("250.00").compareTo(policy.getMonthlyFee()));
        assertEquals(0, new BigDecimal("0.01").compareTo(policy.getDailyInterestRate()));
        assertEquals(2, policy.getGraceDays());
        assertEquals(LocalDate.of(2026, 5, 1), policy.getValidFrom());
    }

    @Test
    void calculateInterestShouldReturnZeroInsideGracePeriod() {
        PaymentBillingPolicy policy = createPolicy();

        BigDecimal interest = policy.calculateInterest(
            new BigDecimal("250.00"),
            LocalDate.of(2026, 5, 10),
            LocalDate.of(2026, 5, 12)
        );

        assertEquals(0, BigDecimal.ZERO.compareTo(interest));
    }

    @Test
    void calculateInterestShouldApplyAfterGracePeriod() {
        PaymentBillingPolicy policy = createPolicy();

        BigDecimal interest = policy.calculateInterest(
            new BigDecimal("250.00"),
            LocalDate.of(2026, 5, 10),
            LocalDate.of(2026, 5, 13)
        );

        assertEquals(0, new BigDecimal("2.50").compareTo(interest));
    }

    @Test
    void constructorShouldRejectNegativeMonthlyFee() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new PaymentBillingPolicy(
                createBuilding(1L),
                new BigDecimal("-1.00"),
                new BigDecimal("0.01"),
                2,
                LocalDate.of(2026, 5, 1)
            )
        );

        assertEquals("Monthly fee must be positive", exception.getMessage());
    }

    private PaymentBillingPolicy createPolicy() {
        return new PaymentBillingPolicy(
            createBuilding(1L),
            new BigDecimal("250.00"),
            new BigDecimal("0.01"),
            2,
            LocalDate.of(2026, 5, 1)
        );
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
