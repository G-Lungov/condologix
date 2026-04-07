package com.condologix.application.resident;

import com.condologix.application.building.BuildingModel;
import com.condologix.application.unit.UnitModel;
import com.condologix.application.unit.UnitService;
import com.condologix.application.unit.UnitRepository;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;

@Service
@Transactional
public class ResidentService {

    private final ResidentRepository residentRepository;
    private final UnitService unitService;
    private final UnitRepository unitRepository;

    public ResidentService(ResidentRepository residentRepository, UnitService unitService, UnitRepository unitRepository) {
        this.residentRepository = residentRepository;
        this.unitService = unitService;
        this.unitRepository = unitRepository;
    }

    public ResidentModel createResident(ResidentModel resident) {
        if (resident == null) {
            throw new IllegalArgumentException("Resident cannot be null");
        }
        try {
            return residentRepository.save(resident);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Failed to create resident due to data integrity violation: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while creating resident: " + e.getMessage(), e);
        }
    }

    public ResidentModel updateResidentModel(Long residentId, String email, long phone) {
        ResidentModel resident = residentRepository.findById(residentId)
            .orElseThrow(() -> new IllegalArgumentException("Resident not found: " + residentId));
        resident.updateContactInfo(email, phone);
        return residentRepository.save(resident);
    }

    public void deleteResident(Long residentId) {
        ResidentModel resident = residentRepository.findById(residentId)
            .orElseThrow(() -> new IllegalArgumentException("Resident not found: " + residentId));
        residentRepository.delete(resident);
    }

    public List<ResidentModel> getResidentsByUnit (BuildingModel building, String block, short number) {
        String normalizedBlock = unitService.normalizedBlock(block);
        UnitModel unit = unitRepository.findByBuildingIdAndBlockAndNumber(building.getId(), normalizedBlock, number)
            .orElseThrow(() -> new IllegalArgumentException("Unit not found: " + building.getId() + ", " + number + ", " + normalizedBlock));
        return residentRepository.findByUnitId(unit.getId());
    }
}
