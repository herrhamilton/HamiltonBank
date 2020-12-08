package de.othr.sw.hamilton.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class Customer extends User {

    @OneToOne //TODO was macht das?(cascade = {CascadeType.ALL})
    @Getter
    @Setter
    private BankAccount bankAccount;

    //TODO: private Consulting consultingCall;
}
