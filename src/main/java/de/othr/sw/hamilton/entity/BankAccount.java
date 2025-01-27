package de.othr.sw.hamilton.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
public class BankAccount implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(mappedBy = "bankAccount")
    @Getter
    @Setter
    private Customer owner;

    @Getter
    private BigDecimal balance = BigDecimal.ZERO;

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
        return owner.getUsername();
    }
}
