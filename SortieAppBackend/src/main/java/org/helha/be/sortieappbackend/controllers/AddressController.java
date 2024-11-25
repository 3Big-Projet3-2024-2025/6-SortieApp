package org.helha.be.sortieappbackend.controllers;

import org.helha.be.sortieappbackend.models.Address;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path="/addresses")
public class AddressController {

    public List<Address> addresses = new ArrayList<Address>();

    @GetMapping
    public List<Address> getAddresses() { return addresses; }

    @PostMapping
    public Address addAddress(@RequestBody Address address) {
        address.setId_address(addresses.size() + 1);
        addresses.add(address);
        return address;
    }

    @PutMapping
    public Address updateAddress(@RequestBody Address address) {
        for (Address a : addresses) {
            if(a.getId_address() == address.getId_address()) {
                a.setStreet(address.getStreet());
                a.setNumber(address.getNumber());
                a.setBox(address.getBox());
                a.setPostalCode(address.getPostalCode());
                a.setLocality(address.getLocality());
                a.setCountry(address.getCountry());
            }
        }
        return address;
    }

    @DeleteMapping(path="/{id_address}")
    public void deleteAddress(@PathVariable int id_address) {
        for (int i = 0; i < addresses.size(); i++) {
            if(addresses.get(i).getId_address() == id_address) {
                addresses.remove(i);
                break;
            }
        }
    }
}
