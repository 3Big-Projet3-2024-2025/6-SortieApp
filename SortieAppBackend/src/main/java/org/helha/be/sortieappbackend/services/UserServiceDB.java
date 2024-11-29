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
                    user.setName(newUser.getName());
                    user.setLastname(newUser.getLastname());
                    user.setEmail(newUser.getEmail());
                    user.setAddress(newUser.getAddress());
                    user.setRoles(newUser.getRoles());
                    return repository.save(user);
                })
                .orElseGet(() -> repository.save(newUser));
    }

    public void deleteUser(int id_user) {
        repository.deleteById(id_user);
    }
}
