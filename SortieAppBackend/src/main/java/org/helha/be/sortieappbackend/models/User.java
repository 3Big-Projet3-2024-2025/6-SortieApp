package org.helha.be.sortieappbackend.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private int  id;
    private String lastname;
    private String name;
    private String email;
    private String password;

    @OneToOne(mappedBy = "user")
    private Address address;

    @OneToMany(mappedBy = "user")
    private List<Role> roles;

    public User() {}

    public User(int id_user, String lastname, String name, String email, String password, Address address, List<Role> roles) {
        this.id = id_user;
        this.lastname = lastname;
        this.name = name;
        this.email = email;
        this.password = password;
        this.address = address;
        this.roles = roles;
    }
}
