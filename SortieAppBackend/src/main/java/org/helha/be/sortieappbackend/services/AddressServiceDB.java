package org.helha.be.sortieappbackend.services;

import org.helha.be.sortieappbackend.models.Address;
import org.helha.be.sortieappbackend.repositories.jpa.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Primary
public class AddressServiceDB {

    @Autowired
    private AddressRepository repository;

    public List<Address> getAddresses() { return repository.findAll(); }

    public Address addAddress(Address address) { return repository.save(address); }

    public Address updateAddress(Address address) { return repository.save(address); }

    public void deleteAddress(int id_address) { repository.deleteById(id_address); }
}
