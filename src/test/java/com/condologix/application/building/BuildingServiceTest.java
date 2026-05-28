package com.condologix.application.building;

import com.condologix.application.exception.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BuildingServiceTest {

    private final BuildingRepository buildingRepository = mock(BuildingRepository.class);
    private final BuildingService buildingService = new BuildingService(buildingRepository);

    @Test
    void createBuildingShouldReturnCreated() {
        BuildingCreateDTO request = createBuildingRequest();
        BuildingModel savedBuilding = createBuilding(1L);

        when(buildingRepository.existsByCnpj(request.cnpj())).thenReturn(false);
        when(buildingRepository.save(any(BuildingModel.class))).thenReturn(savedBuilding);

        BuildingDTO createdBuilding = buildingService.createBuilding(request);

        assertEquals(1L, createdBuilding.id());
        assertEquals("12345678000199", createdBuilding.cnpj());
        assertEquals("Condo One LTDA", createdBuilding.legalName());
        assertEquals("Condo One", createdBuilding.name());

        verify(buildingRepository).existsByCnpj(request.cnpj());
        verify(buildingRepository).save(any(BuildingModel.class));
    }

    @Test
    void createBuildingShouldThrowConflictWhenCnpjExists() {
        BuildingCreateDTO request = createBuildingRequest();

        when(buildingRepository.existsByCnpj(request.cnpj())).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> buildingService.createBuilding(request));

        verify(buildingRepository).existsByCnpj(request.cnpj());
        verify(buildingRepository, never()).save(any(BuildingModel.class));
    }

    @Test
    void createBuildingShouldWrapDataIntegrityViolation() {
        BuildingCreateDTO request = createBuildingRequest();

        when(buildingRepository.existsByCnpj(request.cnpj())).thenReturn(false);
        when(buildingRepository.save(any(BuildingModel.class))).thenThrow(new DataIntegrityViolationException("constraint"));

        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> buildingService.createBuilding(request)
        );

        assertEquals("Failed to create building due to data integrity violation", exception.getMessage());
        assertInstanceOf(DataIntegrityViolationException.class, exception.getCause());
    }

    @Test
    void updateBuildingShouldReturnUpdatedBuilding() {
        BuildingModel building = createBuilding(1L);
        BuildingUpdateDTO request = new BuildingUpdateDTO("11888887777", "updated@example.com");

        when(buildingRepository.findById(1L)).thenReturn(Optional.of(building));
        when(buildingRepository.save(building)).thenReturn(building);

        BuildingDTO updatedBuilding = buildingService.updateBuilding(1L, request);

        assertEquals(1L, updatedBuilding.id());
        assertEquals("11888887777", updatedBuilding.phone());
        assertEquals("updated@example.com", updatedBuilding.email());
        verify(buildingRepository).save(building);
    }

    @Test
    void updateBuildingShouldThrowNotFoundWhenBuildingDoesNotExist() {
        BuildingUpdateDTO request = new BuildingUpdateDTO("11888887777", "updated@example.com");

        when(buildingRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> buildingService.updateBuilding(99L, request)
        );

        assertEquals("Building not found: 99", exception.getMessage());
        verify(buildingRepository, never()).save(any(BuildingModel.class));
    }

    @Test
    void deleteBuildingShouldRemoveExistingBuilding() {
        BuildingModel building = createBuilding(1L);

        when(buildingRepository.findById(1L)).thenReturn(Optional.of(building));

        buildingService.deleteBuilding(1L);

        verify(buildingRepository).delete(building);
    }

    @Test
    void getBuildingByIdShouldReturnBuilding() {
        BuildingModel building = createBuilding(1L);

        when(buildingRepository.findById(1L)).thenReturn(Optional.of(building));

        BuildingDTO foundBuilding = buildingService.getBuildingById(1L);

        assertEquals(1L, foundBuilding.id());
        assertEquals("12345678000199", foundBuilding.cnpj());
        assertEquals("Condo One", foundBuilding.name());
    }

    @Test
    void getAllBuildingsShouldReturnMappedList() {
        BuildingModel firstBuilding = createBuilding(1L);
        BuildingModel secondBuilding = createBuilding(2L);
        ReflectionTestUtils.setField(secondBuilding, "cnpj", "99887766000155");
        ReflectionTestUtils.setField(secondBuilding, "name", "Condo Two");

        when(buildingRepository.findAll()).thenReturn(List.of(firstBuilding, secondBuilding));

        List<BuildingDTO> buildings = buildingService.getAllBuildings();

        assertEquals(2, buildings.size());
        assertEquals(1L, buildings.get(0).id());
        assertEquals(2L, buildings.get(1).id());
        assertEquals("Condo Two", buildings.get(1).name());
    }

    private BuildingCreateDTO createBuildingRequest() {
        return new BuildingCreateDTO(
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
