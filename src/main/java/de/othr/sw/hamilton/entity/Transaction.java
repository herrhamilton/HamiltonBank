package de.othr.sw.hamilton.entity;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity @Getter @NoArgsConstructor
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

    public Transaction(int amount, String description, BankAccount to, BankAccount from) {
        this(amount, description, to);
        this.fromAccount = from;
    }

    // Deposit money
    public Transaction(int amount, String description, BankAccount to) {
        this.amount = BigDecimal.valueOf(amount);
        this.description = description;
        this.toAccount = to;
        this.date = new Date();
    }
}
