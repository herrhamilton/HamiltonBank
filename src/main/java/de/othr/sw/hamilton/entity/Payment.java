package de.othr.sw.hamilton.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Payment extends PaymentRequest implements Serializable {

    @Id
    @GeneratedValue
    @JsonIgnore
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Long id;

    // second id so PK is not exposed
    @Column(columnDefinition = "BINARY(16)")
    @Getter
    @Setter(AccessLevel.NONE)
    private final UUID paymentId = UUID.randomUUID();

    private String senderName;

    private String paymentUrl;

    private boolean isFulfilled = false;

    private Date fulfillDate;

    @OneToOne
    @JsonIgnore
    private Transaction transaction;

    public Payment(PaymentRequest requested) {
        super(requested.getReceiverName(), requested.getAmount(), requested.getDescription());
    }

    public void fulfill(Transaction transaction) {
        String senderName = transaction.getFromAccount().getOwner().getUsername();
        setTransaction(transaction);
        setSenderName(senderName);
        setFulfilled(true);
        setFulfillDate(new Date());
    }
}
