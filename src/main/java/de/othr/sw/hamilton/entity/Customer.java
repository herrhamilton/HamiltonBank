package de.othr.sw.hamilton.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;
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

    @OneToMany(mappedBy = "customer")
    @JsonIgnore
    private List<Consulting> consultings;

    public Consulting getOpenConsulting() {
        //TODO darf max. 1 sein
        Optional<Consulting> pendingConsulting = getConsultings().stream()
                .filter(c -> c.isOpen()).findFirst();

        return pendingConsulting.orElse(null);
    }
}
