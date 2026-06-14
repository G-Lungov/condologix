package com.condologix.application.authentication;

public record LoginRequest(
    String username,
    String password
) {
}
