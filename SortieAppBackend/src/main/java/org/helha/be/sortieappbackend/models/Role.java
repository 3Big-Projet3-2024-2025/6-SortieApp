package org.helha.be.sortieappbackend.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_role;
    private String name_role;

    //Ajout relation
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id_user") // Clé étrangère dans Address
    private User user_role;

    public Role() {}

    public Role(int id_role, String name_role, User user_role) {
        this.id_role = id_role;
        this.name_role = name_role;
        this.user_role = user_role;
    }
}