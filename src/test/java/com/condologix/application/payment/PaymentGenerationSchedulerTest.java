package com.condologix.application.payment;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.LocalDate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PaymentGenerationSchedulerTest {

    private final PaymentGenerationService paymentGenerationService = mock(PaymentGenerationService.class);
    private final PaymentCyclePolicy paymentCyclePolicy = mock(PaymentCyclePolicy.class);
    private final PaymentGenerationScheduler paymentGenerationScheduler = new PaymentGenerationScheduler(
        paymentGenerationService,
        paymentCyclePolicy
    );

    @Test
    void generateMonthlyPaymentsShouldRunWhenTodayIsGenerationDate() {
        LocalDate today = LocalDate.of(2026, 6, 30);

        try (MockedStatic<LocalDate> localDate = Mockito.mockStatic(LocalDate.class, Mockito.CALLS_REAL_METHODS)) {
            localDate.when(LocalDate::now).thenReturn(today);
            when(paymentCyclePolicy.isGenerationDate(today)).thenReturn(true);

            paymentGenerationScheduler.generateMonthlyPayments();

            verify(paymentGenerationService).generateMonthlyPayments(today);
        }
    }

    @Test
    void generateMonthlyPaymentsShouldDoNothingWhenTodayIsNotGenerationDate() {
        LocalDate today = LocalDate.of(2026, 6, 29);

        try (MockedStatic<LocalDate> localDate = Mockito.mockStatic(LocalDate.class, Mockito.CALLS_REAL_METHODS)) {
            localDate.when(LocalDate::now).thenReturn(today);
            when(paymentCyclePolicy.isGenerationDate(today)).thenReturn(false);

            paymentGenerationScheduler.generateMonthlyPayments();

            verify(paymentGenerationService, never()).generateMonthlyPayments(today);
        }
    }
}
