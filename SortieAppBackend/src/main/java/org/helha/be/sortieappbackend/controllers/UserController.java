/**
 * REST controller for managing User-related operations.
 * Provides endpoints to perform CRUD operations on User entities.
 */
package org.helha.be.sortieappbackend.controllers;

import org.helha.be.sortieappbackend.models.Role;
import org.helha.be.sortieappbackend.models.User;
import org.helha.be.sortieappbackend.services.RoleServiceDB;
import org.helha.be.sortieappbackend.services.UserServiceDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for handling User-related HTTP requests.
 */
@RestController
@RequestMapping(path = "/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    UserServiceDB serviceDB;

    @Autowired
    RoleServiceDB roleServiceDB;

    /**
     * Retrieves a list of all users.
     *
     * @return a list of all User objects.
     */
    @GetMapping
    public List<User> getUsers() {
        return serviceDB.getUsers();
    }

    /**
     * Adds a new User.
     *
     * @param user the User object to add.
     * @return the added User object.
     */
    @PostMapping
    public User addUser(@RequestBody User user) {
        return serviceDB.addUser(user);
    }

    /**
     * Updates an existing User.
     *
     * @param user    the updated User object.
     * @param id_user the ID of the User to update.
     * @return the updated User object.
     */
    @PutMapping(path = "/{id_user}")
    public User updateUser(@RequestBody User user, @PathVariable int id_user) {
        return serviceDB.updateUser(user, id_user);
    }

    /**
     * Deletes a User by ID.
     *
     * @param id_user the ID of the User to delete.
     */
    @DeleteMapping(path = "/{id_user}")
    public void deleteUser(@PathVariable int id_user) {
        serviceDB.deleteUser(id_user);
    }
}
