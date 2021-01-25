package de.othr.sw.hamilton.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Advisor extends User {

    @OneToOne
    @JsonIgnore
    private Consulting runningConsulting;

    @Column(columnDefinition = "BINARY(16)")
    private UUID vociApiKey;
}
