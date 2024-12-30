package org.helha.be.sortieappbackend.repositories.jpa;

import org.helha.be.sortieappbackend.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    @Query("SELECT a FROM Role a WHERE a.name_role = :name_role")
    Optional<Role> findByNameRole(String name_role);
}
