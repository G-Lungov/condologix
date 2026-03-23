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
    private String name;
    private long phone;
    private String email;
    private String address;
    private int addressNumber;
    private int postalCode;
    private String neighborhood;
    private String city;
    private String state;

}
