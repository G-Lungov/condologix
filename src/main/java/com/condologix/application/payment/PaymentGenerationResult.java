package com.condologix.application.payment;

public record PaymentGenerationResult(
    int created,
    int skipped,
    int failed
) {
}
