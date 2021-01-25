package de.othr.sw.hamilton.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;

@Embeddable
@Getter
@Setter
public class Address {
    private String street;

    private String houseNr;

    private String postalCode;

    private String city;
}
