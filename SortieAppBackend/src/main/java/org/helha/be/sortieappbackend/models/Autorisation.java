package org.helha.be.sortieappbackend.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter @Setter
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
    //private long user_id;

}
