package de.othr.sw.hamilton.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter //TODO id zum API Zugriff auf Request? DB Id iwie ned so geil. Wirklich alles Getter/Setter?
@Setter
public class Payment implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    private Customer receiver;

    //TODO annotations anschauen! Bei Customer steht doch nix von Request? Richtig so?
    @OneToOne
    private Customer sender;

    private BigDecimal amount;

    private boolean isFulfilled = false;

    private String fulfillUrl;

    //TODO moveinto constants file/config? change address
    private UUID paymentId;

    private String paymentUrl;

    private String description = "Ja ne Zahlung halt, was gibts da groß zu beschreiben?";

    public Payment(Customer receiver, BigDecimal amount) {
        this.receiver = receiver;

        this.paymentId = UUID.randomUUID();
        this.paymentUrl  = "localhost:8080/payment/" + this.paymentId;
    }
}
