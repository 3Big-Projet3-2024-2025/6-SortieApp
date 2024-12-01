package org.helha.be.sortieappbackend.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int  id_user;
    private String lastname_user;
    private String name_user;
    private String email_user;
    private String password_user;

    @OneToOne(mappedBy = "user_address")
    private Address address_user;

    @OneToMany(mappedBy = "user_role")
    private List<Role> roles_user;

    public User() {}

    public User(int id_user, String lastname_user, String name_user, String email_user, String password_user, Address address_user, List<Role> roles_user) {
        this.id_user = id_user;
        this.lastname_user = lastname_user;
        this.name_user = name_user;
        this.email_user = email_user;
        this.password_user = password_user;
        this.address_user = address_user;
        this.roles_user = roles_user;
    }
}
