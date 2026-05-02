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

    @Column(name = "CNPJ", nullable = false, unique = true)
    private String cnpj;
    @Column(name = "LEGAL_NAME", nullable = false)
    private String legalName;
    @Column(name = "NAME", nullable = false)
    private String name;
    @Column(name = "PHONE", nullable = false)
    private String phone;
    @Column(name = "EMAIL", nullable = false)
    private String email;
    @Column(name = "ADDRESS", nullable = false)
    private String address;
    @Column(name = "ADDRESS_NUMBER", nullable = false)
    private Integer addressNumber;
    @Column(name = "POSTAL_CODE", nullable = false)
    private String postalCode;
    @Column(name = "NEIGHBORHOOD", nullable = false)
    private String neighborhood;
    @Column(name = "CITY", nullable = false)
    private String city;
    @Column(name = "STATE", nullable = false)
    private String state;

    public BuildingModel (
        String cnpj,
        String legalName,
        String name,
        String phone,
        String email,
        String address,
        Integer addressNumber,
        String postalCode,
        String neighborhood,
        String city,
        String state
    ) {
        if (cnpj == null || cnpj.length() != 14 || !characteresIsDigit(cnpj)) throw new IllegalArgumentException("CNPJ cannot be null, must have 14 digits");
        if (legalName == null || legalName.length() < 3 || legalName.length() > 100) throw new IllegalArgumentException("Legal name cannot be null and must have between 3 and 100 characters");
        if (name == null || name.length() < 3 || name.length() > 100) throw new IllegalArgumentException("Name cannot be null and must have between 3 and 100 characters");
        if (phone == null || phone.length() != 11 || !characteresIsDigit(phone)) throw new IllegalArgumentException("Phone cannot be null, must have 11 digits");
        if (email == null) throw new IllegalArgumentException("Email cannot be null");
        if (address == null) throw new IllegalArgumentException("Address cannot be null");
        if (addressNumber == null) throw new IllegalArgumentException("Address number cannot be null");
        if (postalCode == null || postalCode.length() != 8 || !characteresIsDigit(postalCode)) throw new IllegalArgumentException("Postal code cannot be null and must have 8 digits");
        if (neighborhood == null) throw new IllegalArgumentException("Neighborhood cannot be null");
        if (city == null) throw new IllegalArgumentException("City cannot be null");
        if (state == null || state.length() != 2) throw new IllegalArgumentException("State cannot be null, must have 2 characters");

        this.cnpj = cnpj;
        this.legalName = legalName;
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

    public static boolean characteresIsDigit (String string) {
        for (int i=0; i < string.length(); i++) {
            if (!Character.isDigit(string.charAt(i))) {
                return false;
            }
        }
        return true;
    }

}
