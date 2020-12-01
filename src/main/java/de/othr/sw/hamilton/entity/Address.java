package de.othr.sw.hamilton.entity;

import javax.persistence.Embeddable;

@Embeddable
public class Address {
    private String street;

    private String houseNr;

    private String postalCode;

    private String city;
}
