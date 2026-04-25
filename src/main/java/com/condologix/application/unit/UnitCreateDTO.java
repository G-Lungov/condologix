package com.condologix.application.unit;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UnitCreateDTO(
    @NotNull(message = "Building ID is required")
    @Positive(message = "Building ID must be a positive number")
    Long buildingId,

    @Positive(message = "Unit number must be a positive number")
    short number,

    @NotBlank(message = "Block cannot be blank")
    String block,

    @NotNull(message = "Unit type must be selected between RESIDENTIAL and COMERCIAL")
    UnitType unitType
) {
}
