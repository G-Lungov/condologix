package com.condologix.application.payment;

import com.condologix.application.building.BuildingModel;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
    name = "payments",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "UK_BUILDING_BILLING_PERIOD",
            columnNames = {"BUILDING_ID", "BILLING_PERIOD"}
        )
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Core Relationships

    @ManyToOne(optional = false)
    @JoinColumn(name = "BUILDING_ID", nullable = false)
    private BuildingModel building;

    // Billing Identification

    @Column(name = "BILLING_PERIOD", nullable = false, length = 7)
    private String billingPeriod; // Format: YYYY-MM

    // Financial Data

    @Column(name = "AMOUNT", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "INTEREST_RATE", nullable = false, precision = 5, scale = 4)
    private BigDecimal interestRate;

    @Column(name = "INTEREST_AMOUNT", precision = 15, scale = 2)
    private BigDecimal interestAmount;

    @Column(name = "TOTAL_AMOUNT", precision = 15, scale = 2)
    private BigDecimal totalAmount;

    // Dates

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDate createdAt;

    @Column(name = "DUE_DATE", nullable = false)
    private LocalDate dueDate;

    @Column(name = "PAID_AT")
    private LocalDate paidAt;

    // Status

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private PaymentStatus status;

    // Constructor (Factory)

    public PaymentModel(
        BuildingModel building,
        String billingPeriod,
        BigDecimal amount,
        BigDecimal interestRate,
        LocalDate createdAt,
        LocalDate dueDate
    ) {
        if (building == null) {
            throw new IllegalArgumentException("Building cannot be null");
        }
        if (billingPeriod == null || billingPeriod.isBlank()) {
            throw new IllegalArgumentException("Billing period cannot be null");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (interestRate == null || interestRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Interest rate cannot be negative");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("Created date cannot be null");
        }
        if (dueDate == null || dueDate.isBefore(createdAt)) {
            throw new IllegalArgumentException("Due date must be after creation date");
        }

        this.building = building;
        this.billingPeriod = billingPeriod;
        this.amount = amount;
        this.interestRate = interestRate;
        this.createdAt = createdAt;
        this.dueDate = dueDate;
        this.status = PaymentStatus.PENDING;
    }

    // Business Rules

    public void markAsPaid(LocalDate paymentDate) {
        if (status != PaymentStatus.PENDING) {
            throw new IllegalStateException("Only pending payments can be paid");
        }

        if (paymentDate == null) {
            throw new IllegalArgumentException("Payment date cannot be null");
        }

        BigDecimal interest = calculateInterest(paymentDate);
        this.interestAmount = interest;
        this.totalAmount = this.amount.add(interest);
        this.paidAt = paymentDate;
        this.status = PaymentStatus.PAID;
    }

    public boolean isOverdue(LocalDate today) {
        return status == PaymentStatus.PENDING && today.isAfter(dueDate);
    }

    public BigDecimal calculateInterest(LocalDate paymentDate) {
        if (!paymentDate.isAfter(dueDate)) {
            return BigDecimal.ZERO;
        }

        long daysLate = java.time.temporal.ChronoUnit.DAYS.between(dueDate, paymentDate);

        return amount
                .multiply(interestRate)
                .multiply(BigDecimal.valueOf(daysLate));
    }
}
