package com.condologix.application.building;

import com.condologix.application.exception.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BuildingService {

    private final BuildingRepository buildingRepository;

    public BuildingService(BuildingRepository buildingRepository) {
        this.buildingRepository = buildingRepository;
    }

    public BuildingDTO createBuilding(BuildingCreateDTO buildingDTO) {
        BuildingModel building = new BuildingModel(
            buildingDTO.cnpj(),
            buildingDTO.legalName(),
            buildingDTO.name(),
            buildingDTO.phone(),
            buildingDTO.email(),
            buildingDTO.address(),
            buildingDTO.addressNumber(),
            buildingDTO.postalCode(),
            buildingDTO.neighborhood(),
            buildingDTO.city(),
            buildingDTO.state()
        );

        try {
            BuildingModel savedBuilding = buildingRepository.save(building);
            return toDTO(savedBuilding);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to create building: ", e);
        }
    }

    public BuildingDTO updateBuilding(Long buildingId, BuildingUpdateDTO buildingDTO) {
        BuildingModel building = buildingRepository.findById(buildingId)
            .orElseThrow(() -> new ResourceNotFoundException("Building not found: " + buildingId));
        building.updateContactInfo(buildingDTO.email(), buildingDTO.phone());
        BuildingModel updateBuilding = buildingRepository.save(building);
        return toDTO(updateBuilding);
    }

    public void deleteBuilding(Long buildingId) {
        BuildingModel building = buildingRepository.findById(buildingId)
            .orElseThrow(() -> new ResourceNotFoundException("Building not found: " + buildingId));
        buildingRepository.delete(building);
    }

    @Transactional(readOnly = true)
    public BuildingDTO getBuildingById(Long buildingId) {
        BuildingModel building = buildingRepository.findById(buildingId)
            .orElseThrow(() -> new ResourceNotFoundException("Building not found: " + buildingId));
        return toDTO(building);
    }

    @Transactional(readOnly = true)
    public List<BuildingDTO> getAllBuildings() {
        return buildingRepository.findAll()
            .stream()
            .map(this::toDTO)
            .toList();
    }

    private BuildingDTO toDTO(BuildingModel building) {
        return new BuildingDTO(
            building.getId(),
            building.getCnpj(),
            building.getLegalName(),
            building.getName(),
            building.getPhone(),
            building.getEmail(),
            building.getAddress(),
            building.getAddressNumber(),
            building.getPostalCode(),
            building.getNeighborhood(),
            building.getCity(),
            building.getState()
        );
    }
}
