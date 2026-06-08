package com.condologix.application.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record PaymentCreateDTO(
    @NotNull(message = "Building ID is required")
    @Positive(message = "Building ID must be a positive number")
    Long buildingId,

    @NotBlank(message = "Billing period cannot be blank")
    @Pattern(regexp = "\\d{4}-\\d{2}", message = "Billing period must use format YYYY-MM")
    String billingPeriod,

    @NotNull(message = "Created date is required")
    LocalDate createdAt,

    @NotNull(message = "Due date is required")
    LocalDate dueDate
) {
}
