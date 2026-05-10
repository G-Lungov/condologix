package com.condologix.application.building;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/buildings")
@Validated
public class BuildingController {

    private final BuildingService buildingService;

    public BuildingController(BuildingService buildingService) {
        this.buildingService = buildingService;
    }

    @PostMapping
    public ResponseEntity<BuildingDTO> createBuilding(@Valid @RequestBody BuildingCreateDTO buildingDTO) {
        BuildingDTO createdBuilding = buildingService.createBuilding(buildingDTO);
        return new ResponseEntity<>(createdBuilding, HttpStatus.CREATED);
    }

    @PutMapping("/{buildingId}")
    public ResponseEntity<BuildingDTO> updateBuilding(
        @PathVariable @Positive Long buildingId,
        @Valid @RequestBody BuildingUpdateDTO buildingDTO
    ) {
        BuildingDTO updatedBuilding = buildingService.updateBuilding(buildingId, buildingDTO);
        return ResponseEntity.ok(updatedBuilding);
    }

    @DeleteMapping("/{buildingId}")
    public ResponseEntity<Void> deleteBuilding(@PathVariable @Positive Long buildingId) {
        buildingService.deleteBuilding(buildingId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{buildingId}")
    public ResponseEntity<BuildingDTO> getBuildingById(@PathVariable @Positive Long buildingId) {
        BuildingDTO building = buildingService.getBuildingById(buildingId);
        return ResponseEntity.ok(building);
    }

    @GetMapping
    public ResponseEntity<List<BuildingDTO>> getAllBuildings() {
        List<BuildingDTO> buildings = buildingService.getAllBuildings();
        return ResponseEntity.ok(buildings);
    }
}
