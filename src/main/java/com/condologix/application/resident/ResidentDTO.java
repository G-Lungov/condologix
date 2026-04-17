package com.condologix.application.resident;

import lombok.Getter;
import jakarta.validation.constraints.*;

@Getter
public class ResidentDTO {

    private Long id;

    @NotNull(message = "Unit id is required")
    private Long unitId;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be at most 100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\d{10,13}$", message = "Phone must contain only digits (10 to 13)")
    private String phone;

}
