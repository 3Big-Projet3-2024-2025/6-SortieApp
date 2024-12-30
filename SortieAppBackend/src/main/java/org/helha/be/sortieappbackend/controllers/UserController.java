package org.helha.be.sortieappbackend.controllers;

import org.helha.be.sortieappbackend.models.Role;
import org.helha.be.sortieappbackend.models.User;
import org.helha.be.sortieappbackend.services.RoleServiceDB;
import org.helha.be.sortieappbackend.services.UserServiceDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

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
        // Check if a role is defined
        if (user.getRole_user() != null && user.getRole_user().getId_role() != 0) {
            Role role = roleServiceDB.getRoleById(user.getRole_user().getId_role())
                    .orElseThrow(() -> new RuntimeException("Role not found"));
            user.setRole_user(role);
        }
        user.setPassword_user(passwordEncoder.encode(user.getPassword_user()));
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