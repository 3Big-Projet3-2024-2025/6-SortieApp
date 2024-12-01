package org.helha.be.sortieappbackend.services;

import org.helha.be.sortieappbackend.models.User;
import org.helha.be.sortieappbackend.repositories.jpa.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Primary
public class UserServiceDB implements IUserService {

    @Autowired
    private UserRepository repository;

    public List<User> getUsers() {
        return repository.findAll();
    }

    public User addUser(User user) {
        return repository.save(user);
    }

    public User updateUser(User newUser, int id_user) {
        return repository.findById(id_user)
                .map(user -> {
                    user.setName_user(newUser.getName_user());
                    user.setLastname_user(newUser.getLastname_user());
                    user.setEmail_user(newUser.getEmail_user());
                    user.setAddress_user(newUser.getAddress_user());
                    user.setRoles_user(newUser.getRoles_user());
                    return repository.save(user);
                })
                .orElseGet(() -> repository.save(newUser));
    }

    public void deleteUser(int id_user) {
        repository.deleteById(id_user);
    }
}
