/**
 * REST controller for managing User-related operations.
 * Provides endpoints to perform CRUD operations on User entities.
 */
package org.helha.be.sortieappbackend.controllers;

import io.jsonwebtoken.JwtException;
import org.helha.be.sortieappbackend.models.User;
import org.helha.be.sortieappbackend.services.UserServiceDB;
import org.helha.be.sortieappbackend.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * REST controller for handling User-related HTTP requests.
 * This controller provides endpoints for creating, retrieving, updating, and deleting User entities.
 * It supports operations such as retrieving all users, adding a new user, updating an existing user,
 * and deleting a user by ID.
 */
@RestController
@RequestMapping(path = "/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    UserServiceDB serviceDB;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JWTUtils jwtUtils;

    @GetMapping(path="/getAllUsers")
    public List<User> getAllUsers() {
        return serviceDB.getAllUsers();
    }

    /**
     * Retrieves a list of all users.
     *
     * @return a list of all {@link User} objects.
     */
    @GetMapping
    public List<User> getUsers() {
        return serviceDB.getUsers();
    }

    /**
     * Adds a new User to the database.
     *
     * @param user the {@link User} object to add.
     * @return the added {@link User} object.
     */
    @PostMapping
    public User addUser(@RequestBody User user) {
        return serviceDB.addUser(user);
    }

    /**
     * Updates an existing User in the database.
     *
     * @param user    the updated {@link User} object.
     * @param id_user the ID of the {@link User} to update.
     * @return the updated {@link User} object.
     */
    @PutMapping(path = "/{id_user}")
    public User updateUser(@RequestBody User user, @PathVariable int id_user) {
        return serviceDB.updateUser(user, id_user);
    }

    /**
     * Deletes logically a User from the database by ID.
     *
     * @param id_user the ID of the {@link User} to delete.
     */
    @DeleteMapping(path = "/{id_user}")
    public void deleteUser(@PathVariable int id_user) {
        serviceDB.deleteUser(id_user);
    }

    /**
     * Deletes physically a User from the database by ID.
     *
     * @param id_user the ID of the {@link User} to delete.
     */
    @DeleteMapping(path="/delete/{id_user}")
    public void deletePhysically(@PathVariable int id_user) {
        serviceDB.deleteUserPhysically(id_user);
    }

    /**
     * Endpoint to retrieve the profile information of the currently connected user.
     *
     * This endpoint extracts the user ID from the provided JWT token in the Authorization header.
     * If the token is missing, invalid, or if the user cannot be found, appropriate error responses
     * are returned.
     *
     * @param authHeader the Authorization header containing the JWT token in the format "Bearer <token>".
     * @return a {@link ResponseEntity} containing the user profile information if the token is valid,
     *         or an error message with the appropriate HTTP status code otherwise.
     *         - {@code 403 Forbidden}: if the Authorization header is missing.
     *         - {@code 401 Unauthorized}: if the token is invalid.
     *         - {@code 404 Not Found}: if the user does not exist.
     */
    @GetMapping(path = "/profile")
    public ResponseEntity<?> getProfile(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || authHeader.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Authorization header is missing");
        }
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;

        try {
            int userId = jwtUtils.getUserIdFromToken(token);
            User user = serviceDB.getUserById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
            if (user != null) {
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

    @PostMapping("/import")
    public ResponseEntity<String> importUsersFromCSV(@RequestParam("file") MultipartFile file) {
        try {
            serviceDB.importUsersFromCSV(file);
            return ResponseEntity.ok("Users imported successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to import users: " + e.getMessage());
        }
    }

}
