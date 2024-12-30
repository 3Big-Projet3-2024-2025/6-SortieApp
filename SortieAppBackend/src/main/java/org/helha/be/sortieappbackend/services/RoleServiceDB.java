/**
 * Service class to manage operations on the Role entity using a database.
 * This service provides CRUD operations for Role entities.
 */
package org.helha.be.sortieappbackend.services;

import lombok.Data;
import org.helha.be.sortieappbackend.models.Role;
import org.helha.be.sortieappbackend.repositories.jpa.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for handling Role-related database operations.
 */
@Service
@Primary
@Data
public class RoleServiceDB {

    @Autowired
    private RoleRepository repository;

    /**
     * Retrieves all roles from the database.
     *
     * @return a list of all Role objects.
     */
    public List<Role> getRoles() {
        return repository.findAll();
    }

    /**
     * Retrieves a Role by its ID.
     *
     * @param id_role the ID of the Role to retrieve.
     * @return an Optional containing the Role if found, or empty if not.
     */
    public Optional<Role> getRoleById(int id_role) {
        return repository.findById(id_role);
    }

    /**
     * Adds a new Role to the database.
     *
     * @param role the Role object to add.
     * @return the saved Role object.
     */
    public Role addRole(Role role) {
        return repository.save(role);
    }

    /**
     * Updates an existing Role or adds a new one if the Role with the specified ID does not exist.
     *
     * @param newRole the new Role data to update or add.
     * @param id_role the ID of the Role to update.
     * @return the updated or newly created Role object.
     */
    public Role updateRole(Role newRole, int id_role) {
        return repository.findById(id_role)
                .map(role -> {
                    role.setName_role(newRole.getName_role());
                    return repository.save(role);
                })
                .orElseGet(() -> repository.save(newRole));
    }

    /**
     * Deletes a Role by its ID.
     *
     * @param id_role the ID of the Role to delete.
     */
    public void deleteRole(int id_role) {
        repository.deleteById(id_role);
    }

    public Optional<Role> getRoleByName(String roleName) {
        return repository.findByNameRole(roleName);
    }
}
