package org.helha.be.sortieappbackend.controllers;

import org.helha.be.sortieappbackend.models.Role;
import org.helha.be.sortieappbackend.models.User;
import org.helha.be.sortieappbackend.services.RoleServiceDB;
import org.helha.be.sortieappbackend.services.UserServiceDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path="/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    UserServiceDB serviceDB;

    @Autowired
    RoleServiceDB roleServiceDB;

    @GetMapping
    public List<User> getUsers() { return serviceDB.getUsers(); }

    @PostMapping
    public User addUser(@RequestBody User user) {
        // Check if a role is defined
        if (user.getRole_user() != null && user.getRole_user().getId_role() != 0) {
            Role role = roleServiceDB.getRoleById(user.getRole_user().getId_role())
                    .orElseThrow(() -> new RuntimeException("Role not found"));
            user.setRole_user(role);
        }

        return serviceDB.addUser(user);
    }

    @PutMapping(path = "/{id_user}")
    public User updateUser(@RequestBody User user, @PathVariable int id_user) {
        // Fetch existing user from DB
        User existingUser = serviceDB.getUserById(id_user)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update fields of the current user
        existingUser.setLastname_user(user.getLastname_user());
        existingUser.setName_user(user.getName_user());
        existingUser.setEmail_user(user.getEmail_user());
        existingUser.setPassword_user(user.getPassword_user());
        existingUser.setAddress_user(user.getAddress_user());

        // Update the role
        if (user.getRole_user() != null && user.getRole_user().getId_role() != 0) {
            Role newRole = roleServiceDB.getRoleById(user.getRole_user().getId_role())
                    .orElseThrow(() -> new RuntimeException("Role not found"));
            existingUser.setRole_user(newRole);
        }

        return serviceDB.updateUser(existingUser, id_user);
    }

    @DeleteMapping(path="/{id_user}")
    public void deleteUser(@PathVariable int id_user) {
        serviceDB.deleteUser(id_user);
    }
}