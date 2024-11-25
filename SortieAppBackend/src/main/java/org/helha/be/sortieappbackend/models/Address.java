package org.helha.be.sortieappbackend.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_address;
    private String street;
    private String number;
    private String box;
    private String postalCode;
    private String locality;
    private String country;

    //Ajout relation
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id_user") // Clé étrangère dans Address
    private User user;

    public Address() {}

    public Address(int id_address, String street, String number, String box, String postalCode, String locality, String country, User user) {
        this.id_address = id_address;
        this.street = street;
        this.number = number;
        this.box = box;
        this.postalCode = postalCode;
        this.locality = locality;
        this.country = country;
        this.user = user;
    }
}
