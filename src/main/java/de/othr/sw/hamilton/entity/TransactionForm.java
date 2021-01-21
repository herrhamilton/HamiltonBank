package de.othr.sw.hamilton.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;

// Needed for filling in Transaction Form in transaction.html,
// because we cannot convert input field text (=String) to "BankAccount" entity
@Getter @Setter
public class TransactionForm implements Serializable {
    public BigDecimal amount;
    public String description;
    public String toUsername;
    private String id;
}