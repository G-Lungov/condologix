package com.condologix.application.payment;

import com.condologix.application.building.BuildingModel;
import com.condologix.application.building.BuildingRepository;
import com.condologix.application.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PaymentGenerationServiceTest {

    private final BuildingRepository buildingRepository = mock(BuildingRepository.class);
    private final PaymentRepository paymentRepository = mock(PaymentRepository.class);
    private final PaymentCyclePolicy paymentCyclePolicy = mock(PaymentCyclePolicy.class);
    private final PaymentService paymentService = mock(PaymentService.class);
    private final PaymentGenerationService paymentGenerationService = new PaymentGenerationService(
        buildingRepository,
        paymentRepository,
        paymentCyclePolicy,
        paymentService
    );

    @Test
    void generateMonthlyPaymentsShouldCountCreatedSkippedAndFailedPayments() {
        LocalDate generationDate = LocalDate.of(2026, 6, 30);
        LocalDate dueDate = LocalDate.of(2026, 7, 15);
        BuildingModel firstBuilding = createBuilding(1L);
        BuildingModel secondBuilding = createBuilding(2L);
        BuildingModel thirdBuilding = createBuilding(3L);

        when(paymentCyclePolicy.calculateBillingPeriod(generationDate)).thenReturn("2026-07");
        when(paymentCyclePolicy.calculateDueDate(generationDate)).thenReturn(dueDate);
        when(buildingRepository.findAll()).thenReturn(List.of(firstBuilding, secondBuilding, thirdBuilding));
        when(paymentRepository.existsByBuildingIdAndBillingPeriod(1L, "2026-07")).thenReturn(false);
        when(paymentRepository.existsByBuildingIdAndBillingPeriod(2L, "2026-07")).thenReturn(true);
        when(paymentRepository.existsByBuildingIdAndBillingPeriod(3L, "2026-07")).thenReturn(false);
        when(paymentService.createPayment(argThat(dto -> dto != null && dto.buildingId().equals(3L))))
            .thenThrow(new ResourceNotFoundException("Payment billing policy not found for building id: 3"));

        PaymentGenerationResult result = paymentGenerationService.generateMonthlyPayments(generationDate);

        assertEquals(1, result.created());
        assertEquals(1, result.skipped());
        assertEquals(1, result.failed());

        ArgumentCaptor<PaymentCreateDTO> captor = ArgumentCaptor.forClass(PaymentCreateDTO.class);
        verify(paymentService, times(2)).createPayment(captor.capture());

        assertEquals(1L, captor.getAllValues().get(0).buildingId());
        assertEquals(3L, captor.getAllValues().get(1).buildingId());
        assertEquals("2026-07", captor.getAllValues().get(0).billingPeriod());
        assertEquals(generationDate, captor.getAllValues().get(0).createdAt());
        assertEquals(dueDate, captor.getAllValues().get(0).dueDate());
    }

    @Test
    void generateMonthlyPaymentsShouldReturnZeroCountersWhenThereAreNoBuildings() {
        LocalDate generationDate = LocalDate.of(2026, 6, 30);

        when(paymentCyclePolicy.calculateBillingPeriod(generationDate)).thenReturn("2026-07");
        when(paymentCyclePolicy.calculateDueDate(generationDate)).thenReturn(LocalDate.of(2026, 7, 15));
        when(buildingRepository.findAll()).thenReturn(List.of());

        PaymentGenerationResult result = paymentGenerationService.generateMonthlyPayments(generationDate);

        assertEquals(0, result.created());
        assertEquals(0, result.skipped());
        assertEquals(0, result.failed());
        verify(paymentService, never()).createPayment(argThat(dto -> true));
    }

    private BuildingModel createBuilding(Long id) {
        BuildingModel building = new BuildingModel(
            "12345678000199",
            "Condo One LTDA",
            "Condo One",
            "11999999999",
            "condo@example.com",
            "Main Street",
            123,
            "12345678",
            "Downtown",
            "Sao Paulo",
            "SP"
        );
        ReflectionTestUtils.setField(building, "id", id);
        return building;
    }
}
