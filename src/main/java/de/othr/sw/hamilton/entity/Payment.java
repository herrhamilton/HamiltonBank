package de.othr.sw.hamilton.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
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
    private String description;

    @Getter
    private BigDecimal amount;

    @Getter
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
