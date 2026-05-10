package com.condologix.application.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record OrderCreateDTO(
    @NotNull(message = "Building ID is required")
    @Positive(message = "Building ID must be a positive number")
    Long buildingId,

    @NotNull(message = "Unit ID is required")
    @Positive(message = "Unit ID must be a positive number")
    Long unitId,

    @Positive(message = "Resident ID must be a positive number")
    Long residentId,

    @NotNull(message = "Concierge ID is required")
    @Positive(message = "Concierge ID must be a positive number")
    Long conciergeId,

    @NotBlank(message = "Sender name is required")
    @Size(max = 50, message = "Sender name must be at most 50 characters")
    String senderName,

    @NotBlank(message = "Carrier is required")
    @Size(max = 50, message = "Carrier must be at most 50 characters")
    String carrier,

    @NotBlank(message = "Tracking code is required")
    @Size(max = 25, message = "Tracking code must be at most 25 characters")
    String trackingCode,

    @Size(max = 100, message = "Description must be at most 100 characters")
    String description,

    @NotNull(message = "Registration type is required")
    OrderRegistrationType registrationType
) {
}
