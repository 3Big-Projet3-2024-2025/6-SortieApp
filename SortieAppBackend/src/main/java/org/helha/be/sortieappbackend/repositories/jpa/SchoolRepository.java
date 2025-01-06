package org.helha.be.sortieappbackend.repositories.jpa;

import org.helha.be.sortieappbackend.models.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchoolRepository extends JpaRepository<School, Integer> {}
