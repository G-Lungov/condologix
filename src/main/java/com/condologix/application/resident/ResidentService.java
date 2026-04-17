package com.condologix.application.resident;

import com.condologix.application.unit.UnitModel;
import com.condologix.application.unit.UnitRepository;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;

@Service
@Transactional
public class ResidentService {

    private final ResidentRepository residentRepository;
    private final UnitRepository unitRepository;

    public ResidentService(ResidentRepository residentRepository, UnitRepository unitRepository) {
        this.residentRepository = residentRepository;
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

    public List<ResidentModel> getResidentsByUnit (Long buildingId, String block, short number) {
        if (block == null) throw new IllegalArgumentException("Block cannot be null");
        String normalizedBlock = block.trim().toUpperCase();
        if (normalizedBlock.isBlank()) throw new IllegalArgumentException("Block cannot be blank");
        UnitModel unit = unitRepository.findByBuildingIdAndBlockAndNumber(buildingId, normalizedBlock, number)
            .orElseThrow(() -> new IllegalArgumentException("Unit not found: " + buildingId + ", " + number + ", " + normalizedBlock));
        return residentRepository.findByUnitId(unit.getId());
    }
}
