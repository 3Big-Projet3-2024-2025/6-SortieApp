package org.helha.be.sortieappbackend.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class ActivationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String token;

    private LocalDateTime expiryDate = LocalDateTime.now().plusHours(24); // Token valable 24 hours

    @OneToOne
    private User user;
}
