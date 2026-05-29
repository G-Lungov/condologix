package com.condologix.application.concierge;

public record ConciergeDTO (
    Long id,
    Long buildingId,
    String name,
    String phone
) {
}
