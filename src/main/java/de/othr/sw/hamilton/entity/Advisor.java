package de.othr.sw.hamilton.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Advisor extends User {

    @OneToMany(mappedBy = "advisor")
    @JsonIgnore
    private List<Consulting> consultings;

    @Column(columnDefinition = "BINARY(16)")
    private UUID vociApiKey;

    @JsonIgnore
    public Consulting getRunningConsulting() {
        //TODO darf max. 1 sein
        Optional<Consulting> pendingConsulting = getConsultings().stream()
                .filter(c -> c.isOpen()).findFirst();

        return pendingConsulting.orElse(null);
    }
}
