package com.condologix.application.payment;

import com.condologix.application.building.BuildingModel;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "payment_billing_policies")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentBillingPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "BUILDING_ID", nullable = false)
    private BuildingModel building;

    @Column(name = "MONTHLY_FEE", nullable = false, precision = 15, scale = 2)
    private BigDecimal monthlyFee;

    @Column(name = "DAILY_INTEREST_RATE", nullable = false, precision = 5, scale = 4)
    private BigDecimal dailyInterestRate;

    @Column(name = "GRACE_DAYS", nullable = false)
    private Integer graceDays;

    @Column(name = "VALID_FROM", nullable = false)
    private LocalDate validFrom;

    public PaymentBillingPolicy(
        BuildingModel building,
        BigDecimal monthlyFee,
        BigDecimal dailyInterestRate,
        Integer graceDays,
        LocalDate validFrom
    ) {
        if (building == null) {
            throw new IllegalArgumentException("Building cannot be null");
        }
        if (monthlyFee == null || monthlyFee.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Monthly fee must be positive");
        }
        if (dailyInterestRate == null || dailyInterestRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Daily interest rate cannot be negative");
        }
        if (graceDays == null || graceDays < 0) {
            throw new IllegalArgumentException("Grace days cannot be negative");
        }
        if (validFrom == null) {
            throw new IllegalArgumentException("Valid from date cannot be null");
        }

        this.building = building;
        this.monthlyFee = monthlyFee;
        this.dailyInterestRate = dailyInterestRate;
        this.graceDays = graceDays;
        this.validFrom = validFrom;
    }

    public BigDecimal calculateInterest(BigDecimal baseAmount, LocalDate dueDate, LocalDate paymentDate) {
        if (baseAmount == null || baseAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Base amount must be positive");
        }
        if (dueDate == null) {
            throw new IllegalArgumentException("Due date cannot be null");
        }
        if (paymentDate == null) {
            throw new IllegalArgumentException("Payment date cannot be null");
        }

        LocalDate interestStartDate = dueDate.plusDays(graceDays);
        if (!paymentDate.isAfter(interestStartDate)) {
            return BigDecimal.ZERO;
        }

        long chargeableDays = ChronoUnit.DAYS.between(interestStartDate, paymentDate);
        return baseAmount
            .multiply(dailyInterestRate)
            .multiply(BigDecimal.valueOf(chargeableDays));
    }

}
