package org.helha.be.sortieappbackend.controllersTest;

import io.jsonwebtoken.JwtException;
import org.helha.be.sortieappbackend.controllers.UserController;
import org.helha.be.sortieappbackend.models.ActivationToken;
import org.helha.be.sortieappbackend.models.User;
import org.helha.be.sortieappbackend.repositories.jpa.ActivationTokenRepository;
import org.helha.be.sortieappbackend.services.UserServiceDB;
import org.helha.be.sortieappbackend.utils.JWTUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserServiceDB serviceDB;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JWTUtils jwtUtils;

    @Mock
    private ActivationTokenRepository activationTokenRepository;

    @InjectMocks
    private UserController userController;

    private User userMock;
    private List<User> userListMock;
    private ActivationToken activationToken;

    @BeforeEach
    void setup() {
        userMock = new User();
        userMock.setId(1);
        userMock.setEmail("test@example.com");
        userMock.setActivated(false);

        userListMock = new ArrayList<>();
        userListMock.add(userMock);

        activationToken = new ActivationToken();
        activationToken.setToken("some-token-value");
        activationToken.setUser(userMock);
        activationToken.setExpiryDate(LocalDateTime.now().plusDays(1));
    }

    @Test
    void testGetAllUsers_ShouldReturnListOfUsers() {
        when(serviceDB.getAllUsers()).thenReturn(userListMock);

        List<User> result = userController.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(serviceDB, times(1)).getAllUsers();
    }

    @Test
    void testGetUsers_ShouldReturnListOfUsers() {
        when(serviceDB.getUsers()).thenReturn(userListMock);

        List<User> result = userController.getUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(serviceDB, times(1)).getUsers();
    }

    @Test
    void testGetUserById_ShouldReturnUser_WhenUserFound() {
        when(serviceDB.getUserById(1)).thenReturn(Optional.of(userMock));

        ResponseEntity<User> response = userController.getUserById(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(userMock.getEmail(), response.getBody().getEmail());
        verify(serviceDB, times(1)).getUserById(1);
    }

    @Test
    void testGetUserById_ShouldReturnBadRequest_WhenIdIsNull() {
        ResponseEntity<User> response = userController.getUserById(null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verifyNoInteractions(serviceDB);
    }

    @Test
    void testGetUserById_ShouldReturnNotFound_WhenUserNotFound() {
        when(serviceDB.getUserById(999)).thenReturn(Optional.empty());

        ResponseEntity<User> response = userController.getUserById(999);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testAddUser_ShouldReturnAddedUser() {
        User newUser = new User();
        newUser.setId(2);
        newUser.setEmail("new@example.com");
        when(serviceDB.addUser(any(User.class))).thenReturn(newUser);

        User result = userController.addUser(newUser);

        assertNotNull(result);
        assertEquals("new@example.com", result.getEmail());
        verify(serviceDB, times(1)).addUser(newUser);
    }

    @Test
    void testUpdateUser_ShouldReturnUpdatedUser() {
        User updatedUser = new User();
        updatedUser.setId(1);
        updatedUser.setEmail("updated@example.com");

        when(serviceDB.updateUser(any(User.class), eq(1))).thenReturn(updatedUser);

        User result = userController.updateUser(updatedUser, 1);

        assertNotNull(result);
        assertEquals("updated@example.com", result.getEmail());
        verify(serviceDB, times(1)).updateUser(updatedUser, 1);
    }

    @Test
    void testDeleteUser_ShouldCallService() {
        doNothing().when(serviceDB).deleteUser(1);

        userController.deleteUser(1);

        verify(serviceDB, times(1)).deleteUser(1);
    }

    @Test
    void testDeletePhysically_ShouldCallService() {
        doNothing().when(serviceDB).deleteUserPhysically(1);

        userController.deletePhysically(1);

        verify(serviceDB, times(1)).deleteUserPhysically(1);
    }

    @Test
    void testGetProfile_ShouldReturnUser_WhenTokenIsValid() {
        String validToken = "validToken";
        when(jwtUtils.getUserIdFromToken(validToken)).thenReturn(1);
        when(serviceDB.getUserById(1)).thenReturn(Optional.of(userMock));

        ResponseEntity<?> response = userController.getProfile("Bearer " + validToken);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof User);
        verify(jwtUtils, times(1)).getUserIdFromToken(validToken);
        verify(serviceDB, times(1)).getUserById(1);
    }

    @Test
    void testGetProfile_ShouldReturnForbidden_WhenAuthHeaderIsMissing() {
        ResponseEntity<?> response = userController.getProfile(null);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Authorization header is missing", response.getBody());
        verifyNoInteractions(jwtUtils);
        verifyNoInteractions(serviceDB);
    }

    @Test
    void testGetProfile_ShouldReturnUnauthorized_WhenTokenInvalid() {
        String invalidToken = "invalidToken";
        when(jwtUtils.getUserIdFromToken(invalidToken)).thenThrow(new JwtException("Invalid token"));

        ResponseEntity<?> response = userController.getProfile("Bearer " + invalidToken);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid token", response.getBody());
    }

    @Test
    void testImportUsersFromCSV_ShouldReturnOk_WhenSuccess() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        doNothing().when(serviceDB).importUsersFromCSV(file);

        ResponseEntity<String> response = userController.importUsersFromCSV(file);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Users imported successfully.", response.getBody());
        verify(serviceDB, times(1)).importUsersFromCSV(file);
    }

    @Test
    void testImportUsersFromCSV_ShouldReturnBadRequest_WhenExceptionThrown() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        doThrow(new RuntimeException("Error")).when(serviceDB).importUsersFromCSV(file);

        ResponseEntity<String> response = userController.importUsersFromCSV(file);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Failed to import users: Error"));
    }

    @Test
    void testImportUsersForAdmin_ShouldReturnOk_WhenSuccess() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        String token = "someValidToken";
        when(jwtUtils.getUserIdFromToken(token)).thenReturn(1);
        when(serviceDB.getUserById(1)).thenReturn(Optional.of(userMock));
        doNothing().when(serviceDB).importUsersFromCSVForAdmin(file, 1);

        ResponseEntity<?> response = userController.importUsersForAdmin(file, "Bearer " + token);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Users imported successfully and assigned to admin's school.", response.getBody());
    }

    @Test
    void testImportUsersForAdmin_ShouldReturnForbidden_WhenHeaderMissing() {
        MultipartFile file = mock(MultipartFile.class);

        ResponseEntity<?> response = userController.importUsersForAdmin(file, null);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Authorization header is missing", response.getBody());
    }

    @Test
    void testImportUsersForAdmin_ShouldReturnUnauthorized_WhenTokenInvalid() {
        MultipartFile file = mock(MultipartFile.class);
        String invalidToken = "invalid";
        when(jwtUtils.getUserIdFromToken(invalidToken)).thenThrow(new JwtException("Invalid token"));

        ResponseEntity<?> response = userController.importUsersForAdmin(file, "Bearer " + invalidToken);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid token", response.getBody());
    }

    @Test
    void testUpdateProfilePicture_ShouldReturnOk_WhenSuccess() {
        String validToken = "validToken";
        Map<String, String> payload = new HashMap<>();
        payload.put("picture_user", "base64ImageString");

        when(jwtUtils.getUserIdFromToken(validToken)).thenReturn(1);
        doNothing().when(serviceDB).updateProfilePicture(1, "base64ImageString");

        ResponseEntity<?> response = userController.updateProfilePicture("Bearer " + validToken, payload);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Profile picture updated successfully", response.getBody());
        verify(serviceDB, times(1)).updateProfilePicture(1, "base64ImageString");
    }

    @Test
    void testUpdateProfilePicture_ShouldReturnForbidden_WhenHeaderMissing() {
        Map<String, String> payload = new HashMap<>();
        payload.put("picture_user", "base64ImageString");

        ResponseEntity<?> response = userController.updateProfilePicture(null, payload);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Authorization header is missing", response.getBody());
    }

    @Test
    void testUpdateProfilePicture_ShouldReturnUnauthorized_WhenTokenInvalid() {
        String invalidToken = "invalidToken";
        Map<String, String> payload = new HashMap<>();
        payload.put("picture_user", "base64ImageString");

        when(jwtUtils.getUserIdFromToken(invalidToken)).thenThrow(new JwtException("Invalid token"));

        ResponseEntity<?> response = userController.updateProfilePicture("Bearer " + invalidToken, payload);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid token", response.getBody());
    }

    @Test
    void testUpdateProfilePicture_ShouldReturnBadRequest_WhenPictureMissing() {
        String validToken = "validToken";
        Map<String, String> emptyPayload = new HashMap<>();
        when(jwtUtils.getUserIdFromToken(validToken)).thenReturn(1);

        ResponseEntity<?> response = userController.updateProfilePicture("Bearer " + validToken, emptyPayload);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Picture data is missing", response.getBody());
    }

    @Test
    void testActivateAccount_ShouldReturnOk_WhenTokenValid() {
        when(activationTokenRepository.findByToken("some-token-value")).thenReturn(Optional.of(activationToken));
        doAnswer(invocation -> {
            userMock.setActivated(true);
            return null;
        }).when(serviceDB).updateUser(any(User.class), eq(1));

        ResponseEntity<String> response = userController.activateAccount("some-token-value");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Account activated successfully!", response.getBody());
        assertTrue(userMock.getActivated());
        verify(activationTokenRepository, times(1)).delete(activationToken);
    }

    @Test
    void testActivateAccount_ShouldReturnBadRequest_WhenTokenExpired() {
        activationToken.setExpiryDate(LocalDateTime.now().minusDays(1)); // token expiré
        when(activationTokenRepository.findByToken("expired-token")).thenReturn(Optional.of(activationToken));

        ResponseEntity<String> response = userController.activateAccount("expired-token");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Token has expired.", response.getBody());
    }

    @Test
    void testActivateAccount_ShouldReturnBadRequest_WhenTokenInvalid() {
        when(activationTokenRepository.findByToken("invalid-token")).thenReturn(Optional.empty());

        ResponseEntity<String> response = userController.activateAccount("invalid-token");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid token.", response.getBody());
    }

    @Test
    void testProcessPasswordForm_ShouldReturnOk_WhenPasswordsMatchAndTokenValid() {
        when(activationTokenRepository.findByToken("some-token-value")).thenReturn(Optional.of(activationToken));

        ResponseEntity<String> response = userController.processPasswordForm("some-token-value", "newPassword", "newPassword");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Password set successfully!"));
        assertTrue(userMock.getActivated());
        verify(activationTokenRepository, times(1)).deleteByToken("some-token-value");
    }

    @Test
    void testProcessPasswordForm_ShouldReturnBadRequest_WhenPasswordsDoNotMatch() {
        ResponseEntity<String> response = userController.processPasswordForm("some-token-value", "password1", "password2");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Passwords do not match"));
        verifyNoInteractions(activationTokenRepository);
    }

    @Test
    void testProcessPasswordForm_ShouldReturnBadRequest_WhenTokenInvalid() {
        when(activationTokenRepository.findByToken("invalid-token")).thenReturn(Optional.empty());

        ResponseEntity<String> response = userController.processPasswordForm("invalid-token", "password", "password");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Invalid or already used token"));
    }

    @Test
    void testProcessPasswordForm_ShouldReturnBadRequest_WhenTokenExpired() {
        activationToken.setExpiryDate(LocalDateTime.now().minusDays(1)); // token expiré
        when(activationTokenRepository.findByToken("expired-token")).thenReturn(Optional.of(activationToken));

        ResponseEntity<String> response = userController.processPasswordForm("expired-token", "password", "password");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Token has expired"));
    }

    @Test
    void testGetActivationForm_ShouldReturnHtmlForm_WhenTokenIsValid() {
        when(activationTokenRepository.findByToken("some-token-value")).thenReturn(Optional.of(activationToken));

        ResponseEntity<String> response = userController.getActivationForm("some-token-value");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("<html"));
        assertTrue(response.getBody().contains("Set Your Password"));
    }

    @Test
    void testGetActivationForm_ShouldReturnBadRequest_WhenTokenInvalidOrEmpty() {
        when(activationTokenRepository.findByToken("invalid-token")).thenReturn(Optional.empty());

        ResponseEntity<String> response = userController.getActivationForm("invalid-token");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("ERROR : Invalid or expired token"));
    }
}
