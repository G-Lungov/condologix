package com.condologix.application.building;

import lombok.Data;

@Data
public class BuildingModel {

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
