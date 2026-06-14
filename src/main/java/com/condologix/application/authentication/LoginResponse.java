package com.condologix.application.authentication;

public record LoginResponse(
    String token,
    String username,
    Role role
) {
}
