package org.helha.be.sortieappbackend.repositories.jpa;

import org.helha.be.sortieappbackend.models.ActivationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ActivationTokenRepository extends JpaRepository<ActivationToken, Long> {
    Optional<ActivationToken> findByToken(String token);

    @Modifying
    @Query("DELETE FROM ActivationToken at WHERE at.token = :token")
    void deleteByToken(@Param("token") String token);

    @Override
    void flush();
}

