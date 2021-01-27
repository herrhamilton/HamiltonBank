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

    @Column(columnDefinition = "BINARY(16)")
    private UUID stonksApiKey;

    @Column(columnDefinition = "BINARY(16)")
    @Setter(AccessLevel.NONE)
    private final UUID hamiltonApiKey = UUID.randomUUID();

    @OneToMany(mappedBy = "customer")
    @JsonIgnore
    private List<Consulting> consultings;

    // duplicate with Advisor, but cannot move to User, because of JPA "mappedBy"
    public Consulting getOpenConsulting() {
        Optional<Consulting> pendingConsulting = getConsultings().stream()
                .filter(c -> c.isOpen()).findFirst();

        return pendingConsulting.orElse(null);
    }
}
