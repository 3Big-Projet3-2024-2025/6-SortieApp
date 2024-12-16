package org.helha.be.sortieappbackend.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private int  id;
    private String lastname_user;
    private String name_user;
    private String email;
    private String password_user;
    private String address_user;

    @ManyToOne
    @JoinColumn(name = "school_id")
    @JsonIgnoreProperties("users_school")
    private School school_user;

    /**
     * Many-to-One relationship with the Role entity.
     * Creates a foreign key column `role_id` in the User table and links to the Role entity.
     * Ignores the users list in the Role entity during JSON serialization/deserialization.
     */
    @ManyToOne
    @JoinColumn(name = "role_id")
    @JsonIgnoreProperties("users")
    private Role role_user;

    /**
     * Indicates whether the User account is activated.
     */
    private boolean isActivated_user;

    private String picture_user;

    /**
     * Default constructor.
     */
    public User() {}

    public User(int id_user, String lastname_user, String name_user, String email_user, String password_user, String address_user, Role role_user) {
        this.id = id_user;
        this.lastname_user = lastname_user;
        this.name_user = name_user;
        this.email = email_user;
        this.password_user = password_user;
        this.address_user = address_user;
        this.school_user = school_user;
        this.role_user = role_user;
        this.isActivated_user = isActivated_user;
        this.picture_user = picture_user;
    }
}
