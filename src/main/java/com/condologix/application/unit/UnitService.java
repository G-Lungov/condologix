package com.condologix.application.unit;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.condologix.application.building.BuildingModel;
import com.condologix.application.building.BuildingRepository;

@Service
@Transactional
public class UnitService {

    private final UnitRepository unitRepository;
    private final BuildingRepository buildingRepository;
    public UnitService(UnitRepository unitRepository, BuildingRepository buildingRepository) {
        this.unitRepository = unitRepository;
        this.buildingRepository = buildingRepository;
    }

    public UnitModel createUnit(Long buildingId, short number, String block, UnitType unitType) {
        BuildingModel building = buildingRepository.findById(buildingId)
            .orElseThrow(() -> new IllegalArgumentException("Building not found with id: " + buildingId));
            String normalizedBlock = normalizedBlock(block);

        if (unitRepository.existsByBuildingIdAndBlockAndNumber(building.getId(), normalizedBlock, number)) {
            throw new IllegalArgumentException("Unit with the same block and number already exists");
        }

        UnitModel unit = new UnitModel(building, number, normalizedBlock, unitType);
        return unitRepository.save(unit);
    }

    @Transactional(readOnly = true)
    public List<UnitModel> getUnitsByBuilding(Long buildingId) {
        return unitRepository.findByBuildingId(buildingId);
    }

    @Transactional(readOnly = true)
    public List<UnitModel> getUnitsByBuildingAndType(Long buildingId, UnitType unitType) {
        return unitRepository.findByBuildingIdAndUnitType(buildingId, unitType);
    }

    private String normalizedBlock(String block) {
        if (block == null) throw new IllegalArgumentException("Block cannot be null");
        String normalized = block.trim().toUpperCase();
        if (normalized.isBlank()) throw new IllegalArgumentException("Block cannot be blank");
        return normalized;
    }
}
