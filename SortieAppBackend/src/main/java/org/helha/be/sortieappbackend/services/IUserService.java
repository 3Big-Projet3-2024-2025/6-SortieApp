package org.helha.be.sortieappbackend.services;

import org.helha.be.sortieappbackend.models.User;

import java.util.List;

public interface IUserService {
    public List<User> getUsers();
    public User addUser(User utilisateur);
    public User updateUser(User utilisateur);
    public void deleteUser(int id_user);
}
