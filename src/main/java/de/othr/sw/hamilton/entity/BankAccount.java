package de.othr.sw.hamilton.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
public class BankAccount implements Serializable {

    @Id
    @GeneratedValue
    @Getter
    private Long id;

    @OneToOne(mappedBy = "bankAccount")
    @Setter
    private Customer owner;

    @Getter
    @Setter
    private BigDecimal balance;

    public BankAccount() {
        this.balance = BigDecimal.ZERO;
    }

    public void withdraw(BigDecimal amount) {
        this.balance = this.balance.subtract(amount);
    }

    public void deposit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BankAccount acc = (BankAccount) o;
        return id.equals(acc.id);
    }

    @Override
    public int hashCode() {
        if (id == null)
            return 0;
        else
            return id.hashCode();
    }

    @Override
    public String toString() {
        return id + " - " + owner.getFirstName() + " " + owner.getLastName();
    }
}
