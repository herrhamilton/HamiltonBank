package de.othr.sw.hamilton.entity;

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
    // second id so PK is not exposed
    @Column(columnDefinition = "BINARY(16)")
    @Getter
    @Setter(AccessLevel.NONE)
    private final UUID paymentId = UUID.randomUUID();

    private String senderName;

    private String paymentUrl;

    private boolean isFulfilled = false;

    private Date fulfillDate;

    public Payment(PaymentRequest requested) {
        super(requested.getReceiverName(), requested.getAmount(), requested.getDescription());
    }

    public void fulfill(String senderName) {
        setSenderName(senderName);
        setFulfilled(true);
        setFulfillDate(new Date());
    }
}
