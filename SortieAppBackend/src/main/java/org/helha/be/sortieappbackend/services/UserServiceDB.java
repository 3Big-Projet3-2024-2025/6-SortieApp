/**
 * Service class for managing User operations using the database.
 */
package org.helha.be.sortieappbackend.services;

import org.helha.be.sortieappbackend.models.Role;
import org.helha.be.sortieappbackend.models.School;
import org.helha.be.sortieappbackend.models.User;
import org.helha.be.sortieappbackend.repositories.jpa.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
@Primary
public class UserServiceDB implements IUserService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private RoleServiceDB roleServiceDB;

    @Autowired
    private SchoolServiceDB schoolServiceDB;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Retrieve all users from the database.
     */
    public List<User> getAllUsers() {
        return repository.findAll();
    }

    /**
     * Retrieve all activated users from the database.
     */
    public List<User> getUsers() {
        return repository.findByActivatedTrue();
    }

    /**
     * Retrieve a user by ID.
     */
    public Optional<User> getUserById(int id_user) {
        return repository.findById(id_user);
    }

    /**
     * Add a new user to the database.
     */
    public User addUser(User user) {
        if (user.getRole_user() != null && user.getRole_user().getId_role() != 0) {
            Role role = roleServiceDB.getRoleById(user.getRole_user().getId_role())
                    .orElseThrow(() -> new IllegalArgumentException("Role not found"));
            user.setRole_user(role);
        }

        user.setPassword_user(passwordEncoder.encode(user.getPassword_user()));

        /*
        // Validate Base64 image if provided
        if (user.getPicture_user() != null && !user.getPicture_user().isEmpty()) {
            convertImageToBase64(user.getPicture_user());
        }
         */

        return repository.save(user);
    }

    /**
     * Update an existing user.
     */
    public User updateUser(User newUser, int id_user) {
        return repository.findById(id_user)
                .map(user -> {
                    user.setName_user(newUser.getName_user());
                    user.setLastname_user(newUser.getLastname_user());
                    user.setEmail(newUser.getEmail());
                    user.setAddress_user(newUser.getAddress_user());

                    if (newUser.getPassword_user() != null) {
                        user.setPassword_user(passwordEncoder.encode(newUser.getPassword_user()));
                    }

                    if (newUser.getSchool_user() != null && newUser.getSchool_user().getId_school() != 0) {
                        School school = schoolServiceDB.getSchoolById(newUser.getSchool_user().getId_school())
                                .orElseThrow(() -> new IllegalArgumentException("School not found"));
                        user.setSchool_user(school);
                    }

                    if (newUser.getRole_user() != null && newUser.getRole_user().getId_role() != 0) {
                        Role role = roleServiceDB.getRoleById(newUser.getRole_user().getId_role())
                                .orElseThrow(() -> new IllegalArgumentException("Role not found"));
                        user.setRole_user(role);
                    }

                    if (newUser.getPicture_user() != null) {
                        user.setPicture_user(newUser.getPicture_user());
                    }

                    // Y'a un stut ici
                    if (newUser.getActivated() != null) {
                        user.setActivated(newUser.getActivated());
                    }

                    return repository.save(user);
                })
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    /**
     * Deleting logically a user.
     */
    public void deleteUser(int id_user) {
        repository.findById(id_user).ifPresent(user -> {
            user.setActivated(false);
            repository.save(user);
        });
    }

    /**
     * Deleting physically a user.
     */
    public void deleteUserPhysically(int id_user) {
        repository.deleteById(id_user);
    }
}
