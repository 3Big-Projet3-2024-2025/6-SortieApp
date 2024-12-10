package org.helha.be.sortieappbackend.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_user;

    private String lastname_user;
    private String name_user;
    private String email_user;
    private String password_user;
    private String address_user;

    @ManyToOne
    @JoinColumn(name = "school_id") // Foreign key in the User table
    @JsonIgnoreProperties("users_school") // Prevent infinite loops
    private School school_user;

    @ManyToOne
    @JoinColumn(name = "role_id") // Foreign key for Role
    @JsonIgnoreProperties("users")
    private Role role_user;

    public User() {}

    public User(int id_user, String lastname_user, String name_user, String email_user, String password_user, String address_user, School school_user, Role role_user) {
        this.id_user = id_user;
        this.lastname_user = lastname_user;
        this.name_user = name_user;
        this.email_user = email_user;
        this.password_user = password_user;
        this.address_user = address_user;
        this.school_user = school_user;
        this.role_user = role_user;
    }
}
