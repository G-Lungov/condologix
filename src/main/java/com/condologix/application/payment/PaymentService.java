package com.condologix.application.payment;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public PaymentModel createPayment(PaymentModel payment) {
        try {
            return paymentRepository.save(payment);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("Payment already exists for this building and billing period");
        }
    }

    public PaymentModel payPayment(Long paymentId, LocalDate paymentDate) {
        PaymentModel payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentId));

        payment.markAsPaid(paymentDate);

        return paymentRepository.save(payment);
    }

    public List<PaymentModel> getPaymentsDueOn(LocalDate date) {
        return paymentRepository.findByDueDate(date);
    }

    public List<PaymentModel> getOverduePayments(LocalDate today) {
        return paymentRepository.findByDueDateLessThanEqualAndStatus(
                today,
                PaymentStatus.PENDING
        );
    }
}
