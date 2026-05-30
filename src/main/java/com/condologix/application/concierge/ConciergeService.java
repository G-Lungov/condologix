package com.condologix.application.concierge;

import com.condologix.application.exception.*;
import com.condologix.application.building.BuildingModel;
import com.condologix.application.building.BuildingRepository;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ConciergeService {

    private final ConciergeRepository conciergeRepository;
    private final BuildingRepository buildingRepository;

    public ConciergeService(ConciergeRepository conciergeRepository, BuildingRepository buildingRepository) {
        this.conciergeRepository = conciergeRepository;
        this.buildingRepository = buildingRepository;
    }

    public ConciergeDTO createConcierge(ConciergeCreateDTO conciergeDTO) {
        BuildingModel building = buildingRepository.findById(conciergeDTO.buildingId())
            .orElseThrow(() -> new ResourceNotFoundException("Building not found with id: " + conciergeDTO.buildingId()));

        ConciergeModel concierge = new ConciergeModel(
            building,
            conciergeDTO.name(),
            conciergeDTO.phone()
        );

        try {
            ConciergeModel savedConcierge = conciergeRepository.save(concierge);
            return toDTO(savedConcierge);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("Failed to create concierge due to data integrity violation", e);
        }
    }

    public ConciergeDTO updateContactInfo(Long conciergeId, ConciergeUpdateDTO conciergeDTO) {
        ConciergeModel concierge = conciergeRepository.findById(conciergeId)
            .orElseThrow(() -> new ResourceNotFoundException("Concierge not found with id: " + conciergeId));
        concierge.updateContactInfo(conciergeDTO.phone());
        ConciergeModel updateContactInfo = conciergeRepository.save(concierge);
        return toDTO(updateContactInfo);
    }

    public void deleteConcierge(Long conciergeId) {
        ConciergeModel concierge = conciergeRepository.findById(conciergeId)
            .orElseThrow(() -> new ResourceNotFoundException("Concierge not found with id: " + conciergeId));
        conciergeRepository.delete(concierge);
    }

    @Transactional(readOnly = true)
    public List<ConciergeDTO> getConciergesByBuildingId(Long buildingId) {
        if (buildingId == null || !buildingRepository.existsById(buildingId)) {
            throw new ResourceNotFoundException("Building not found with id: " + buildingId);
        }
        return conciergeRepository.findByBuildingId(buildingId)
            .stream()
            .map(this::toDTO)
            .toList();
    }

    private ConciergeDTO toDTO(ConciergeModel concierge) {
        return new ConciergeDTO(
            concierge.getId(),
            concierge.getBuilding().getId(),
            concierge.getName(),
            concierge.getPhone()
        );
    }
}
