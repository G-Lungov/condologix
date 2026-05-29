package com.condologix.application.concierge;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record ConciergeCreateDTO(
    @NotNull(message = "Building ID is required")
    @Positive(message = "Building ID must be a positive number")
    Long buildingId,

    @NotBlank(message = "Name cannot be blank")
    String name,

    @NotBlank(message = "Phone number cannot be blank")
    @Pattern(regexp = "\\d{11}", message = "Phone number must contain exactly 11 digits")
    String phone
) {
}
