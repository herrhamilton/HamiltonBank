package de.othr.sw.hamilton.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
public class BankAccount implements Serializable {

    @Id @GeneratedValue //TODO: sequential?
    private Long id;

    @OneToOne(mappedBy="bankAccount") //TODO: Was zu beachten bei Änderungen?
    //TODO getter unmodifiable wegen Foreign Key oder nur bei Lists?
    @Setter
    private Customer owner;

    @Getter
    @Setter
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
}
