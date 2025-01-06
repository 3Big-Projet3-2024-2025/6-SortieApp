package org.helha.be.sortieappbackend.services;

import org.helha.be.sortieappbackend.models.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IUserService {
    public List<User> getAllUsers();
    public List<User> getUsers();
    public User addUser(User user);
    public User updateUser(User user, int id_role);
    public void deleteUser(int id_role);
    public void deleteUserPhysically(int id_user);
    public void importUsersFromCSV(MultipartFile file);
    public void updateProfilePicture(int userId, String base64Image);
}
