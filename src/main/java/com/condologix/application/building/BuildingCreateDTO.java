package com.condologix.application.building;

import jakarta.validation.constraints.*;

public record BuildingCreateDTO(

    @NotBlank(message = "CNPJ cannot be blank, only digits")
    @Size(min = 14, max = 14, message = "CNPJ must have 14 characters")
    String cnpj,

    @NotBlank(message = "Legal name cannot be blank")
    @Size(min = 3, max = 100, message = "Legal name must be between 3 and 100 characters")
    String legalName,

    @NotBlank(message = "Name cannot be blank")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    String name,

    @NotBlank(message = "Phone contact is required, only digits")
    @Size(min = 11, max = 11, message = "Phone must have 11 digits")
    @Pattern(regexp = "^\\d{11}$", message = "Phone must have exactly 11 digits")
    String phone,

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be a valid email address")
    @Size(max = 100, message = "Email must be at most 100 characters")
    String email,

    @NotBlank(message = "Address cannot be blank")
    @Size(min = 5, max = 150, message = "Address must be between 5 and 150 characters")
    String address,

    @NotNull (message = "Address number is required")
    @Positive(message = "Address number must be a positive integer number")
    Integer addressNumber,

    @NotBlank(message = "Postal code is required")
    @Pattern(regexp = "^\\d{8}$", message = "Postal code must contain exactly 8 digits")
    String postalCode,

    @NotBlank(message = "Neighborhood is required")
    @Size(max = 100, message = "Neighborhood must be at most 100 characters")
    String neighborhood,

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must be at most 100 characters")
    String city,

    @NotBlank(message = "State is required")
    @Pattern(regexp = "^[A-Z]{2}$", message = "State must contain exactly 2 uppercase letters")
    String state
) {

}
