package com.condologix.application.payment;

import lombok.Data;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import com.condologix.application.building.*;

@Entity
@Table(name = "PAYMENTS")
@Data
public class PaymentModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relationship to Building/Condominium
    @ManyToOne(optional = false)
    @JoinColumn(name = "BUILDING_ID", nullable = false)
    private BuildingModel building;

    @Column(name = "ORDER_ID", nullable = false)
    private Long orderId;

    @Column(name = "AMOUNT", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "PAYMENT_METHOD", nullable = false)
    private PaymentMethod paymentMethod;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDate createdAt;

    @Column(name = "DUE_DATE", nullable = false)
    private LocalDate dueDate;

    @Column(name = "PAID_AT")
    private LocalDate paidAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private PaymentStatus status;

    // Business rule: Calculate interest for overdue payments
        public boolean isOverdue() {
        return LocalDate.now().isAfter(dueDate) && status != PaymentStatus.PAID;
    }

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
