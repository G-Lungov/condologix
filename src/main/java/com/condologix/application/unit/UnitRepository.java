package com.condologix.application.unit;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UnitRepository extends JpaRepository<UnitModel, Long> {

    List<UnitModel> findByBuildingId(Long buildingId);
    List<UnitModel> findByBuildingIdAndUnitType(Long buildingId, UnitType unitType);
    List<UnitModel> findByBuildingIdAndBlockAndNumber(Long buildingId, String block, short number);
    boolean existsByBuildingIdAndBlockAndNumber(Long buildingId, String block, short number);
}
