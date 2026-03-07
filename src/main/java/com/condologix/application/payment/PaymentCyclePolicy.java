package com.condologix.application.payment;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

public class PaymentCyclePolicy {

    public boolean isGenerationDate(LocalDate date) {
        return date.equals(date.with(TemporalAdjusters.lastDayOfMonth()));
    }

    public LocalDate calculateDueDate(LocalDate generationDate) {
        return generationDate
            .plusMonths(1)
            .withDayOfMonth(15);
    }

}
