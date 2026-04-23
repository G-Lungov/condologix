package com.condologix.application.unit;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import org.springframework.validation.annotation.Validated;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/units")
@Validated
public class UnitController {

    private final UnitService unitService;

    public UnitController(UnitService unitService) {
        this.unitService = unitService;
    }

    @PostMapping
    public ResponseEntity<UnitDTO> createUnit(@Valid @RequestBody UnitCreateDTO unitDTO) {
        UnitDTO createUnitDTO = unitService.createUnit(unitDTO);
        return new ResponseEntity<>(createUnit, HttpStatus.CREATED);
    }

    @PutMapping("/{unitId}/type")
    public ResponseEntity<UnitDTO> updateUnitType(@PathVariable @Positive Long unitId, @Valid @RequestBody UnitUpdateDTO unitDTO) {
        UnitDTO updateUnit = unitService.updateUnit(unitId, unitDTO);
        return ResponseEntity.ok(updatedUnit);
    }

    @DeleteMapping("/{unitId}")
    public ResponseEntity<Void> deleteUnit(@PathVariable @Positive Long unitId) {
        unitService.deleteUnit(unitId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/building/{buildingId}")
    public ResponseEntity<List<UnitDTO>> getUnitsByBuilding(@PathVariable @Positive Long buildingId) {
        List<UnitDTO> units = unitService.getUnitsByBuilding(buildingId);
        return ResponseEntity.ok(units);
    }

}
