package com.condologix.application.concierge;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ConciergeUpdateDTO(
    @NotBlank(message = "Phone number cannot be blank")
    @Pattern(regexp = "\\d{11}", message = "Phone number must contain exactly 11 digits")
    String phone
) {
}
