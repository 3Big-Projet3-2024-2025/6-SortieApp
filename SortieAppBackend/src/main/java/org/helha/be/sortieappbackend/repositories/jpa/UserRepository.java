package org.helha.be.sortieappbackend.repositories.jpa;

import org.helha.be.sortieappbackend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findUserByEmail_user(String email_user);
}
