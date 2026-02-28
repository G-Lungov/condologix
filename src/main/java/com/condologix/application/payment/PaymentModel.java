package com.condologix.application.payment;

import com.condologix.application.building.*;
import lombok.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "payments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Fields - Start
    // Relationship to Building/Condominium
    @ManyToOne(optional = false)
    @JoinColumn(name = "BUILDING_ID", nullable = false)
    private BuildingModel building;

    @ManyToOne(optional = false)
    private PaymentBillingPolicy billingPolicy;

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

    @Column(name ="INTEREST_AMOUNT", precision = 15, scale = 2)
    private BigDecimal interestAmount;

    @Column(name = "TOTAL_AMOUNT", precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "PAID_AT")
    private LocalDate paidAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private PaymentStatus status;
    // Fields - End

    // Constructors - Start
    public PaymentModel(BuildingModel building, Long orderId, BigDecimal amount, PaymentMethod paymentMethod, LocalDate dueDate) {
        if (building == null) {
            throw new IllegalArgumentException("Building cannot be null");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (dueDate == null || dueDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Due date must be in the future");
        }
        this.building = building;
        this.orderId = orderId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.createdAt = LocalDate.now();
        this.dueDate = dueDate;
        this.status = PaymentStatus.PENDING;
    }
    // Constructors - End

    // Business rules - Start
    public void markAsPaid() {
        if (status != PaymentStatus.PENDING) {
            throw new IllegalArgumentException("Payment is already paid, only pending payments can be paid");
        }
        BigDecimal interest = billingPolicy.calculateInterest(this.amount, this.dueDate);
        this.interestAmount = interest;
        this.totalAmount = this.amount.add(interest);
        this.status = PaymentStatus.PAID;
        this.paidAt = LocalDate.now();
    }

    public boolean isOverdue() {
        return LocalDate.now().isAfter(dueDate) && status != PaymentStatus.PAID;
    }
    public BigDecimal calculateInterest() {
        return billingPolicy.calculateInterest(this.amount, this.dueDate);
    }
    // Business rules - End

}
