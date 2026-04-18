package com.condologix.application.resident;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import org.springframework.validation.annotation.Validated;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/residents")
@Validated
public class ResidentController {

    private final ResidentService residentService;

    public ResidentController(ResidentService residentService) {
        this.residentService = residentService;
    }

    @PostMapping
    public ResponseEntity<ResidentDTO> createResident(@Valid @RequestBody ResidentCreateDTO residentDTO) {
        ResidentDTO createdResident = residentService.createResident(residentDTO);
        return new ResponseEntity<>(createdResident, HttpStatus.CREATED);
    }

    @PutMapping("/{residentId}")
    public ResponseEntity<ResidentDTO> updateResident(
            @PathVariable @Positive Long residentId,
            @Valid @RequestBody ResidentUpdateDTO residentDTO) {
        ResidentDTO updatedResident = residentService.updateResident(residentId, residentDTO);
        return ResponseEntity.ok(updatedResident);
    }

    @DeleteMapping("/{residentId}")
    public ResponseEntity<Void> deleteResident(@PathVariable @Positive Long residentId) {
        residentService.deleteResident(residentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/unit/{unitId}")
    public ResponseEntity<List<ResidentDTO>> getResidentsByUnit(@PathVariable @Positive Long unitId) {
        List<ResidentDTO> residents = residentService.getResidentsByUnit(unitId);
        return ResponseEntity.ok(residents);
    }

}
