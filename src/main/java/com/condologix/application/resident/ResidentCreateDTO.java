package com.condologix.application.resident;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ResidentCreateDTO(
    @NotNull @Positive(message = "Unit ID is required")
    Long unitId,

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be at most 100 characters")
    String name,

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    String email,

    @Min(value = 10000000000L, message = "Phone must have exactly 11 digits")
    @Max(value = 99999999999L, message = "Phone must have exactly 11 digits")
    long phone
) {
}
