package com.condologix.application.unit;

import com.condologix.application.exception.*;
import com.condologix.application.building.BuildingModel;
import com.condologix.application.building.BuildingRepository;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;

@Service
@Transactional
public class UnitService {

    private final UnitRepository unitRepository;
    private final BuildingRepository buildingRepository;

    public UnitService(UnitRepository unitRepository, BuildingRepository buildingRepository) {
        this.unitRepository = unitRepository;
        this.buildingRepository = buildingRepository;
    }

    public UnitDTO createUnit(UnitCreateDTO unitDTO) {
        BuildingModel building = buildingRepository.findById(unitDTO.buildingId())
            .orElseThrow(() -> new ResourceNotFoundException("Building not found with id: " + unitDTO.buildingId()));

        String normalizedBlock = normalizedBlock(unitDTO.block());

        if (unitRepository.existsByBuildingIdAndBlockAndNumber(building.getId(), normalizedBlock, unitDTO.number())) {
            throw new IllegalStateException("Unit with the same block and number already exists");
        }

        UnitModel unit = new UnitModel(
            building,
            unitDTO.number(),
            normalizedBlock,
            unitDTO.unitType()
        );

        try {
            UnitModel savedUnit = unitRepository.save(unit);
            return toDTO(savedUnit);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("Failed to create unit due to data integrity violation", e);
        }
    }

    public UnitDTO updateUnitType(Long unitId, UnitUpdateDTO unitDTO) {
        UnitModel unit = unitRepository.findById(unitId)
            .orElseThrow(() -> new ResourceNotFoundException("Unit not found with id: " + unitId));
        unit.updateUnitType(unitDTO.unitType());
        UnitModel updateUnit = unitRepository.save(unit);
        return toDTO(updateUnit);
    }

    public void deleteUnit(Long unitId) {
        UnitModel unit = unitRepository.findById(unitId)
            .orElseThrow(() -> new ResourceNotFoundException("Unit not found with id: " + unitId));
        unitRepository.delete(unit);
    }

    @Transactional(readOnly = true)
    public List<UnitDTO> getUnitsByBuilding(Long buildingId) {
        if (buildingId == null) {
            throw new IllegalArgumentException("Building ID cannot be null");
        }
        return unitRepository.findByBuildingId(buildingId)
        .stream()
        .map(this::toDTO)
        .toList();
    }

    @Transactional(readOnly = true)
    public List<UnitDTO> getUnitsByBuildingAndType(Long buildingId, UnitType unitType) {
        if (buildingId == null) {
            throw new IllegalArgumentException("Building ID cannot be null");
        }
        if (unitType == null) {
            throw new IllegalArgumentException("Unit type cannot be null");
        }
        return unitRepository.findByBuildingIdAndUnitType(buildingId, unitType)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public String normalizedBlock(String block) {
        if (block == null) throw new IllegalArgumentException("Block cannot be null");
        String normalized = block.trim().toUpperCase();
        if (normalized.isBlank()) throw new IllegalArgumentException("Block cannot be blank");
        return normalized;
    }

    public UnitDTO toDTO(UnitModel unit) {
        return new UnitDTO(
            unit.getId(),
            unit.getBuilding().getId(),
            unit.getNumber(),
            unit.getBlock(),
            unit.getUnitType()
        );
    }
}
