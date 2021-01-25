package de.othr.sw.hamilton.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Customer extends User {

    @OneToOne
    private BankAccount bankAccount;

    @Embedded
    private Address address;

    //API Keys werden der Einfachheit halber einfach auf der Seite angezeigt, Security hat hier niedrige Priorität
    @Column(columnDefinition = "BINARY(16)")
    private UUID stonksApiKey;

    @Column(columnDefinition = "BINARY(16)")
    @Setter(AccessLevel.NONE)
    private UUID hamiltonApiKey = UUID.randomUUID();

    @OneToOne
    @JsonIgnore
    private Consulting pendingConsulting;
}
