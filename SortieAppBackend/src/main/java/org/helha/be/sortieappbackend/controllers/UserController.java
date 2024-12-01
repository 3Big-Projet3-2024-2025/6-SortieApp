package org.helha.be.sortieappbackend.controllers;

import org.helha.be.sortieappbackend.models.Address;
import org.helha.be.sortieappbackend.models.User;
import org.helha.be.sortieappbackend.services.AddressServiceDB;
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

    @Autowired
    AddressServiceDB addressServiceDB;

    @GetMapping
    public List<User> getUsers() { return serviceDB.getUsers(); }

    @PostMapping
    public User addUser(@RequestBody User user) {
        if (user.getAddress_user() != null) {
            Address newAddressToAdd = user.getAddress_user();
            addressServiceDB.addAddress(newAddressToAdd);
        }
        return serviceDB.addUser(user);
    }

    @PutMapping(path="/{id_user}")
    public User updateUser(@RequestBody User user, @PathVariable int id_user) {
        return serviceDB.updateUser(user, id_user);
    }

    @DeleteMapping(path="/{id_user}")
    public void deleteUser(@PathVariable int id_user) {
        serviceDB.deleteUser(id_user);
    }
}