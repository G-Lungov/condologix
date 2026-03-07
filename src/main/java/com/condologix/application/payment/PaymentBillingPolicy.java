package com.condologix.application.payment;

import com.condologix.application.building.BuildingModel;
import lombok.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Getter
public class PaymentBillingPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private BuildingModel building;

    private BigDecimal monthlyFee;
    private BigDecimal dailyInterestRate;
    private Integer graceDays;
    private LocalDate validFrom;

    public BigDecimal calculateInterest(BigDecimal baseAmount, LocalDate dueDate) {
        if (baseAmount == null || baseAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Base amount must be positive");
        }
        long daysLate = ChronoUnit.DAYS.between(dueDate, LocalDate.now());
        if (daysLate <= graceDays) {
            return BigDecimal.ZERO;
        }
        long chargeableDays = daysLate - graceDays;
        return baseAmount
            .multiply(dailyInterestRate)
            .multiply(BigDecimal.valueOf(chargeableDays));
    }

}
