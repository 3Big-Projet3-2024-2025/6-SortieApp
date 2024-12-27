package org.helha.be.sortieappbackend.services;

import org.helha.be.sortieappbackend.models.User;

import java.util.List;

public interface IUserService {
    public List<User> getAllUsers();
    public List<User> getUsers();
    public User addUser(User user);
    public User updateUser(User user, int id_role);
    public void deleteUser(int id_role);
}
