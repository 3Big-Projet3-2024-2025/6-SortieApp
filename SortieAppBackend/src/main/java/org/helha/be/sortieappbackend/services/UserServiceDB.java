/**
 * Service class for managing User operations using the database.
 */
package org.helha.be.sortieappbackend.services;

import com.opencsv.CSVReader;
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
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
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
        Role roleStudent = roleServiceDB.getRoleByName("STUDENT")
                .orElseThrow(() -> new IllegalArgumentException("Role STUDENT not found"));

        user.setPassword_user(passwordEncoder.encode(user.getPassword_user()));
        user.setRole_user(roleStudent);
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

    public void importUsersFromCSV(MultipartFile file) {
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] header = reader.readNext(); // Lire l'en-tête

            // Vérification des colonnes du fichier CSV (sans activated)
            if (header == null || header.length != 6 ||
                    !"lastname_user".equals(header[0]) ||
                    !"name_user".equals(header[1]) ||
                    !"address_user".equals(header[2]) ||
                    !"email".equals(header[3]) ||
                    !"school_id".equals(header[4]) ||
                    !"picture_user".equals(header[5])) {

                throw new IllegalArgumentException("CSV file has incorrect column headers or order.");
            }

            String[] nextLine;

            while ((nextLine = reader.readNext()) != null) {
                // Extraction des données (sans activated)
                String lastname = nextLine[0];
                String firstname = nextLine[1];
                String address = nextLine[2];
                String email = nextLine[3];
                int schoolId = Integer.parseInt(nextLine[4]);
                String pictureUrl = nextLine[5];

                // Rôle par défaut (STUDENT)
                Role defaultRole = roleServiceDB.getRoleByName("STUDENT")
                        .orElseThrow(() -> new RuntimeException("Role STUDENT not found"));

                // Recherche de l'école par ID
                School school = schoolServiceDB.getSchoolById(schoolId)
                        .orElseThrow(() -> new RuntimeException("School not found with ID: " + schoolId));

                // Création d'un nouvel utilisateur
                User user = new User();
                user.setLastname_user(lastname);
                user.setName_user(firstname);
                user.setEmail(email);
                user.setAddress_user(address);
                user.setSchool_user(school);
                user.setRole_user(defaultRole);
                user.setActivated(false);  // Activated est false par défaut

                // Si pictureUrl est vide ou null, ne pas l'assigner
                if (pictureUrl != null && !pictureUrl.isEmpty()) {
                    user.setPicture_user(pictureUrl);
                }

                // Enregistrement dans la base de données
                repository.save(user);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error importing users from CSV", e);
        }
    }
}
