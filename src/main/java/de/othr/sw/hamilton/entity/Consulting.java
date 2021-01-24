package de.othr.sw.hamilton.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Entity
@Getter @Setter
public class Consulting implements Serializable  {
    @Id
    @GeneratedValue
    private Long id;

    // second id so PK is not exposed
    @Column(columnDefinition = "BINARY(16)")
    private UUID consultingId = UUID.randomUUID();

    @OneToOne
    private Customer customer;

    @OneToOne
    private Advisor advisor;

    private String reason;

    private String summary;

    private String accessToken;

    private boolean isAccepted;

    private boolean isResolved;

    private Instant requestTime;

    private Instant acceptTime;

    private Instant endTime;
}
