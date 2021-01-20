package de.othr.sw.hamilton.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity @NoArgsConstructor
@Getter @Setter
public class Customer extends User {

    @OneToOne //TODO was macht das?(cascade = {CascadeType.ALL})
    private BankAccount bankAccount;

    //TODO settings page where user can put in api key
    private String stonksApiKey;

    //TODO: private Consulting consultingCall;
}
