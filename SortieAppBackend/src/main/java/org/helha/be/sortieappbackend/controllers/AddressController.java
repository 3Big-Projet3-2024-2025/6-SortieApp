package org.helha.be.sortieappbackend.controllers;

import org.helha.be.sortieappbackend.models.Address;
import org.helha.be.sortieappbackend.services.AddressServiceDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path="/addresses")
public class AddressController {

    @Autowired
    AddressServiceDB serviceDB;

    @GetMapping
    public List<Address> getAddresses() { return serviceDB.getAddresses(); }

    @PostMapping
    public Address addAddress(@RequestBody Address address) {
        return serviceDB.addAddress(address);
    }

    @PutMapping(path="/{id_address}")
    public Address updateAddress(@RequestBody Address address, @PathVariable int id_address) {
        return serviceDB.updateAddress(address, id_address);
    }

    @DeleteMapping(path="/{id_address}")
    public void deleteAddress(@PathVariable int id_address) {
        serviceDB.deleteAddress(id_address);
    }
}
