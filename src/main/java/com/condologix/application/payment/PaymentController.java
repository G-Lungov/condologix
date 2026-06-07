package com.condologix.application.payment;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@Validated
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentDTO> createPayment(@Valid @RequestBody PaymentCreateDTO paymentDTO) {
        PaymentDTO createdPayment = paymentService.createPayment(paymentDTO);
        return new ResponseEntity<>(createdPayment, HttpStatus.CREATED);
    }

    @PutMapping("/{paymentId}/pay")
    public ResponseEntity<PaymentDTO> markPaymentAsPaid(
        @PathVariable @Positive Long paymentId,
        @Valid @RequestBody PaymentPayDTO paymentDTO
    ) {
        PaymentDTO paidPayment = paymentService.markPaymentAsPaid(paymentId, paymentDTO);
        return ResponseEntity.ok(paidPayment);
    }

    @GetMapping("/building/{buildingId}")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByBuilding(@PathVariable @Positive Long buildingId) {
        List<PaymentDTO> payments = paymentService.getPaymentsByBuilding(buildingId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/due/{date}")
    public ResponseEntity<List<PaymentDTO>> getPaymentsDueOn(
        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<PaymentDTO> payments = paymentService.getPaymentsDueOn(date);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<PaymentDTO>> getOverduePayments(
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate today
    ) {
        LocalDate referenceDate = today == null ? LocalDate.now() : today;
        List<PaymentDTO> payments = paymentService.getOverduePayments(referenceDate);
        return ResponseEntity.ok(payments);
    }
}
