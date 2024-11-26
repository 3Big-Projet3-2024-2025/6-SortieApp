package org.helha.be.sortieappbackend.controllers;

import org.helha.be.sortieappbackend.models.User;
import org.helha.be.sortieappbackend.repositories.jpa.UserRepository;
import org.helha.be.sortieappbackend.services.UserServiceDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path="/users")
public class UserController {

    @Autowired
    UserServiceDB serviceDB;

    @GetMapping
    public List<User> getUsers() { return serviceDB.getUsers(); }

    @PostMapping
    public User addUser(@RequestBody User user) {
        return serviceDB.addUser(user);
    }

    @PutMapping()
    public User updateUser(@RequestBody User user) {
        return serviceDB.updateUser(user);
    }

    @DeleteMapping(path="/{id_user}")
    public void deleteUser(@PathVariable int id_user) {
        serviceDB.deleteUser(id_user);
    }
}