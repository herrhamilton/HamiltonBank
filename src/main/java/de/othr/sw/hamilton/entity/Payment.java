package de.othr.sw.hamilton.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@NoArgsConstructor
public class Payment implements Serializable {

    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

    // second id so PK is not exposed
    @Column(columnDefinition = "BINARY(16)")
    @Getter
    private UUID paymentId = UUID.randomUUID();

    @Getter
    @NotEmpty
    private String description;

    //TODO move die Attribute in sowas wie PaymentRequest?
    @Getter
    @NotNull
    @DecimalMin(value = "0.00", inclusive = false)
    @Digits(integer=8, fraction=2)
    private BigDecimal amount;

    @Getter
    @NotEmpty
    //TODO maybe doch Customer statt String und evtl Transaction dranhängen wenn abgeschlossen?
    private String receiverName;

    @Getter
    @Setter
    private String senderName;

    @Getter
    @Setter
    private String paymentUrl;

    @Getter
    @Setter
    private boolean isFulfilled = false;

    public Payment(String receiverName, BigDecimal amount, String description) {
        this.receiverName = receiverName;
        this.amount = amount;
        this.description = description;
    }
}
