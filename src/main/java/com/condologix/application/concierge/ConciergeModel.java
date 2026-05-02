package com.condologix.application.concierge;

import com.condologix.application.building.BuildingModel;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "concierges")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConciergeModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "BUILDING_ID", nullable = false)
    private BuildingModel building;

    @Column(name = "NAME", nullable = false, length = 100)
    private String name;

    @Column(name = "PHONE", nullable = false, length = 20)
    private String phone;

    public ConciergeModel(BuildingModel building, String name, String phone) {
        if (building == null) {
            throw new IllegalArgumentException("Building cannot be null");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank");
        }
        validatePhone(phone);

        this.building = building;
        this.name = name;
        this.phone = phone;
    }

    private void validatePhone(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("Phone number cannot be null or blank");
        }
        if (phone.length() != 11 || !containsOnlyDigits(phone)) {
            throw new IllegalArgumentException("Phone number must contain exactly 11 digits");
        }
    }

    private boolean containsOnlyDigits(String value) {
        for (int i = 0; i < value.length(); i++) {
            if (!Character.isDigit(value.charAt(i))) {
                return false;
            }
        }
        return true;
    }

}
