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

    @Column(name = "SENDER_NAME", nullable = false, length = 50)
    private String senderName;
    @Column(name = "CARRIER", nullable = false, length = 50)
    private String carrier;
    @Column(name = "TRACKING_CODE", nullable = false, length = 25)
    private String trackingCode;
    @Column(name = "DESCRIPTION", nullable = true, length = 100)
    private String description;

    @Column(name = "ARRIVED_AT")
    private LocalDateTime arrivedAt;
    @Column(name = "NOTIFIED_AT")
    private LocalDateTime notifiedAt;
    @Column(name = "PICED_UP_AT")
    private LocalDateTime pickedUpAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "REGISTRATION_TYPE")
    private OrderRegistrationType registrationType;
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private OrderStatus status;

    public OrderModel (
        BuildingModel buildingId,
        UnitModel unitId,
        ResidentModel residentId,
        ConciergeModel conciergeId,
        String senderName,
        String carrier,
        String trackingCode,
        String description
    ) {
        this.buildingId = buildingId;
        this.unitId = unitId;
        this.residentId = residentId;
        this.conciergeId = conciergeId;
        this.senderName = senderName;
        this.carrier = carrier;
        this.trackingCode = trackingCode;
        this.description = description;
    }
}
