package com.condologix.application.concierge;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import org.springframework.validation.annotation.Validated;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/concierges")
@Validated
public class ConciergeController {

    private final ConciergeService conciergeService;

    public ConciergeController(ConciergeService conciergeService) {
        this.conciergeService = conciergeService;
    }

    @PostMapping
    public ResponseEntity<ConciergeDTO> createConcierge(@Valid @RequestBody ConciergeCreateDTO conciergeDTO) {
        ConciergeDTO createdConcierge = conciergeService.createConcierge(conciergeDTO);
        return new ResponseEntity<>(createdConcierge, HttpStatus.CREATED);
    }

    @PutMapping("/{conciergeId}/contact")
    public ResponseEntity<ConciergeDTO> updateContactInfo(
        @PathVariable @Positive Long conciergeId,
        @Valid @RequestBody ConciergeUpdateDTO conciergeDTO
    ) {
        ConciergeDTO updateConcierge = conciergeService.updateContactInfo(conciergeId, conciergeDTO);
        return ResponseEntity.ok(updateConcierge);
    }

    @DeleteMapping("/{conciergeId}")
    public ResponseEntity<Void> deleteConcierge(@PathVariable @Positive Long conciergeId) {
        conciergeService.deleteConcierge(conciergeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/building/{buildingId}")
    public ResponseEntity<List<ConciergeDTO>> getConciergesByBuildingId(@PathVariable @Positive Long buildingId) {
        List<ConciergeDTO> concierges = conciergeService.getConciergesByBuildingId(buildingId);
        return ResponseEntity.ok(concierges);
    }
}
