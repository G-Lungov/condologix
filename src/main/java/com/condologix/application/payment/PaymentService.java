package com.condologix.application.payment;

import java.util.List;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PaymentService {
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }
    public PaymentModel createPayment(PaymentModel payment) {
        return paymentRepository.save(payment);
    }
    public PaymentModel payPayment(Long orderId) {
        PaymentModel payment = paymentRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Payment not found for orderId: " + orderId));
        payment.markAsPaid();
        return paymentRepository.save(payment);
    }
    public List<PaymentModel> getPaymentsDueOn (LocalDate date) {
        return paymentRepository.findByDueDate(date);
    }
    public List<PaymentModel> getOverduePayments (LocalDate date) {
        return paymentRepository.findByDueDateLessThanEqualAndStatus(date, PaymentStatus.PENDING);
    }
}
