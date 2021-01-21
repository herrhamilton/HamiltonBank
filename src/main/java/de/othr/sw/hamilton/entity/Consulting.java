package de.othr.sw.hamilton.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
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

    private String reason;

    private String outcome;

    private boolean isAccepted;

    private String consultingUrl;

    private boolean isResolved;

    @OneToOne
    private Customer customer;

    @OneToOne
    private Advisor advisor;

    private Date requestTime;

    private Date startTime;

    private Date endTime;
}
