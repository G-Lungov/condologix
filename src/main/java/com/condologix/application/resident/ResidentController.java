package com.condologix.application.resident;

import java.util.List;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/residents")
public class ResidentController {

    private final ResidentService residentService;

    public residentController(ResidentService residentService) {
        this.residentService = residentService;
    }

    @PostMapping("/api/residents")
    public ResidentModel createResident(@RequestBody ResidentModel resident) {
        return residentService.createResident(resident);
    }

    @PutMapping("/api/residents/{residentId}")
    public ResidentModel updateResident(@PathVariable Long residentId, @RequestParam String email, @RequestParam Long phone) {
        return residentService.updateResidentModel(residentId, email, phone);
    }

    @DeleteMapping("/api/residents/{residentId}")
    public ResidentModel deleteResident(@PathVariable Long residentId) {
        return residentService.deleteResident(residentId);
    }

    @GetMapping("/api/residents/unit")
    public List<ResidentModel> getResidentsByUnit(@RequestParam Long buildingId, String block, short number) {
        return residentService.getResidentByUnit(buildingId, block, number);
    }

}
