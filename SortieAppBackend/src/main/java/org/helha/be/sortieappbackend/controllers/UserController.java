/**
 * REST controller for managing User-related operations.
 * Provides endpoints to perform CRUD operations on User entities.
 */
package org.helha.be.sortieappbackend.controllers;

import org.helha.be.sortieappbackend.models.User;
import org.helha.be.sortieappbackend.services.UserServiceDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for handling User-related HTTP requests.
 * This controller provides endpoints for creating, retrieving, updating, and deleting User entities.
 * It supports operations such as retrieving all users, adding a new user, updating an existing user,
 * and deleting a user by ID.
 */
@RestController
@RequestMapping(path = "/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    UserServiceDB serviceDB;

    /**
     * Retrieves a list of all users.
     *
     * @return a list of all {@link User} objects.
     */
    @GetMapping
    public List<User> getUsers() {
        return serviceDB.getUsers();
    }

    /**
     * Adds a new User to the database.
     *
     * @param user the {@link User} object to add.
     * @return the added {@link User} object.
     */
    @PostMapping
    public User addUser(@RequestBody User user) {
        return serviceDB.addUser(user);
    }

    /**
     * Updates an existing User in the database.
     *
     * @param user    the updated {@link User} object.
     * @param id_user the ID of the {@link User} to update.
     * @return the updated {@link User} object.
     */
    @PutMapping(path = "/{id_user}")
    public User updateUser(@RequestBody User user, @PathVariable int id_user) {
        return serviceDB.updateUser(user, id_user);
    }

    /**
     * Deletes a User from the database by ID.
     *
     * @param id_user the ID of the {@link User} to delete.
     */
    @DeleteMapping(path = "/{id_user}")
    public void deleteUser(@PathVariable int id_user) {
        serviceDB.deleteUser(id_user);
    }
}
