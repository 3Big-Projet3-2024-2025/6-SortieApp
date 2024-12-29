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
        // Forcer le rôle à "STUDENT" indépendamment de ce qui est envoyé
        Role roleStudent = roleServiceDB.getRoleByName("STUDENT")
                .orElseThrow(() -> new IllegalArgumentException("Role STUDENT not found"));

        // Associer le rôle "STUDENT" à l'utilisateur
        user.setRole_user(roleStudent);

        // Valider et convertir l'image si nécessaire (désactivé pour l'instant)
    /*
    if (user.getPicture_user() != null && !user.getPicture_user().isEmpty()) {
        convertImageToBase64(user.getPicture_user());
    }
    */

        // Sauvegarder l'utilisateur
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
                        user.setPassword_user(newUser.getPassword_user());
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

                    // Validate and update Base64 image
                    if (newUser.getPicture_user() != null && !newUser.getPicture_user().isEmpty()) {
                        convertImageToBase64(newUser.getPicture_user());
                        user.setPicture_user(newUser.getPicture_user());
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


    /**
     * Validate, resize, and encode the image to Base64 format.
     *
     * @param base64Image the Base64-encoded image string
     * @return the compressed and validated Base64 image string
     */
    public String convertImageToBase64(String base64Image) {
        try {
            // Decode the Base64 string to get the binary data
            byte[] decodedBytes = Base64.getDecoder().decode(base64Image);

            if (decodedBytes.length == 0) {
                throw new IllegalArgumentException("Image Base64 is empty");
            }

            // Convert binary data to a BufferedImage
            ByteArrayInputStream inputStream = new ByteArrayInputStream(decodedBytes);
            BufferedImage originalImage = ImageIO.read(inputStream);

            if (originalImage == null) {
                throw new IllegalArgumentException("Invalid Base64 image provided");
            }

            // Resize the image to 400x400 pixels while maintaining aspect ratio
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Thumbnails.of(originalImage)
                    .size(400, 400) // Resize to 400x400 pixels
                    .outputQuality(0.85) // Set image quality to 85%
                    .toOutputStream(outputStream);

            // Convert resized image back to Base64
            byte[] resizedBytes = outputStream.toByteArray();
            return Base64.getEncoder().encodeToString(resizedBytes);

        } catch (Exception e) {
            throw new IllegalArgumentException("Error processing Base64 image", e);
        }
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
