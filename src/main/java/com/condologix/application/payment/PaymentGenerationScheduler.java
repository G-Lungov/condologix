package com.condologix.application.payment;

import java.time.LocalDate;

import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;

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

    @Scheduled(cron = "0 5 0 * * *", zone = "America/Sao_Paulo")
    public void generateMonthlyPayments() {
        LocalDate today = LocalDate.now();

        if (!paymentCyclePolicy.isGenerationDate(today)) {
            return;
        }
        paymentGenerationService.generateMonthlyPayments(today);
    }
}
