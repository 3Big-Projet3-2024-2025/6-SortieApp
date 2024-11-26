package org.helha.be.sortieappbackend.services;

import org.helha.be.sortieappbackend.models.Address;

import java.util.List;

public interface IAddressService {
    public List<Address> getAddresses();
    public Address addAddress(Address address);
    public Address updateAddress(Address address);
    public void deleteAddress(int id_address);
}
