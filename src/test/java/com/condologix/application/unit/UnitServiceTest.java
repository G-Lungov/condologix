package com.condologix.application.unit;

import com.condologix.application.building.BuildingModel;
import com.condologix.application.building.BuildingRepository;
import com.condologix.application.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UnitServiceTest {

    private final UnitRepository unitRepository = mock(UnitRepository.class);
    private final BuildingRepository buildingRepository = mock(BuildingRepository.class);
    private final UnitService unitService = new UnitService(unitRepository, buildingRepository);

    @Test
    void createUnitShouldNormalizeBlockBeforeCheckingAndSaving() {
        BuildingModel building = createBuilding(1L);
        UnitCreateDTO request = new UnitCreateDTO(1L, (short) 101, " a ", UnitType.RESIDENTIAL);

        when(buildingRepository.findById(1L)).thenReturn(Optional.of(building));
        when(unitRepository.existsByBuildingIdAndBlockAndNumber(1L, "A", (short) 101)).thenReturn(false);
        when(unitRepository.save(any(UnitModel.class))).thenAnswer(invocation -> {
            UnitModel unit = invocation.getArgument(0);
            ReflectionTestUtils.setField(unit, "id", 7L);
            return unit;
        });

        UnitDTO createdUnit = unitService.createUnit(request);

        verify(unitRepository).existsByBuildingIdAndBlockAndNumber(1L, "A", (short) 101);
        verify(unitRepository).save(any(UnitModel.class));
        assertEquals(7L, createdUnit.id());
        assertEquals(1L, createdUnit.buildingId());
        assertEquals((short) 101, createdUnit.number());
        assertEquals("A", createdUnit.block());
        assertEquals(UnitType.RESIDENTIAL, createdUnit.unitType());
    }

    @Test
    void createUnitShouldThrowNotFoundWhenBuildingDoesNotExist() {
        UnitCreateDTO request = new UnitCreateDTO(99L, (short) 101, "A", UnitType.RESIDENTIAL);

        when(buildingRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> unitService.createUnit(request)
        );

        assertEquals("Building not found with id: 99", exception.getMessage());
        verify(unitRepository, never()).save(any(UnitModel.class));
    }

    @Test
    void createUnitShouldThrowConflictWhenDuplicateExists() {
        BuildingModel building = createBuilding(1L);
        UnitCreateDTO request = new UnitCreateDTO(1L, (short) 101, "A", UnitType.RESIDENTIAL);

        when(buildingRepository.findById(1L)).thenReturn(Optional.of(building));
        when(unitRepository.existsByBuildingIdAndBlockAndNumber(1L, "A", (short) 101)).thenReturn(true);

        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> unitService.createUnit(request)
        );

        assertEquals("Unit with the same block and number already exists", exception.getMessage());
        verify(unitRepository, never()).save(any(UnitModel.class));
    }

    @Test
    void createUnitShouldWrapDataIntegrityViolation() {
        BuildingModel building = createBuilding(1L);
        UnitCreateDTO request = new UnitCreateDTO(1L, (short) 101, "A", UnitType.RESIDENTIAL);

        when(buildingRepository.findById(1L)).thenReturn(Optional.of(building));
        when(unitRepository.existsByBuildingIdAndBlockAndNumber(1L, "A", (short) 101)).thenReturn(false);
        when(unitRepository.save(any(UnitModel.class))).thenThrow(new DataIntegrityViolationException("constraint"));

        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> unitService.createUnit(request)
        );

        assertEquals("Failed to create unit due to data integrity violation", exception.getMessage());
        assertInstanceOf(DataIntegrityViolationException.class, exception.getCause());
    }

    @Test
    void updateUnitTypeShouldReturnUpdatedUnit() {
        BuildingModel building = createBuilding(1L);
        UnitModel unit = new UnitModel(building, (short) 101, "A", UnitType.RESIDENTIAL);
        ReflectionTestUtils.setField(unit, "id", 7L);

        when(unitRepository.findById(7L)).thenReturn(Optional.of(unit));
        when(unitRepository.save(unit)).thenReturn(unit);

        UnitDTO updatedUnit = unitService.updateUnitType(7L, new UnitUpdateDTO(UnitType.COMERCIAL));

        assertEquals(7L, updatedUnit.id());
        assertEquals(UnitType.COMERCIAL, updatedUnit.unitType());
    }

    @Test
    void deleteUnitShouldRemoveExistingUnit() {
        BuildingModel building = createBuilding(1L);
        UnitModel unit = new UnitModel(building, (short) 101, "A", UnitType.RESIDENTIAL);
        ReflectionTestUtils.setField(unit, "id", 7L);

        when(unitRepository.findById(7L)).thenReturn(Optional.of(unit));

        unitService.deleteUnit(7L);

        verify(unitRepository).delete(unit);
    }

    @Test
    void getUnitsByBuildingShouldMapRepositoryResults() {
        BuildingModel building = createBuilding(5L);
        UnitModel firstUnit = new UnitModel(building, (short) 101, "A", UnitType.RESIDENTIAL);
        UnitModel secondUnit = new UnitModel(building, (short) 102, "A", UnitType.COMERCIAL);
        ReflectionTestUtils.setField(firstUnit, "id", 1L);
        ReflectionTestUtils.setField(secondUnit, "id", 2L);

        when(unitRepository.findByBuildingId(5L)).thenReturn(List.of(firstUnit, secondUnit));

        List<UnitDTO> units = unitService.getUnitsByBuilding(5L);

        assertEquals(2, units.size());
        assertEquals(1L, units.get(0).id());
        assertEquals(2L, units.get(1).id());
        assertEquals(UnitType.COMERCIAL, units.get(1).unitType());
    }

    @Test
    void getUnitsByBuildingAndTypeShouldRejectNullType() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> unitService.getUnitsByBuildingAndType(1L, null)
        );

        assertEquals("Unit type cannot be null", exception.getMessage());
        verify(unitRepository, never()).findByBuildingIdAndUnitType(any(), any());
    }

    private BuildingModel createBuilding(Long id) {
        BuildingModel building = new BuildingModel(
            "Condo One",
            "11999999999",
            "condo@example.com",
            "Main Street",
            123,
            "12345678",
            "Downtown",
            "Sao Paulo",
            "SP"
        );
        ReflectionTestUtils.setField(building, "id", id);
        return building;
    }
}
