package de.othr.sw.hamilton.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@NoArgsConstructor
public class PaymentRequest {
    @Id
    @GeneratedValue
    @JsonIgnore
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Long id;

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
