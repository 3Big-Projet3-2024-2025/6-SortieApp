/**
 * REST controller for managing User-related operations.
 * Provides endpoints to perform CRUD operations on User entities.
 */
package org.helha.be.sortieappbackend.controllers;

import io.jsonwebtoken.JwtException;
import jakarta.transaction.Transactional;
import org.helha.be.sortieappbackend.models.ActivationToken;
import org.helha.be.sortieappbackend.models.User;
import org.helha.be.sortieappbackend.repositories.jpa.ActivationTokenRepository;
import org.helha.be.sortieappbackend.services.UserServiceDB;
import org.helha.be.sortieappbackend.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @Autowired
    private ActivationTokenRepository activationTokenRepository;


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


    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Integer id) {
        if (id == null) {
            return ResponseEntity.badRequest().body(null);
        }
        User user = serviceDB.getUserById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(user);
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

    /**
     * Handles HTTP POST requests to import users from a CSV file.
     *
     * @param file the CSV file containing user data, uploaded as a multipart file.
     * @return a {@link ResponseEntity} containing a success message if the import
     *         is successful, or an error message if the import fails.
     */

    @PostMapping("/import")
    public ResponseEntity<String> importUsersFromCSV(@RequestParam("file") MultipartFile file) {
        try {
            serviceDB.importUsersFromCSV(file);
            return ResponseEntity.ok("Users imported successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to import users: " + e.getMessage());
        }
    }
    @PostMapping("/importUsersForAdmin")
    public ResponseEntity<?> importUsersForAdmin(
            @RequestParam("file") MultipartFile file,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || authHeader.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Authorization header is missing");
        }
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        try {
            int userId = jwtUtils.getUserIdFromToken(token);
            User user = serviceDB.getUserById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
            if (user != null) {
                serviceDB.importUsersFromCSVForAdmin(file, userId);
                return ResponseEntity.ok("Users imported successfully and assigned to admin's school.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e);
        }
    }
    /**
     * Updates the profile picture of the currently connected user.
     *
     * @param authHeader   the Authorization header containing the JWT token.
     * @param payload      a JSON payload containing the new profile picture in Base64 format.
     * @return a {@link ResponseEntity} indicating the success or failure of the operation.
     */

    @PutMapping(path = "/updateProfilePicture")
    public ResponseEntity<?> updateProfilePicture(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody Map<String, String> payload) {

        // Check if Authorization header is present
        if (authHeader == null || authHeader.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Authorization header is missing");
        }

        // Extract the token
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;

        try {
            // Get the user ID from the token
            int userId = jwtUtils.getUserIdFromToken(token);

            // Extract the Base64 image from the request payload
            String base64Image = payload.get("picture_user");
            if (base64Image == null || base64Image.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Picture data is missing");
            }

            // Update the profile picture using the service
            serviceDB.updateProfilePicture(userId, base64Image);

            return ResponseEntity.ok("Profile picture updated successfully");
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        } catch (Exception e) {
            e.printStackTrace(); //debug
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating profile picture: " + e.getMessage());
        }
    }

    /**
     * Activates a user account using the activation token provided.
     *
     * @param token the activation token received by email.
     * @return a ResponseEntity indicating the result of the activation process.
     */

    @GetMapping("/activate")
    public ResponseEntity<String> activateAccount(@RequestParam String token) {
        try {
            // Searching the token in the database
            Optional<ActivationToken> optionalToken = activationTokenRepository.findByToken(token);

            if (optionalToken.isPresent()) {
                ActivationToken activationToken = optionalToken.get();

                // Verifying if token has expired
                if (activationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token has expired.");
                }

                // Activating associated user
                User user = activationToken.getUser();
                user.setActivated(true);
                serviceDB.updateUser(user, user.getId());

                // Deleting token after activation
                activationTokenRepository.delete(activationToken);

                return ResponseEntity.ok("Account activated successfully!");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid token.");
            }
        } catch (Exception e) {
            e.printStackTrace(); // for debug
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during activation.");
        }
    }

    /**
     * Processes the password reset form submitted via an HTML POST request.
     *
     * This method validates the activation token and ensures that the provided
     * password and confirmation password match. If the token is valid and the
     * passwords are valid, the associated user's password is updated, and the
     * account is activated. The activation token is deleted upon successful processing.
     *
     * @param token           The activation token provided in the request.
     * @param password        The new password entered by the user.
     * @param confirmPassword The confirmation of the new password entered by the user.
     * @return A ResponseEntity indicating the result of the operation:
     *         - HTTP 200 OK if the password is successfully updated and the account activated.
     *         - HTTP 400 Bad Request if the token is invalid, expired, or the passwords do not match.
     */

    @Transactional
    @PostMapping("/set-password")
    public ResponseEntity<String> processPasswordForm(
            @RequestParam String token,
            @RequestParam String password,
            @RequestParam String confirmPassword) {

        // Verifying if passwords match
        if (!password.equals(confirmPassword)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("""
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Passwords do not match</title>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        margin: 0;
                        padding: 0;
                        background-color: #f5f5f5;
                    }
                    .container {
                        max-width: 400px;
                        margin: 100px auto;
                        padding: 20px;
                        background-color: #ffffff;
                        border-radius: 10px;
                        box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
                        text-align: center;
                    }
                    h1 {
                        color: red;
                        font-size: 24px;
                    }
                    a {
                        display: inline-block;
                        margin-top: 20px;
                        text-decoration: none;
                        color: #003366;
                        font-weight: bold;
                    }
                    a:hover {
                        color: #0055a5;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>Passwords do not match</h1>
                    <a href="/users/activate-form?token=%s">Try Again</a>
                </div>
            </body>
            </html>
        """.formatted(token));
        }

        // Verifying if token is ok
        Optional<ActivationToken> optionalToken = activationTokenRepository.findByToken(token);
        if (optionalToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("""
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Invalid Token</title>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        margin: 0;
                        padding: 0;
                        background-color: #f5f5f5;
                    }
                    .container {
                        max-width: 400px;
                        margin: 100px auto;
                        padding: 20px;
                        background-color: #ffffff;
                        border-radius: 10px;
                        box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
                        text-align: center;
                    }
                    h1 {
                        color: red;
                        font-size: 24px;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>Invalid or already used token</h1>
                </div>
            </body>
            </html>
        """);
        }

        ActivationToken activationToken = optionalToken.get();

        // Verifying if token has expired
        if (activationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("""
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Token Expired</title>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        margin: 0;
                        padding: 0;
                        background-color: #f5f5f5;
                    }
                    .container {
                        max-width: 400px;
                        margin: 100px auto;
                        padding: 20px;
                        background-color: #ffffff;
                        border-radius: 10px;
                        box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
                        text-align: center;
                    }
                    h1 {
                        color: red;
                        font-size: 24px;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>Token has expired</h1>
                </div>
            </body>
            </html>
        """);
        }

        User user = activationToken.getUser();
        //System.out.println(password); //debug
        user.setPassword_user(password);
        user.setActivated(true);

        serviceDB.updateUser(user, user.getId());

        // Deleting token after use (for not using link twice)
        activationTokenRepository.deleteByToken(token);
        activationTokenRepository.flush(); // Forcing synchronisation of Database

        // Response if OK
        return ResponseEntity.ok("""
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Password Set</title>
            <style>
                body {
                    font-family: Arial, sans-serif;
                    margin: 0;
                    padding: 0;
                    background-color: #f5f5f5;
                }
                .container {
                    max-width: 400px;
                    margin: 100px auto;
                    padding: 20px;
                    background-color: #ffffff;
                    border-radius: 10px;
                    box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
                    text-align: center;
                }
                h1 {
                    color: green;
                    font-size: 24px;
                }
            </style>
        </head>
        <body>
            <div class="container">
                <h1>Password set successfully!</h1>
            </div>
        </body>
        </html>
    """);
    }

    /**
     * Generates an HTML form for setting a new password, based on a provided activation token.
     *
     * This endpoint validates the provided token and, if valid, returns a styled HTML form where
     * the user can enter and confirm a new password. If the token is invalid or expired,
     * an error message is returned instead.
     *
     * The HTML form includes:
     * - Input fields for the new password and confirmation password.
     * - JavaScript-based validation to ensure the passwords match and meet minimum length requirements.
     *
     * @param token the activation token provided to the user via email.
     * @return a ResponseEntity containing:
     *         - A styled HTML form if the token is valid.
     *         - An error message if the token is invalid or expired (HTTP 400 Bad Request).
     */

    @GetMapping("/activate-form")
    public ResponseEntity<String> getActivationForm(@RequestParam String token) {
        Optional<ActivationToken> optionalToken = activationTokenRepository.findByToken(token);
        if (optionalToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ERROR : Invalid or expired token. Link probably already used or expired.");
        }

        String htmlForm = "<!DOCTYPE html>" +
                "<html lang=\"en\">" +
                "<head>" +
                "    <meta charset=\"UTF-8\">" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "    <title>Set Your Password</title>" +
                "    <style>" +
                "        body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f5f5f5; }" +
                "        .container { max-width: 400px; margin: 100px auto; padding: 20px; background-color: #ffffff; " +
                "            border-radius: 10px; box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1); text-align: center; }" +
                "        .container h1 { font-size: 24px; margin-bottom: 20px; color: #003366; }" +
                "        label { display: block; text-align: left; margin-bottom: 5px; font-weight: bold; color: #003366; }" +
                "        input[type=\"password\"] { width: 100%; padding: 10px; margin-bottom: 15px; border: 1px solid #cccccc; border-radius: 5px; box-sizing: border-box; }" +
                "        button { width: 100%; padding: 10px; background-color: #003366; color: #ffffff; border: none; border-radius: 5px; font-size: 16px; cursor: pointer; transition: background-color 0.3s; }" +
                "        button:hover { background-color: #0055a5; }" +
                "        #message { color: red; margin-bottom: 15px; }" +
                "        .footer { margin-top: 20px; font-size: 12px; color: #777777; }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class=\"container\">" +
                "        <h1>Set Your Password</h1>" +
                "        <form id=\"passwordForm\" action=\"/users/set-password\" method=\"POST\">" +
                "            <input type=\"hidden\" name=\"token\" value=\"" + token + "\">" +
                "            <label for=\"password\">New Password:</label>" +
                "            <input type=\"password\" id=\"password\" name=\"password\" required>" +
                "            <label for=\"confirmPassword\">Confirm Password:</label>" +
                "            <input type=\"password\" id=\"confirmPassword\" name=\"confirmPassword\" required>" +
                "            <span id=\"message\"></span>" +
                "            <button type=\"submit\">Submit</button>" +
                "        </form>" +
                "        <div class=\"footer\">© 2024 Sortie'App.</div>" +
                "    </div>" +
                "    <script>" +
                "        document.getElementById(\"passwordForm\").addEventListener(\"submit\", function(event) {" +
                "            const password = document.getElementById(\"password\").value;" +
                "            const confirmPassword = document.getElementById(\"confirmPassword\").value;" +
                "            const message = document.getElementById(\"message\");" +
                "            message.textContent = \"\";" +
                "            if (password !== confirmPassword) {" +
                "                event.preventDefault();" +
                "                message.textContent = \"Passwords do not match.\";" +
                "            } else if (password.length < 8) {" +
                "                event.preventDefault();" +
                "                message.textContent = \"Password must be at least 8 characters.\";" +
                "            }" +
                "        });" +
                "    </script>" +
                "</body>" +
                "</html>";
        return ResponseEntity.ok(htmlForm);
    }
}