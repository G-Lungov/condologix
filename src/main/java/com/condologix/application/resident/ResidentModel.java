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
    @Column(name = "PHONE", nullable = false, length = 13)
    private long phone;

    public ResidentModel (
        UnitModel unit,
        String name,
        String email,
        long phone
    ) {
        this.unit = unit;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public void updateContactInfo(
        String email,
        long phone
    ) {
        this.email = email;
        this.phone = phone;
    }

}
