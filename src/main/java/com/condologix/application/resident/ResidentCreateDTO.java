package com.condologix.application.resident;

import jakarta.validation.constraints.*;

public record ResidentCreateDTO(
    @NotNull(message = "Unit ID is required")
    @Positive(message = "Unit ID must be a positive number")
    Long unitId,

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be at most 100 characters")
    String name,

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    String email,

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^\\d{11}$", message = "Phone must have exactly 11 digits")
    String phone
) {
}
