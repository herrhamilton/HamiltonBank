package de.othr.sw.hamilton.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.util.UUID;

@Entity @NoArgsConstructor
@Getter @Setter
public class Customer extends User {

    @OneToOne
    private BankAccount bankAccount;

    @Embedded
    private Address address;

    //API Keys werden der Einfachheit halber einfach auf der Seite angezeigt, Security hat hier niedrige Priorität
    private UUID stonksApiKey;

    @Column(columnDefinition = "BINARY(16)")
    private UUID hamiltonApiKey = UUID.randomUUID();
}
