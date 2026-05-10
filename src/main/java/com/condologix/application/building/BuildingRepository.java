package com.condologix.application.building;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BuildingRepository extends JpaRepository<BuildingModel, Long> {

    List<BuildingModel> findByLegalName(String legalName);
    List<BuildingModel> findByName(String name);
    List<BuildingModel> findByPhone(String phone);
    List<BuildingModel> findByEmail(String email);
    List<BuildingModel> findByAddress(String address);
    List<BuildingModel> findByPostalCode(String postalCode);
    List<BuildingModel> findByNeighborhood(String neighborhood);
    List<BuildingModel> findByCity(String city);
    List<BuildingModel> findByState(String state);
    Optional<BuildingModel> findByCnpj(String cnpj);
    boolean existsByCnpj(String cnpj);
}
