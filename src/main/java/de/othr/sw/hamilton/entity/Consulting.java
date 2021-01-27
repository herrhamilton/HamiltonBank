package de.othr.sw.hamilton.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Consulting implements Serializable {
    @Id
    @GeneratedValue
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Long id;

    // second id so PK is not exposed
    @Column(columnDefinition = "BINARY(16)")
    @Getter
    @Setter(AccessLevel.NONE)
    private UUID consultingId = UUID.randomUUID();

    @ManyToOne(cascade = CascadeType.MERGE)
    private Customer customer;

    @ManyToOne(cascade = CascadeType.MERGE)
    private Advisor advisor;

    private String reason;

    private String summary;

    private String accessToken;

    private boolean isAccepted = false;

    private boolean isOpen = true;

    private Date requestTime;

    private Date acceptTime;

    private Date endTime;
}
