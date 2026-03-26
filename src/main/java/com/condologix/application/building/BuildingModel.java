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
    private Long id;

    @Column(name = "NAME", nullable = false)
    private String name;
    @Column(name = "PHONE", nullable = false)
    private String phone;
    @Column(name = "EMAIL", nullable = false)
    private String email;
    @Column(name = "ADDRESS", nullable = false)
    private String address;
    @Column(name = "ADDRESS_NUMBER", nullable = false)
    private int addressNumber;
    @Column(name = "POSTAL_CODE", nullable = false)
    private String postalCode;
    @Column(name = "NEIGHBORHOOD", nullable = false)
    private String neighborhood;
    @Column(name = "CITY", nullable = false)
    private String city;
    @Column(name = "STATE", nullable = false)
    private String state;

    public BuildingModel (
        String name,
        String phone,
        String email,
        String address,
        int addressNumber,
        String postalCode,
        String neighborhood,
        String city,
        String state
    ) {
        if (name == null) throw new IllegalArgumentException("Name cannot be null");
        if (phone == null || phone.length() != 11 || phoneCharacteresIsDigit(phone) != true) throw new IllegalArgumentException("Phone cannot be null, must have 11 digits");
        if (email == null) throw new IllegalArgumentException("Email cannot be null");
        if (address == null) throw new IllegalArgumentException("Address cannot be null");
        if (addressNumber == 0) throw new IllegalArgumentException("Address number cannot be null");
        if (postalCode == null || postalCode.length() != 8) throw new IllegalArgumentException("Postal code cannot be null and must have 8 charcteres");
        if (neighborhood == null) throw new IllegalArgumentException("Neighborhood cannot be null");
        if (city == null) throw new IllegalArgumentException("City cannot be null");
        if (state == null) throw new IllegalArgumentException("State cannot be null");

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

    public static boolean phoneCharacteresIsDigit (String phone) {
        for (int i=0; i < phone.length(); i++) {
            if (!Character.isDigit(phone.charAt(i))) {
                return false;
            }
        }
        return true;
    }

}
