package com.condologix.application.concierge;

import com.condologix.application.exception.*;
import com.condologix.application.building.BuildingModel;
import com.condologix.application.building.BuildingRepository;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ConciergeServiceTest {

    private final ConciergeRepository conciergeRepository = mock(ConciergeRepository.class);
    private final BuildingRepository buildingRepository = mock(BuildingRepository.class);
    private final ConciergeService conciergeService = new ConciergeService(conciergeRepository, buildingRepository);

    @Test
    void createConciergeShouldReturnCreated() {
        BuildingModel building = createBuilding(1L);
        ConciergeCreateDTO request = new ConciergeCreateDTO(1L, "John Doe", "01234567890");

        when(buildingRepository.findById(1L)).thenReturn(Optional.of(building));
        when(conciergeRepository.save(any(ConciergeModel.class))).thenAnswer(invocation -> {
            ConciergeModel concierge = invocation.getArgument(0);
            ReflectionTestUtils.setField(concierge, "id", 7L);
            return concierge;
        });

        ConciergeDTO createdConcierge = conciergeService.createConcierge(request);

        verify(conciergeRepository).save(any(ConciergeModel.class));
        assertEquals(7L, createdConcierge.id());
        assertEquals(1L, createdConcierge.buildingId());
        assertEquals("John Doe", createdConcierge.name());
        assertEquals("01234567890", createdConcierge.phone());
    }

    @Test
    void createConciergeShouldThrowNotFoundWhenBuildingDoesNotExist() {
        ConciergeCreateDTO request = new ConciergeCreateDTO(99L, "John Doe", "01234567890");

        when(buildingRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> conciergeService.createConcierge(request)
        );

        assertEquals("Building not found with id: 99", exception.getMessage());
        verify(conciergeRepository, never()).save(any(ConciergeModel.class));
    }

    @Test
    void createConciergeShouldWrapDataIntegrityViolation() {
        BuildingModel building = createBuilding(1L);
        ConciergeCreateDTO request = new ConciergeCreateDTO(1L, "John Doe", "01234567890");

        when(buildingRepository.findById(1L)).thenReturn(Optional.of(building));
        when(conciergeRepository.save(any(ConciergeModel.class))).thenThrow(new DataIntegrityViolationException("constraint"));

        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> conciergeService.createConcierge(request)
        );

        assertEquals("Failed to create concierge due to data integrity violation", exception.getMessage());
        assertInstanceOf(DataIntegrityViolationException.class, exception.getCause());
    }

    @Test
    void updateContactInfoShouldReturnUpdatedConcierge() {
        BuildingModel building = createBuilding(1L);
        ConciergeModel concierge = createConcierge(7L, building);
        ConciergeUpdateDTO request = new ConciergeUpdateDTO("11988887777");

        when(conciergeRepository.findById(7L)).thenReturn(Optional.of(concierge));
        when(conciergeRepository.save(concierge)).thenReturn(concierge);

        ConciergeDTO updatedConcierge = conciergeService.updateContactInfo(7L, request);

        assertEquals(7L, updatedConcierge.id());
        assertEquals(1L, updatedConcierge.buildingId());
        assertEquals("John Doe", updatedConcierge.name());
        assertEquals("11988887777", updatedConcierge.phone());
        verify(conciergeRepository).save(concierge);
    }

    @Test
    void updateContactInfoShouldThrowNotFoundWhenConciergeDoesNotExist() {
        ConciergeUpdateDTO request = new ConciergeUpdateDTO("11988887777");

        when(conciergeRepository.findById(7L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> conciergeService.updateContactInfo(7L, request)
        );

        assertEquals("Concierge not found with id: 7", exception.getMessage());
        verify(conciergeRepository, never()).save(any(ConciergeModel.class));
    }

    @Test
    void deleteConciergeShouldRemoveExistingConcierge() {
        BuildingModel building = createBuilding(1L);
        ConciergeModel concierge = createConcierge(7L, building);

        when(conciergeRepository.findById(7L)).thenReturn(Optional.of(concierge));

        conciergeService.deleteConcierge(7L);

        verify(conciergeRepository).delete(concierge);
    }

    @Test
    void deleteConciergeShouldThrowNotFoundWhenConciergeDoesNotExist() {
        when(conciergeRepository.findById(7L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> conciergeService.deleteConcierge(7L)
        );

        assertEquals("Concierge not found with id: 7", exception.getMessage());
        verify(conciergeRepository, never()).delete(any(ConciergeModel.class));
    }

    @Test
    void getConciergesByBuildingIdShouldReturnMappedList() {
        BuildingModel building = createBuilding(1L);
        ConciergeModel firstConcierge = createConcierge(7L, building);
        ConciergeModel secondConcierge = createConcierge(8L, building);
        ReflectionTestUtils.setField(secondConcierge, "name", "Jane Doe");
        ReflectionTestUtils.setField(secondConcierge, "phone", "11977776666");

        when(buildingRepository.existsById(1L)).thenReturn(true);
        when(conciergeRepository.findByBuildingId(1L)).thenReturn(List.of(firstConcierge, secondConcierge));

        List<ConciergeDTO> concierges = conciergeService.getConciergesByBuildingId(1L);

        assertEquals(2, concierges.size());
        assertEquals(7L, concierges.get(0).id());
        assertEquals(8L, concierges.get(1).id());
        assertEquals("Jane Doe", concierges.get(1).name());
        assertEquals("11977776666", concierges.get(1).phone());
    }

    @Test
    void getConciergesByBuildingIdShouldThrowNotFoundWhenBuildingDoesNotExist() {
        when(buildingRepository.existsById(99L)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> conciergeService.getConciergesByBuildingId(99L)
        );

        assertEquals("Building not found with id: 99", exception.getMessage());
        verify(conciergeRepository, never()).findByBuildingId(99L);
    }

    @Test
    void getConciergesByBuildingIdShouldRejectNullBuildingId() {
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> conciergeService.getConciergesByBuildingId(null)
        );

        assertEquals("Building not found with id: null", exception.getMessage());
        verify(buildingRepository, never()).existsById(any());
        verify(conciergeRepository, never()).findByBuildingId(any());
    }

    private ConciergeModel createConcierge(Long id, BuildingModel building) {
        ConciergeModel concierge = new ConciergeModel(building, "John Doe", "01234567890");
        ReflectionTestUtils.setField(concierge, "id", id);
        return concierge;
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
