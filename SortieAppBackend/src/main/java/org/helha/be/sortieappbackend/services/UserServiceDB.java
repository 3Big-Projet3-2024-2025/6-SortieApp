/**
 * Service class to manage operations on the User entity using a database.
 * This service provides CRUD operations for User entities.
 */
package org.helha.be.sortieappbackend.services;

import org.helha.be.sortieappbackend.models.Role;
import org.helha.be.sortieappbackend.models.School;
import org.helha.be.sortieappbackend.models.User;
import org.helha.be.sortieappbackend.repositories.jpa.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Primary
public class UserServiceDB implements IUserService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private RoleServiceDB roleServiceDB;
    @Autowired
    private SchoolServiceDB schoolServiceDB;

    /**
     * Retrieve all users from the database.
     */
    public List<User> getUsers() {
        return repository.findAll();
    }

    /**
     * Retrieve a user by their ID.
     * @param id_user the ID of the user to retrieve
     * @return an Optional containing the user if found, or empty if not
     */
    public Optional<User> getUserById(int id_user) {
        return repository.findById(id_user);
    }

    /**
     * Add a new user to the database.
     * If a role is specified, it validates the role before saving the user.
     * @param user the user to add
     * @return the saved user
     */
    public User addUser(User user) {
        // Check if a role is defined and validate it
        if (user.getRole_user() != null && user.getRole_user().getId_role() != 0) {
            Role role = roleServiceDB.getRoleById(user.getRole_user().getId_role())
                    .orElseThrow(() -> new IllegalArgumentException("Role with ID " + user.getRole_user().getId_role() + " not found"));
            user.setRole_user(role);
        }

        // Save the user to the database
        return repository.save(user);
    }

    /**
     * Update an existing user's details in the database.
     * Includes updates to name, email, address, password, role, and activation status.
     * @param newUser the updated user details
     * @param id_user the ID of the user to update
     * @return the updated user
     */
    public User updateUser(User newUser, int id_user) {
        return repository.findById(id_user)
                .map(user -> {
                    // Update basic user details
                    user.setName_user(newUser.getName_user());
                    user.setLastname_user(newUser.getLastname_user());
                    user.setEmail_user(newUser.getEmail_user());
                    user.setAddress_user(newUser.getAddress_user());

                    // Update the password only if provided
                    if (newUser.getPassword_user() != null) {
                        user.setPassword_user(newUser.getPassword_user());
                    }

                    // Update and validate the school if specified
                    if (newUser.getSchool_user() != null && newUser.getSchool_user().getId_school() != 0) {
                        School school = schoolServiceDB.getSchoolById(newUser.getSchool_user().getId_school())
                                .orElseThrow(() -> new IllegalArgumentException("School with ID " + newUser.getSchool_user().getId_school() + " not found"));
                        user.setSchool_user(school);
                    }


                    // Update and validate the role if specified
                    if (newUser.getRole_user() != null && newUser.getRole_user().getId_role() != 0) {
                        Role role = roleServiceDB.getRoleById(newUser.getRole_user().getId_role())
                                .orElseThrow(() -> new IllegalArgumentException("Role with ID " + newUser.getRole_user().getId_role() + " not found"));
                        user.setRole_user(role);
                    }

                    // Update activation status
                    if (user.isActivated_user()) {
                        // Nothing to do
                    } else {
                        user.setActivated_user(true);
                    }

                    user.setPicture_user((newUser.getPicture_user()));

                    // Save and return the updated user
                    return repository.save(user);
                })
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + id_user + " not found"));
    }

    /**
     * Delete a user from the database by their ID.
     * @param id_user the ID of the user to delete
     */
    public void deleteUser(int id_user) {
        repository.deleteById(id_user);
    }
}
