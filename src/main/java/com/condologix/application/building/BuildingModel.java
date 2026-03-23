package com.condologix.application.building;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "buildings")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BuildingModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "NAME", nullable = false)
    private String name;
    @Column(name = "PHONE", nullable = false)
    private long phone;
    @Column(name = "EMAIL", nullable = false)
    private String email;
    @Column(name = "ADDRESS", nullable = false)
    private String address;
    @Column(name = "ADDRESS_NUMBER", nullable = false)
    private int addressNumber;
    @Column(name = "POSTAL_CODE", nullable = false)
    private int postalCode;
    @Column(name = "NEIGHBORHOOD", nullable = false)
    private String neighborhood;
    @Column(name = "CITY", nullable = false)
    private String city;
    @Column(name = "STATE", nullable = false)
    private String state;

    public BuildingModel (
        String name,
        long phone,
        String email,
        String address,
        int addressNumber,
        int postalCode,
        String neighborhood,
        String city,
        String state
    ) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.addressNumber = addressNumber;
        this.postalCode = postalCode;
        this.neighborhood = neighborhood;
        this.city = city;
        this.state = state;
    }
}
