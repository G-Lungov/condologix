package com.condologix.application.payment;

import com.condologix.application.building.BuildingModel;
import com.condologix.application.building.BuildingRepository;
import com.condologix.application.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PaymentServiceTest {

    private final PaymentRepository paymentRepository = mock(PaymentRepository.class);
    private final BuildingRepository buildingRepository = mock(BuildingRepository.class);
    private final PaymentBillingPolicyRepository paymentBillingPolicyRepository = mock(PaymentBillingPolicyRepository.class);
    private final PaymentService paymentService = new PaymentService(
        paymentRepository,
        buildingRepository,
        paymentBillingPolicyRepository
    );

    @Test
    void createPaymentShouldReturnCreatedPaymentUsingBillingPolicy() {
        BuildingModel building = createBuilding(1L);
        PaymentBillingPolicy policy = createPolicy(building);
        PaymentCreateDTO paymentDTO = createPaymentCreateDTO();

        when(buildingRepository.findById(1L)).thenReturn(Optional.of(building));
        when(paymentRepository.existsByBuildingIdAndBillingPeriod(1L, "2026-05")).thenReturn(false);
        when(paymentBillingPolicyRepository.findTopByBuildingIdAndValidFromLessThanEqualOrderByValidFromDesc(
            1L,
            LocalDate.of(2026, 5, 1)
        )).thenReturn(Optional.of(policy));
        when(paymentRepository.save(any(PaymentModel.class))).thenAnswer(invocation -> {
            PaymentModel payment = invocation.getArgument(0);
            ReflectionTestUtils.setField(payment, "id", 20L);
            return payment;
        });

        PaymentDTO createdPayment = paymentService.createPayment(paymentDTO);

        assertEquals(20L, createdPayment.id());
        assertEquals(1L, createdPayment.buildingId());
        assertEquals("2026-05", createdPayment.billingPeriod());
        assertEquals(0, new BigDecimal("250.00").compareTo(createdPayment.amount()));
        assertEquals(0, new BigDecimal("0.01").compareTo(createdPayment.interestRate()));
        assertEquals(2, createdPayment.graceDays());
        assertEquals(PaymentStatus.PENDING, createdPayment.status());
        verify(paymentRepository).save(any(PaymentModel.class));
    }

    @Test
    void createPaymentShouldThrowNotFoundWhenBuildingDoesNotExist() {
        when(buildingRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> paymentService.createPayment(createPaymentCreateDTO())
        );

        assertEquals("Building not found with id: 1", exception.getMessage());
        verify(paymentRepository, never()).save(any(PaymentModel.class));
    }

    @Test
    void createPaymentShouldThrowConflictWhenPaymentAlreadyExistsForPeriod() {
        BuildingModel building = createBuilding(1L);

        when(buildingRepository.findById(1L)).thenReturn(Optional.of(building));
        when(paymentRepository.existsByBuildingIdAndBillingPeriod(1L, "2026-05")).thenReturn(true);

        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> paymentService.createPayment(createPaymentCreateDTO())
        );

        assertEquals("Payment already exists for this building and billing period", exception.getMessage());
        verify(paymentBillingPolicyRepository, never())
            .findTopByBuildingIdAndValidFromLessThanEqualOrderByValidFromDesc(1L, LocalDate.of(2026, 5, 1));
        verify(paymentRepository, never()).save(any(PaymentModel.class));
    }

    @Test
    void createPaymentShouldThrowNotFoundWhenBillingPolicyDoesNotExist() {
        BuildingModel building = createBuilding(1L);

        when(buildingRepository.findById(1L)).thenReturn(Optional.of(building));
        when(paymentRepository.existsByBuildingIdAndBillingPeriod(1L, "2026-05")).thenReturn(false);
        when(paymentBillingPolicyRepository.findTopByBuildingIdAndValidFromLessThanEqualOrderByValidFromDesc(
            1L,
            LocalDate.of(2026, 5, 1)
        )).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> paymentService.createPayment(createPaymentCreateDTO())
        );

        assertEquals("Payment billing policy not found for building id: 1", exception.getMessage());
        verify(paymentRepository, never()).save(any(PaymentModel.class));
    }

    @Test
    void createPaymentShouldWrapDataIntegrityViolation() {
        BuildingModel building = createBuilding(1L);
        PaymentBillingPolicy policy = createPolicy(building);

        when(buildingRepository.findById(1L)).thenReturn(Optional.of(building));
        when(paymentRepository.existsByBuildingIdAndBillingPeriod(1L, "2026-05")).thenReturn(false);
        when(paymentBillingPolicyRepository.findTopByBuildingIdAndValidFromLessThanEqualOrderByValidFromDesc(
            1L,
            LocalDate.of(2026, 5, 1)
        )).thenReturn(Optional.of(policy));
        when(paymentRepository.save(any(PaymentModel.class)))
            .thenThrow(new DataIntegrityViolationException("constraint"));

        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> paymentService.createPayment(createPaymentCreateDTO())
        );

        assertEquals("Payment already exists for this building and billing period", exception.getMessage());
    }

    @Test
    void markPaymentAsPaidShouldReturnPaidPayment() {
        PaymentModel payment = createPayment(20L);

        when(paymentRepository.findById(20L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(payment)).thenReturn(payment);

        PaymentDTO paidPayment = paymentService.markPaymentAsPaid(
            20L,
            new PaymentPayDTO(LocalDate.of(2026, 5, 13))
        );

        assertEquals(20L, paidPayment.id());
        assertEquals(PaymentStatus.PAID, paidPayment.status());
        assertEquals(LocalDate.of(2026, 5, 13), paidPayment.paidAt());
        assertEquals(0, new BigDecimal("2.50").compareTo(paidPayment.interestAmount()));
        assertEquals(0, new BigDecimal("252.50").compareTo(paidPayment.totalAmount()));
        verify(paymentRepository).save(payment);
    }

    @Test
    void markPaymentAsPaidShouldThrowNotFoundWhenPaymentDoesNotExist() {
        when(paymentRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> paymentService.markPaymentAsPaid(99L, new PaymentPayDTO(LocalDate.of(2026, 5, 10)))
        );

        assertEquals("Payment not found with id: 99", exception.getMessage());
    }

    @Test
    void getPaymentsByBuildingShouldReturnPayments() {
        PaymentModel payment = createPayment(20L);

        when(buildingRepository.existsById(1L)).thenReturn(true);
        when(paymentRepository.findByBuildingId(1L)).thenReturn(List.of(payment));

        List<PaymentDTO> payments = paymentService.getPaymentsByBuilding(1L);

        assertEquals(1, payments.size());
        assertEquals(20L, payments.get(0).id());
        assertEquals(1L, payments.get(0).buildingId());
    }

    @Test
    void getPaymentsByBuildingShouldThrowNotFoundWhenBuildingDoesNotExist() {
        when(buildingRepository.existsById(99L)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> paymentService.getPaymentsByBuilding(99L)
        );

        assertEquals("Building not found with id: 99", exception.getMessage());
    }

    @Test
    void getPaymentsDueOnShouldDelegateToRepository() {
        LocalDate dueDate = LocalDate.of(2026, 5, 10);
        PaymentModel payment = createPayment(20L);

        when(paymentRepository.findByDueDate(dueDate)).thenReturn(List.of(payment));

        List<PaymentDTO> payments = paymentService.getPaymentsDueOn(dueDate);

        assertEquals(1, payments.size());
        assertEquals(20L, payments.get(0).id());
    }

    @Test
    void getOverduePaymentsShouldQueryPendingPaymentsDueBeforeToday() {
        LocalDate today = LocalDate.of(2026, 5, 20);
        PaymentModel payment = createPayment(20L);

        when(paymentRepository.findByDueDateBeforeAndStatus(today, PaymentStatus.PENDING))
            .thenReturn(List.of(payment));

        List<PaymentDTO> payments = paymentService.getOverduePayments(today);

        assertEquals(1, payments.size());
        assertEquals(PaymentStatus.PENDING, payments.get(0).status());
    }

    private PaymentCreateDTO createPaymentCreateDTO() {
        return new PaymentCreateDTO(
            1L,
            "2026-05",
            LocalDate.of(2026, 5, 1),
            LocalDate.of(2026, 5, 10)
        );
    }

    private PaymentModel createPayment(Long id) {
        PaymentModel payment = new PaymentModel(
            createBuilding(1L),
            "2026-05",
            new BigDecimal("250.00"),
            new BigDecimal("0.01"),
            2,
            LocalDate.of(2026, 5, 1),
            LocalDate.of(2026, 5, 10)
        );
        ReflectionTestUtils.setField(payment, "id", id);
        return payment;
    }

    private PaymentBillingPolicy createPolicy(BuildingModel building) {
        return new PaymentBillingPolicy(
            building,
            new BigDecimal("250.00"),
            new BigDecimal("0.01"),
            2,
            LocalDate.of(2026, 5, 1)
        );
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
