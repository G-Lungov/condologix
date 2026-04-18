package com.condologix.application.resident;

public record ResidentDTO(
    Long id,
    Long unitId,
    String name,
    String email,
    long phone
) {
}
