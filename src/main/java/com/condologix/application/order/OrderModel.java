package com.condologix.application.order;

import com.condologix.application.building.BuildingModel;
import com.condologix.application.concierge.ConciergeModel;
import com.condologix.application.resident.ResidentModel;
import com.condologix.application.unit.UnitModel;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(optional = false)
    @JoinColumn(name =  "BUILDING_ID", nullable = false)
    private BuildingModel buildingId;
    @ManyToOne(optional = false)
    @JoinColumn(name = "NAME_ID", nullable = false)
    private UnitModel unitId;
    @ManyToOne(optional = false)
    @JoinColumn(name = "RESIDENT_ID", nullable = false)
    private ResidentModel residentId;
    @ManyToOne(optional = false)
    @JoinColumn(name = "CONCIERGE_ID", nullable = false)
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
