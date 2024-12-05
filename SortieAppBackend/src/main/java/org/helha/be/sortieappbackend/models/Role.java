package org.helha.be.sortieappbackend.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_role;
    private String name_role;

    @OneToMany(mappedBy = "role_user", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("role") // Ignore the role of each user
    private List<User> users;

    public Role() {}

    public Role(int id_role, String name_role, List<User> users) {
        this.id_role = id_role;
        this.name_role = name_role;
        this.users = users;
    }
}