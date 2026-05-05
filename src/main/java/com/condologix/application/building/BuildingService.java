package com.condologix.application.building;

import com.condologix.application.exception.*;

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
