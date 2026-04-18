package com.condologix.application.resident;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ResidentUpdateDTO(
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    String email,

    @Min(value = 10000000000L, message = "Phone must have exactly 11 digits")
    @Max(value = 99999999999L, message = "Phone must have exactly 11 digits")
    long phone
) {
}
