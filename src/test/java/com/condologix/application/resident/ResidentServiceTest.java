package com.condologix.application.resident;

import com.condologix.application.building.BuildingModel;
import com.condologix.application.exception.ResourceNotFoundException;
import com.condologix.application.unit.UnitModel;
import com.condologix.application.unit.UnitRepository;
import com.condologix.application.unit.UnitType;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ResidentServiceTest {

    private final ResidentRepository residentRepository = mock(ResidentRepository.class);
    private final UnitRepository unitRepository = mock(UnitRepository.class);
    private final ResidentService residentService = new ResidentService(residentRepository, unitRepository);

    @Test
    void createResidentShouldReturnCreated() {
        UnitModel unit = createUnit(5L);
        ResidentCreateDTO request = new ResidentCreateDTO(5L, "John Doe", "john@example.com", "11999999999");

        when(unitRepository.findById(5L)).thenReturn(Optional.of(unit));
        when(residentRepository.save(any(ResidentModel.class))).thenAnswer(invocation -> {
            ResidentModel resident = invocation.getArgument(0);
            ReflectionTestUtils.setField(resident, "id", 10L);
            return resident;
        });

        ResidentDTO createdResident = residentService.createResident(request);

        assertEquals(10L, createdResident.id());
        assertEquals(5L, createdResident.unitId());
        assertEquals("John Doe", createdResident.name());
        assertEquals("john@example.com", createdResident.email());
        assertEquals("11999999999", createdResident.phone());
    }

    @Test
    void createResidentShouldThrowNotFoundWhenUnitDoesNotExist() {
        ResidentCreateDTO request = new ResidentCreateDTO(99L, "John Doe", "john@example.com", "11999999999");

        when(unitRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> residentService.createResident(request)
        );

        assertEquals("Unit not found: 99", exception.getMessage());
        verify(residentRepository, never()).save(any(ResidentModel.class));
    }

    @Test
    void createResidentShouldWrapDataIntegrityViolation() {
        UnitModel unit = createUnit(5L);
        ResidentCreateDTO request = new ResidentCreateDTO(5L, "John Doe", "john@example.com", "11999999999");

        when(unitRepository.findById(5L)).thenReturn(Optional.of(unit));
        when(residentRepository.save(any(ResidentModel.class))).thenThrow(new DataIntegrityViolationException("constraint"));

        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> residentService.createResident(request)
        );

        assertEquals("Failed to create resident due to data integrity violation", exception.getMessage());
        assertInstanceOf(DataIntegrityViolationException.class, exception.getCause());
    }

    @Test
    void updateResidentShouldReturnUpdatedResident() {
        ResidentModel resident = createResident(10L, createUnit(5L));
        ResidentUpdateDTO request = new ResidentUpdateDTO("updated@example.com", "11888887777");

        when(residentRepository.findById(10L)).thenReturn(Optional.of(resident));
        when(residentRepository.save(resident)).thenReturn(resident);

        ResidentDTO updatedResident = residentService.updateResident(10L, request);

        assertEquals(10L, updatedResident.id());
        assertEquals("updated@example.com", updatedResident.email());
        assertEquals("11888887777", updatedResident.phone());
    }

    @Test
    void updateResidentShouldThrowNotFoundWhenResidentDoesNotExist() {
        ResidentUpdateDTO request = new ResidentUpdateDTO("updated@example.com", "11888887777");

        when(residentRepository.findById(10L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> residentService.updateResident(10L, request)
        );

        assertEquals("Resident not found: 10", exception.getMessage());
        verify(residentRepository, never()).save(any(ResidentModel.class));
    }

    @Test
    void deleteResidentShouldRemoveExistingResident() {
        ResidentModel resident = createResident(10L, createUnit(5L));

        when(residentRepository.findById(10L)).thenReturn(Optional.of(resident));

        residentService.deleteResident(10L);

        verify(residentRepository).delete(resident);
    }

    @Test
    void getResidentsByUnitShouldReturnMappedList() {
        UnitModel unit = createUnit(5L);
        ResidentModel firstResident = createResident(1L, unit);
        ResidentModel secondResident = createResident(2L, unit);
        ReflectionTestUtils.setField(secondResident, "name", "Jane Doe");

        when(residentRepository.findByUnitId(5L)).thenReturn(List.of(firstResident, secondResident));

        List<ResidentDTO> residents = residentService.getResidentsByUnit(5L);

        assertEquals(2, residents.size());
        assertEquals(1L, residents.get(0).id());
        assertEquals(2L, residents.get(1).id());
        assertEquals("Jane Doe", residents.get(1).name());
    }

    @Test
    void getResidentsByUnitShouldRejectNullUnitId() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> residentService.getResidentsByUnit(null)
        );

        assertEquals("Unit ID cannot be null", exception.getMessage());
        verify(residentRepository, never()).findByUnitId(any());
    }

    private ResidentModel createResident(Long id, UnitModel unit) {
        ResidentModel resident = new ResidentModel(unit, "John Doe", "john@example.com", "11999999999");
        ReflectionTestUtils.setField(resident, "id", id);
        return resident;
    }

    private UnitModel createUnit(Long id) {
        UnitModel unit = new UnitModel(createBuilding(1L), (short) 101, "A", UnitType.RESIDENTIAL);
        ReflectionTestUtils.setField(unit, "id", id);
        return unit;
    }

    private BuildingModel createBuilding(Long id) {
        BuildingModel building = new BuildingModel(
            "12345678000199",
            "Condo One LTDA",
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
