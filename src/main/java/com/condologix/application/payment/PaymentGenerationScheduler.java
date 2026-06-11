package com.condologix.application.payment;

import java.time.LocalDate;

import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PaymentGenerationScheduler {

    private final PaymentGenerationService paymentGenerationService;
    private final PaymentCyclePolicy paymentCyclePolicy;

    public PaymentGenerationScheduler(
        PaymentGenerationService paymentGenerationService,
        PaymentCyclePolicy paymentCyclePolicy
    ) {
        this.paymentGenerationService = paymentGenerationService;
        this.paymentCyclePolicy = paymentCyclePolicy;
    }

    @Transactional
    @Scheduled(cron = "0 5 0 * * *", zone = "America/Sao_Paulo")
    public void generateMonthlyPayments() {
        LocalDate today = LocalDate.now();

        try {;
            if (!paymentCyclePolicy.isGenerationDate(today)) {
                return;
            }
            paymentGenerationService.generateMonthlyPayments(today);
        } catch (Exception e) {
            throw new IllegalStateException("Todays is not the day to generate the payments", e);
        }
    }
}
