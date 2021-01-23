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
@Getter //TODO Wirklich alles Getter/Setter?
@Setter
public class Payment implements Serializable {

    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

    // second id so PK is not exposed
    @Column(columnDefinition = "BINARY(16)")
    private UUID paymentId = UUID.randomUUID();

    private String receiverName;

    private String senderName;

    private String description;

    private BigDecimal amount;

    private String paymentUrl;

    private boolean isFulfilled = false;

    public Payment(String username, BigDecimal amount, String description) {
        this.receiverName = username;
        this.amount = amount;
        this.description = description;
    }
}
