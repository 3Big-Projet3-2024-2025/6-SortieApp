package org.helha.be.sortieappbackend.repositories.jpa;

import org.helha.be.sortieappbackend.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {}
