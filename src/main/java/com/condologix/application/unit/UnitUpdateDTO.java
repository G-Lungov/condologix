package com.condologix.application.unit;

import jakarta.validation.constraints.NotNull;

public record UnitUpdateDTO(
    @NotNull(message = "Unit type must be selected between RESIDENCIAL and COMERCIAL")
    UnitType unitType
) {
}
