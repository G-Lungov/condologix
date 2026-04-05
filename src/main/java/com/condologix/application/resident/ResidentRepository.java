package com.condologix.application.resident;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ResidentRepository extends JpaRepository<ResidentModel, Long> {

    List<ResidentModel> findByName(String name);
    List<ResidentModel> findByUnitId(Long unitId);
    List<ResidentModel> findByPhone(long phone);
    List<ResidentModel> findByEmail(String email);

}
