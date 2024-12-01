package org.helha.be.sortieappbackend.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_address;
    private String street_address;
    private String number_address;
    private String box_address;
    private String postalCode_address;
    private String locality_address;
    private String country_address;

    //Ajout relation
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id_user") // Clé étrangère dans Address
    private User user_address;

    public Address() {}

    public Address(int id_address, String street_address, String number_address, String box_address, String postalCode_address, String locality_address, String country_address, User user_address) {
        this.id_address = id_address;
        this.street_address = street_address;
        this.number_address = number_address;
        this.box_address = box_address;
        this.postalCode_address = postalCode_address;
        this.locality_address = locality_address;
        this.country_address = country_address;
        this.user_address = user_address;
    }
}
