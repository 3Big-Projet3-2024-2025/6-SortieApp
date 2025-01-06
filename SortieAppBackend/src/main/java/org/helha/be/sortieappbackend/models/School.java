package org.helha.be.sortieappbackend.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString(exclude = "users_school")
@Entity
public class School {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_school;

    private String name_school;

    private String address_school;

    @OneToMany(mappedBy = "school_user", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("school_user") // Prevent infinite loops
    private List<User> users_school;

    public School() {}

    public School(int id_school, String name_school, String address_school, List<User> users_school) {
        this.id_school = id_school;
        this.name_school = name_school;
        this.address_school = address_school;
        this.users_school = users_school;
    }
}
