package de.othr.sw.hamilton.entity;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class Customer extends User {

    @OneToOne //TODO was macht das?(cascade = {CascadeType.ALL})
    private BankAccount bankAccount;

    //TODO: private Consulting consultingCall;


    public BankAccount getBankAccount() {

        return bankAccount;
    }

    public void setBankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }
}
