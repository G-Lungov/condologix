package com.condologix.application.building;

public record BuildingDTO(
    Long id,
    String cnpj,
    String legalName,
    String name,
    String phone,
    String email,
    String address,
    Integer addressNumber,
    String postalCode,
    String neighborhood,
    String city,
    String state
) {

}
