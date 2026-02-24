package com.condologix.application.payment;


import lombok.Data;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Data
public class PaymentModel {
    private long id;
    private long orderId;
    private BigDecimal amount;
    private String paymentMethod;
    private LocalDate createdAt;
    private LocalDate dueDate;;
    private LocalDate paidAt;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    public BigDecimal calculateInterest(BigDecimal dailyRate) {
        long daysOverdue = ChronoUnit.DAYS.between(dueDate, LocalDate.now());
        if (daysOverdue <=0) {
            return BigDecimal.ZERO;
        } else {
            return amount
                .multiply(dailyRate)
                .multiply(BigDecimal.valueOf(daysOverdue));
        }
    }
}
