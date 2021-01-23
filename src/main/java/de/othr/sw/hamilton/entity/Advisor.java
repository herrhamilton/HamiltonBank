package de.othr.sw.hamilton.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Advisor extends User {

    @OneToOne
    @JsonIgnore
    private Consulting runningConsulting;

    private UUID vociApiKey;
}
