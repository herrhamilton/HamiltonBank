package de.othr.sw.hamilton.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@NoArgsConstructor
public class PaymentRequest {
    @Getter
    @NotEmpty
    private String receiverName;

    @Getter
    @NotEmpty
    private String description;

    @Getter
    @NotNull
    @DecimalMin(value = "0.00", inclusive = false)
    @Digits(integer=8, fraction=2)
    private BigDecimal amount;

    public PaymentRequest(String receiverName, BigDecimal amount, String description) {
        this.receiverName = receiverName;
        this.amount = amount;
        this.description = description;
    }
}
