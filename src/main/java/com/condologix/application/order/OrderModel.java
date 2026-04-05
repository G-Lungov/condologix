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
    private Long id;
    @ManyToOne(optional = false)
    @JoinColumn(name =  "BUILDING_ID", nullable = false)
    private BuildingModel building;
    @ManyToOne(optional = false)
    @JoinColumn(name = "UNIT_ID", nullable = false)
    private UnitModel unit;
    @ManyToOne(optional = true)
    @JoinColumn(name = "RESIDENT_ID")
    private ResidentModel resident;
    @ManyToOne(optional = false)
    @JoinColumn(name = "CONCIERGE_ID", nullable = false)
    private ConciergeModel concierge;

    @Column(name = "SENDER_NAME", nullable = false, length = 50)
    private String senderName;
    @Column(name = "CARRIER", nullable = false, length = 50)
    private String carrier;
    @Column(name = "TRACKING_CODE", nullable = false, length = 25)
    private String trackingCode;
    @Column(name = "DESCRIPTION", nullable = true, length = 100)
    private String description;

    @Column(name = "ARRIVED_AT", nullable = false)
    private LocalDateTime arrivedAt;
    @Column(name = "NOTIFIED_AT")
    private LocalDateTime notifiedAt;
    @Column(name = "PICKED_UP_AT")
    private LocalDateTime pickedUpAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "REGISTRATION_TYPE", nullable = false)
    private OrderRegistrationType registrationType;
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private OrderStatus status;

    public OrderModel (
        BuildingModel building,
        UnitModel unit,
        ResidentModel resident,
        ConciergeModel concierge,
        String senderName,
        String carrier,
        String trackingCode,
        String description,
        OrderRegistrationType registrationType
    ) {
        if (building == null) throw new IllegalArgumentException("Building cannot be null");
        if (unit == null) throw new IllegalArgumentException("Unit cannot be null");
        if (concierge == null) throw new IllegalArgumentException("Concierge cannot be null");
        if (senderName ==  null || senderName.isBlank()) throw new IllegalArgumentException("Sender name cannot be null or blank");
        if (carrier == null || carrier.isBlank()) throw new IllegalArgumentException ("Carrier cannot be null or blanck");
        if (resident != null && !resident.getUnit().equals(unit)) {throw new IllegalArgumentException("Resident does not belong to the unit");}

        this.building = building;
        this.unit = unit;
        this.resident = resident;
        this.concierge = concierge;
        this.senderName = senderName;
        this.carrier = carrier;
        this.trackingCode = trackingCode;
        this.description = description;
        this.registrationType = registrationType;
        this.arrivedAt = LocalDateTime.now();
        this.status = OrderStatus.RECEIVED;
    }

    public void markAsNotified() {
        if (this.status != OrderStatus.RECEIVED) {
            throw new IllegalArgumentException("Only orders with status RECEIVED can be marked as notified");
        }
        this.status = OrderStatus.NOTIFIED;
        this.notifiedAt = LocalDateTime.now();
    }

    public void markAsPickedUp() {
        if (this.status == OrderStatus.PICKED_UP) {
            throw new IllegalArgumentException("Order is already macked as picked up");
        }
        this.status = OrderStatus.PICKED_UP;
        this.pickedUpAt = LocalDateTime.now();
    }

    public void changeResident(ResidentModel newResident, UnitModel newUnit) {
        if (this.status == OrderStatus.PICKED_UP) {
            throw new IllegalArgumentException("Cannot change resident for an order that has already been picked up");
        }
        this.unit = newUnit;
        this.resident = newResident;
    }

    public void assignResident(ResidentModel resident) {
        if (resident == null) {
            throw new IllegalArgumentException("Resident cannot be null");
        }
        if (!resident.getUnit().equals(this.unit)) {
            throw new IllegalArgumentException("Resident does not belong to the unit");
        }
        if (this.status == OrderStatus.PICKED_UP) {
            throw new IllegalArgumentException("Cannot assign resident after pickup");
        }
        this.resident = resident;
    }

}
