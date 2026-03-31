package com.condologix.application.unit;

import com.condologix.application.building.BuildingModel;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "units",
    uniqueConstraints = {
        @UniqueConstraint(name = "UK_UNIT_BUILDING_BLOCK_NUMBER",
        columnNames = {"BUILDING_ID", "BLOCK", "NUMBER"}
    )
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UnitModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "BUILDING_ID", nullable = false)
    private BuildingModel building;

    @Column(name = "NUMBER", nullable = false)
    private short number;
    @Column(name = "BLOCK", nullable = false)
    private String block;

    @Enumerated(EnumType.STRING)
    @Column(name = "UNIT_TYPE", nullable = false)
    private UnitType unitType;

    public UnitModel (
        BuildingModel building,
        short number,
        String block,
        UnitType unitType
    ) {
        if (building == null) throw new IllegalArgumentException("Building cannot be null");
        if (number <= 0 ) throw new IllegalArgumentException("Unit number cannot be null");
        if (block == null || block.isEmpty() || block.isBlank()) throw new IllegalArgumentException("Block cannot be null");
        if (unitType ==  null) throw new IllegalArgumentException("Unit type needs to be either RESIDENCIAL or COMERCIAL");

        this.building = building;
        this.number = number;
        this.block = block;
        this.unitType = unitType;
    }
}
