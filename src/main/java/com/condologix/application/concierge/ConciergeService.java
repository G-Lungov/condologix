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

    private ConciergeDTO toDTO(ConciergeModel concierge) {
        return new ConciergeDTO(
            concierge.getId(),
            concierge.getBuilding().getId(),
            concierge.getName(),
            concierge.getPhone()
        );
    }
}
