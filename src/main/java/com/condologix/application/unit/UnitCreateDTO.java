package com.condologix.application.unit;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UnitCreateDTO(
    @NotNull @Positive(message = "Building ID must be a positive number")
    Long buildingId,

    @NotBlank(message = "Unit number cannot be blank")
    @Size(min = 1, max = 99999, message = "Unit number must be between 1 and 99999")
    short number,

    @NotBlank(message = "Block cannot be blank")
    @Size(max = 50, message = "Block must be at most 50 characters")
    String block,

    @NotNull(message = "Unit type must be selected between RESIDENCIAL and COMERCIAL")
    UnitType unitType
) {
}
