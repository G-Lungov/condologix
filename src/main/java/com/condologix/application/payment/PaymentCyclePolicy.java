package com.condologix.application.payment;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;

import org.springframework.stereotype.Component;

@Component
public class PaymentCyclePolicy {

    public boolean isGenerationDate(LocalDate date) {
        return date.equals(date.with(TemporalAdjusters.lastDayOfMonth()));
    }

    public String calculateBillingPeriod(LocalDate generationDate) {
        return YearMonth.from(generationDate.plusMonths(1)).toString();
    }

    public LocalDate calculateDueDate(LocalDate generationDate) {
        return generationDate
            .plusMonths(1)
            .withDayOfMonth(15);
    }

}
