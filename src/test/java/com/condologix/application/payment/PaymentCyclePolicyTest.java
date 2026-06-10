package com.condologix.application.payment;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaymentCyclePolicyTest {

    private final PaymentCyclePolicy paymentCyclePolicy = new PaymentCyclePolicy();

    @Test
    void isGenerationDateShouldReturnTrueForLastDayOfMonth() {
        assertTrue(paymentCyclePolicy.isGenerationDate(LocalDate.of(2026, 6, 30)));
        assertTrue(paymentCyclePolicy.isGenerationDate(LocalDate.of(2028, 2, 29)));
    }

    @Test
    void isGenerationDateShouldReturnFalseWhenDateIsNotLastDayOfMonth() {
        assertFalse(paymentCyclePolicy.isGenerationDate(LocalDate.of(2026, 6, 29)));
        assertFalse(paymentCyclePolicy.isGenerationDate(LocalDate.of(2028, 2, 28)));
    }

    @Test
    void calculateBillingPeriodShouldReturnNextMonthInYearMonthFormat() {
        assertEquals("2026-07", paymentCyclePolicy.calculateBillingPeriod(LocalDate.of(2026, 6, 30)));
        assertEquals("2027-01", paymentCyclePolicy.calculateBillingPeriod(LocalDate.of(2026, 12, 31)));
    }

    @Test
    void calculateDueDateShouldReturnFifteenthDayOfNextMonth() {
        assertEquals(LocalDate.of(2026, 7, 15), paymentCyclePolicy.calculateDueDate(LocalDate.of(2026, 6, 30)));
        assertEquals(LocalDate.of(2027, 1, 15), paymentCyclePolicy.calculateDueDate(LocalDate.of(2026, 12, 31)));
    }
}
