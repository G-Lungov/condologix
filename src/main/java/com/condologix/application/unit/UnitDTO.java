package com.condologix.application.unit;

public record UnitDTO (
    Long id,
    Long buildingId,
    Short number,
    String block,
    UnitType unitType
) {
}
