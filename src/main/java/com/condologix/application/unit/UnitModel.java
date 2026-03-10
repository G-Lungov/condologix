package com.condologix.application.unit;

import com.condologix.application.building.BuildingModel;

import lombok.Data;

@Data
public class UnitModel {

    private Long id;
    private BuildingModel building;
    private short number;
    private String block;
    private UnitType type;

}
