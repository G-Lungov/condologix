package com.condologix.application.payment;

import com.condologix.application.building.BuildingModel;
import com.condologix.application.building.BuildingRepository;
import com.condologix.application.exception.ResourceNotFoundException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BuildingRepository buildingRepository;
    private final PaymentBillingPolicyRepository paymentBillingPolicyRepository;

    public PaymentService(
        PaymentRepository paymentRepository,
        BuildingRepository buildingRepository,
        PaymentBillingPolicyRepository paymentBillingPolicyRepository
    ) {
        this.paymentRepository = paymentRepository;
        this.buildingRepository = buildingRepository;
        this.paymentBillingPolicyRepository = paymentBillingPolicyRepository;
    }

    public PaymentDTO createPayment(PaymentCreateDTO paymentDTO) {
        BuildingModel building = buildingRepository.findById(paymentDTO.buildingId())
            .orElseThrow(() -> new ResourceNotFoundException("Building not found with id: " + paymentDTO.buildingId()));

        if (paymentRepository.existsByBuildingIdAndBillingPeriod(paymentDTO.buildingId(), paymentDTO.billingPeriod())) {
            throw new IllegalStateException("Payment already exists for this building and billing period");
        }

        PaymentBillingPolicy billingPolicy = paymentBillingPolicyRepository
            .findTopByBuildingIdAndValidFromLessThanEqualOrderByValidFromDesc(
                paymentDTO.buildingId(),
                paymentDTO.createdAt()
            )
            .orElseThrow(() -> new ResourceNotFoundException(
                "Payment billing policy not found for building id: " + paymentDTO.buildingId()
            ));

        PaymentModel payment = new PaymentModel(
            building,
            paymentDTO.billingPeriod(),
            billingPolicy.getMonthlyFee(),
            billingPolicy.getDailyInterestRate(),
            billingPolicy.getGraceDays(),
            paymentDTO.createdAt(),
            paymentDTO.dueDate()
        );

        try {
            PaymentModel savedPayment = paymentRepository.save(payment);
            return toDTO(savedPayment);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("Payment already exists for this building and billing period", e);
        }
    }

    public PaymentDTO markPaymentAsPaid(Long paymentId, PaymentPayDTO paymentDTO) {
        PaymentModel payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));

        payment.markAsPaid(paymentDTO.paymentDate());

        PaymentModel paidPayment = paymentRepository.save(payment);
        return toDTO(paidPayment);
    }

    @Transactional(readOnly = true)
    public List<PaymentDTO> getPaymentsByBuilding(Long buildingId) {
        if (buildingId == null || !buildingRepository.existsById(buildingId)) {
            throw new ResourceNotFoundException("Building not found with id: " + buildingId);
        }

        return paymentRepository.findByBuildingId(buildingId)
            .stream()
            .map(this::toDTO)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<PaymentDTO> getPaymentsDueOn(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Due date cannot be null");
        }

        return paymentRepository.findByDueDate(date)
            .stream()
            .map(this::toDTO)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<PaymentDTO> getOverduePayments(LocalDate today) {
        if (today == null) {
            throw new IllegalArgumentException("Today cannot be null");
        }

        return paymentRepository.findByDueDateBeforeAndStatus(
                today,
                PaymentStatus.PENDING
            )
            .stream()
            .map(this::toDTO)
            .toList();
    }

    private PaymentDTO toDTO(PaymentModel payment) {
        return new PaymentDTO(
            payment.getId(),
            payment.getBuilding().getId(),
            payment.getBillingPeriod(),
            payment.getAmount(),
            payment.getInterestRate(),
            payment.getGraceDays(),
            payment.getInterestAmount(),
            payment.getTotalAmount(),
            payment.getCreatedAt(),
            payment.getDueDate(),
            payment.getPaidAt(),
            payment.getStatus()
        );
    }
}
