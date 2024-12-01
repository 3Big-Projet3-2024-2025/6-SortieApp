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

    public Address updateAddress(Address newAddress, int id_address) {
        return repository.findById(id_address)
                .map(address -> {
                    address.setStreet_address(newAddress.getStreet_address());
                    address.setNumber_address(newAddress.getNumber_address());
                    address.setBox_address(newAddress.getBox_address());
                    address.setPostalCode_address(newAddress.getPostalCode_address());
                    address.setLocality_address(newAddress.getLocality_address());
                    address.setCountry_address(newAddress.getCountry_address());
                    address.setBox_address(newAddress.getBox_address());
                    return repository.save(address);
                })
                .orElseGet(() -> repository.save(newAddress));
    }

    public void deleteAddress(int id_address) { repository.deleteById(id_address); }
}
