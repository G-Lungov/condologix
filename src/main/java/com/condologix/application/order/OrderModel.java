package com.condologix.application.order;

import lombok.Data;
import java.time.LocalDateTime;

import com.condologix.application.building.BuildingModel;
import com.condologix.application.concierge.ConciergeModel;
import com.condologix.application.resident.ResidentModel;
import com.condologix.application.unit.UnitModel;

@Data
public class OrderModel {

    private long id;

    private BuildingModel buildingId;
    private UnitModel unitId;
    private ResidentModel residentId;
    private ConciergeModel conciergeId;

    private String senderName;
    private String carrier;
    private String trackingCode;
    private String description;

    private LocalDateTime arrivedAt;
    private LocalDateTime notifiedAt;
    private LocalDateTime pickedUpAt;

    private OrderRegistrationType registrationType;
    private OrderStatus status;

}
