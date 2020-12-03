package de.othr.sw.hamilton.entity;

import javax.persistence.Embeddable;

@Embeddable
public class Address {
    private String street;

    private String houseNr;

    private String postalCode;

    private String city;

    public String getStreet() {
        return street;
    }

    public String getHouseNr() {
        return houseNr;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setHouseNr(String houseNr) {
        this.houseNr = houseNr;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
