package com.condologix.application.payment;

import com.condologix.application.building.BuildingModel;
import com.condologix.application.building.BuildingRepository;
import com.condologix.application.exception.*;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PaymentGenerationService {

    private final BuildingRepository buildingRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentCyclePolicy paymentCyclePolicy;
    private final PaymentService paymentService;

    public PaymentGenerationService(
        BuildingRepository buildingRepository,
        PaymentRepository paymentRepository,
        PaymentCyclePolicy paymentCyclePolicy,
        PaymentService paymentService
    ) {
        this.buildingRepository = buildingRepository;
        this.paymentRepository = paymentRepository;
        this.paymentCyclePolicy = paymentCyclePolicy;
        this.paymentService = paymentService;
    }

    public PaymentGenerationResult generateMonthlyPayments(LocalDate generationDate) {
        String billingPeriod = paymentCyclePolicy.calculateBillingPeriod(generationDate);
        LocalDate dueDate = paymentCyclePolicy.calculateDueDate(generationDate);

        List<BuildingModel> buildings = buildingRepository.findAll();

        int created = 0;
        int skipped = 0;
        int failed = 0;

        for (BuildingModel building : buildings) {
            if (paymentRepository.existsByBuildingIdAndBillingPeriod(building.getId(), billingPeriod)) {
                skipped++;
                continue;
            }
            PaymentCreateDTO paymentDTO = new PaymentCreateDTO(
                building.getId(),
                billingPeriod,
                generationDate,
                dueDate
            );
            try {
                paymentService.createPayment(paymentDTO);
                created++;
            } catch (ResourceNotFoundException | IllegalStateException ex) {
                failed++;
            }
        }
        return new PaymentGenerationResult(created, skipped, failed);
    }
}
