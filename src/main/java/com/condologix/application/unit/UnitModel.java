package com.condologix.application.unit;

import com.condologix.application.building.BuildingModel;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "units")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UnitModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BuildingModel building;
    private short number;
    private String block;
    private UnitType type;

}
