package de.othr.sw.hamilton.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
public class BankAccount implements Serializable {

    @Id @GeneratedValue //TODO: sequential?
    private Long id;

    @OneToOne(mappedBy="bankAccount") //TODO: Was zu beachten bei Änderungen?
    private Customer owner;

    private BigDecimal balance;

    public BankAccount() {
        this.balance = BigDecimal.ZERO;
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
        if(id == null)
            return 0;
        else
            return id.hashCode();
    }

    public Long getBankAccountId() {
        return id;
    }

    public void setBankAccountId(Long bankAccountId) {
        this.id = bankAccountId;
    }

    public Customer getOwner() {
        // TODO: Unmodifiable?
        return owner;
    }

    public void setOwner(Customer owner) {
        this.owner = owner;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
