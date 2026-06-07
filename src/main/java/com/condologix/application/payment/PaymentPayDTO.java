package com.condologix.application.payment;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record PaymentPayDTO(
    @NotNull(message = "Payment date is required")
    LocalDate paymentDate
) {
}
