package de.othr.sw.hamilton.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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

    private String description = "Ja ne Zahlung halt, was gibts da groß zu beschreiben?";

    private BigDecimal amount;

    private String paymentUrl;

    private boolean isFulfilled = false;

    public Payment(String username, BigDecimal amount) {
        this.receiverName = username;
        this.amount = amount;

        //TODO moveinto constants file/config? change address
        this.paymentUrl  = "localhost:8080/payment/" + this.paymentId;
    }
}
