package com.condologix.application.concierge;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ConciergeRepository extends JpaRepository<ConciergeModel, Long> {

    List<ConciergeModel> findByBuildingId(Long buildingId);
}
