package org.helha.be.sortieappbackend.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Autorisation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private Autorisation_Type type;
    private String note;
    private Date date_debut;
    private Date date_fin;
    private String heure_debut;
    private String heure_fin;
    private String jours;
    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

}
