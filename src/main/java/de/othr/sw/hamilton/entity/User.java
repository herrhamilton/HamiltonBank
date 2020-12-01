package de.othr.sw.hamilton.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class User implements Serializable {

    @Id @GeneratedValue
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String passwordHash;

    @Embedded
    private Address address;

    public User() { }

    public User(String fname, String lname) {
        this.firstName = fname;
        this.lastName = lname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        if(id == null)
            return 0;
        else
            return id.hashCode();
    }





    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
