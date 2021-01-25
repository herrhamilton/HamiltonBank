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

@Entity
@NoArgsConstructor
public class Transaction implements Serializable, Comparable<Transaction> {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @Getter
    private BigDecimal amount;

    @NotNull
    @Getter
    private Date date;

    @NotNull
    @Getter
    private String description;

    @OneToOne
    @Getter
    private BankAccount fromAccount;

    @OneToOne
    @NotNull
    @Getter
    private BankAccount toAccount;

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

    @Override
    public int compareTo(Transaction o) {
        return this.date.compareTo(o.getDate());
    }
}
