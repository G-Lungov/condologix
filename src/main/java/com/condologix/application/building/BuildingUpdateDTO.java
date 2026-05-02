package com.condologix.application.building;

import jakarta.validation.constraints.*;

public record BuildingUpdateDTO(
    @NotBlank(message = "Phone contact is required, only digits")
    @Size(min = 11, max = 11, message = "Phone must have 11 digits")
    @Pattern(regexp = "^\\d{11}$", message = "Phone must have exactly 11 digits")
    String phone,

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be a valid email address")
    @Size(max = 100, message = "Email must be at most 100 characters")
    String email
) {

}