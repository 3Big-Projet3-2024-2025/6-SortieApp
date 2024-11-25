package org.helha.be.sortieappbackend.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_role;
    private Roles roleType;

    //Ajout relation
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id_user") // Clé étrangère dans Address
    private User user;

    public Role() {}

    public Role(int id_role, Roles roleType, User user) {
        this.id_role = id_role;
        this.roleType = roleType;
        this.user = user;
    }
}