package com.condologix.application.concierge;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ConciergeRepository extends JpaRepository<ConciergeModel, Long> {

    List<ConciergeModel> findByBuildingId(Long buildingId);
    List<ConciergeModel> findByName(String name);
    List<ConciergeModel> findByPhone(String phone);
    Optional<ConciergeModel> findById(Long id);
}
