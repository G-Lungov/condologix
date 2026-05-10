package com.condologix.application.order;

import java.time.LocalDateTime;

public record OrderDTO(
    Long id,
    Long buildingId,
    Long unitId,
    Long residentId,
    Long conciergeId,
    String senderName,
    String carrier,
    String trackingCode,
    String description,
    LocalDateTime arrivedAt,
    LocalDateTime notifiedAt,
    LocalDateTime pickedUpAt,
    OrderRegistrationType registrationType,
    OrderStatus status
) {
}
