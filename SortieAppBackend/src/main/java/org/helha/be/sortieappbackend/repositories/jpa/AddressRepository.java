package org.helha.be.sortieappbackend.repositories.jpa;

import org.helha.be.sortieappbackend.models.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {
}
