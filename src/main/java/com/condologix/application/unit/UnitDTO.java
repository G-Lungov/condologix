package com.condologix.application.unit;

public record UnitDTO (
    Long id,
    Long buildingId,
    short number,
    String block,
    UnitType unitType
) {
}
