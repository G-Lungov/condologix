package com.condologix.application.payment;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentCreateDTO(
    @NotNull(message = "Building ID is required")
    @Positive(message = "Building ID must be a positive number")
    Long buildingId,

    @NotBlank(message = "Billing period cannot be blank")
    @Pattern(regexp = "\\d{4}-\\d{2}", message = "Billing period must use format YYYY-MM")
    String billingPeriod,

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be positive")
    BigDecimal amount,

    @NotNull(message = "Interest rate is required")
    @DecimalMin(value = "0.00", message = "Interest rate cannot be negative")
    BigDecimal interestRate,

    @NotNull(message = "Created date is required")
    LocalDate createdAt,

    @NotNull(message = "Due date is required")
    LocalDate dueDate
) {
}
