package com.condologix.application.resident;

import com.condologix.application.unit.UnitModel;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "residents")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResidentModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "UNIT_ID", nullable = false)
    private UnitModel unit;

    @Column(name = "NAME", nullable = false, length = 50)
    private String name;
    @Column(name = "EMAIL", nullable = false, length = 100)
    private String email;
    @Column(name = "PHONE", nullable = false, length = 20)
    private String phone;

    public ResidentModel (
        UnitModel unit,
        String name,
        String email,
        String phone
    ) {
        if (unit == null) {throw new IllegalArgumentException("Unit cannot be null");}
        if (name == null || name.isBlank()) {throw new IllegalArgumentException("Name cannot be null or blank");}
        if (email == null || email.isBlank()) {throw new IllegalArgumentException("Email cannot be null or blank");}
        validatePhone(phone);

        this.unit = unit;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public void updateContactInfo(
        String email,
        String phone
    ) {
        if (email == null || email.isBlank()) {throw new IllegalArgumentException("Email cannot be null or blank");}
        validatePhone(phone);
        this.email = email;
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
