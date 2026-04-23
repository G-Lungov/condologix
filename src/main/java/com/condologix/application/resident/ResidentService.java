package com.condologix.application.resident;

import com.condologix.application.exception.*;
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

    public ResidentDTO createResident(ResidentCreateDTO residentDTO) {
        UnitModel unit = unitRepository.findById(residentDTO.unitId())
            .orElseThrow(() -> new ResourceNotFoundException("Unit not found: " + residentDTO.unitId()));

        ResidentModel resident = new ResidentModel(
            unit,
            residentDTO.name(),
            residentDTO.email(),
            residentDTO.phone()
        );

        try {
            ResidentModel savedResident = residentRepository.save(resident);
            return toDTO(savedResident);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("Failed to create resident due to data integrity violation", e);
        }
    }

    public ResidentDTO updateResident(Long residentId, ResidentUpdateDTO residentDTO) {
        ResidentModel resident = residentRepository.findById(residentId)
            .orElseThrow(() -> new ResourceNotFoundException("Resident not found: " + residentId));
        resident.updateContactInfo(residentDTO.email(), residentDTO.phone());
        ResidentModel updatedResident = residentRepository.save(resident);
        return toDTO(updatedResident);
    }

    public void deleteResident(Long residentId) {
        ResidentModel resident = residentRepository.findById(residentId)
            .orElseThrow(() -> new ResourceNotFoundException("Resident not found: " + residentId));
        residentRepository.delete(resident);
    }

    @Transactional(readOnly = true)
    public List<ResidentDTO> getResidentsByUnit(Long unitId) {
        if (unitId == null) {
            throw new IllegalArgumentException("Unit ID cannot be null");
        }
        return residentRepository.findByUnitId(unitId)
            .stream()
            .map(this::toDTO)
            .toList();
    }

    private ResidentDTO toDTO(ResidentModel resident) {
        return new ResidentDTO(
            resident.getId(),
            resident.getUnit().getId(),
            resident.getName(),
            resident.getEmail(),
            resident.getPhone()
        );
    }
}
