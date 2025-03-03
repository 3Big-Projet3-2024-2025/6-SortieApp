package org.helha.be.sortieappbackend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude = "school_user")
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private int id;

    /**
     * The last name of the User.
     */
    private String lastname_user;

    /**
     * The first name of the User.
     */
    private String name_user;

    /**
     * The email address of the User.
     */
    @Column(unique = true, nullable = false)
    private String email;

    /**
     * The password of the User.
     */
    private String password_user;

    /**
     * The address of the User.
     */
    private String address_user;

    /**
     * Many-to-One relationship with the School entity.
     * Creates a foreign key column `school_id` in the User table and links to the School entity.
     * Ignores the users list in the School entity during JSON serialization/deserialization.
     */
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
    private boolean activated;

    /**
     * Stores the user's profile picture as a Base64-encoded string.
     *
     * This field is mapped to a database column of type `LONGTEXT`,
     * allowing it to store large amounts of data, such as images.
     *
     * The profile picture is typically expected to be in a Base64-encoded format,
     * which allows binary image data to be safely stored as text in the database.
     */

    @Column(columnDefinition = "LONGTEXT")
    private String picture_user;

    /**
     * Represents a one-to-one relationship between a User and an ActivationToken.
     *
     * This mapping defines that each user can have one associated activation token,
     * and the token is mapped back to the user using the "user" field in the
     * ActivationToken entity.
     *
     * The `cascade = CascadeType.ALL` ensures that any changes to the User entity
     * (e.g., deletion) are automatically propagated to the associated ActivationToken.
     *
     * The `@JsonIgnore` annotation prevents the activation token from being serialized
     * into JSON responses to avoid exposing sensitive data in API responses.
     */

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private ActivationToken activation_token;

    /**
     * Default constructor.
     */
    public User() {}

    /**
     * Constructor to initialize a User object with specific values.
     *
     * @param id           the unique identifier of the User.
     * @param lastname_user     the last name of the User.
     * @param name_user         the first name of the User.
     * @param email        the email address of the User.
     * @param password_user     the password of the User.
     * @param address_user      the address of the User.
     * @param school_user       the School associated with the User.
     * @param role_user         the Role associated with the User.
     * @param activated  whether the User account is activated.
     */
    public User(int id, String lastname_user, String name_user, String email, String password_user, String address_user, School school_user, Role role_user, boolean activated, String picture_user) {
        this.id = id;
        this.lastname_user = lastname_user;
        this.name_user = name_user;
        this.email = email;
        this.password_user = password_user;
        this.address_user = address_user;
        this.school_user = school_user;
        this.role_user = role_user;
        this.activated = activated;
        this.picture_user = picture_user;
    }

    /**
     * Method that returns the status of activation.
     */
    public Boolean getActivated() {
        return this.activated;
    }

    /**
     * Method that set the status of activation.
     */
    public void setActivated(Boolean activated) {
        this.activated = activated;
    }
}
