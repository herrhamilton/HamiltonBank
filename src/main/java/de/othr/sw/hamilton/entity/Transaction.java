package de.othr.sw.hamilton.entity;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity @Getter @Setter
@NoArgsConstructor
public class Transaction implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private Date date;

    @NotNull
    private String description;

    @OneToOne
    private BankAccount fromAccount;

    @OneToOne
    @NotNull
    private BankAccount toAccount;

    //TODO WEG MIT DEM HACK! Ist da, weil man in nem input field nur primitive Datentypen nehmen kann (I guess?)
    private Long toAccId;

    public Transaction(BigDecimal amount, String description, BankAccount to, BankAccount from) {
        this(amount, description, to);
        this.fromAccount = from;
    }

    // constructor for Deposit money
    public Transaction(BigDecimal amount, String description, BankAccount to) {
        this.amount = amount;
        this.description = description;
        this.toAccount = to;
        this.date = new Date();
    }

    public Long getToAccId() {
        return toAccId;
    }

    public void setToAccId(Long toAccId) {
        this.toAccId = toAccId;
    }
}
