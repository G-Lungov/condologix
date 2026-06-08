package com.condologix.application.payment;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentDTO(
    Long id,
    Long buildingId,
    String billingPeriod,
    BigDecimal amount,
    BigDecimal interestRate,
    Integer graceDays,
    BigDecimal interestAmount,
    BigDecimal totalAmount,
    LocalDate createdAt,
    LocalDate dueDate,
    LocalDate paidAt,
    PaymentStatus status
) {
}
