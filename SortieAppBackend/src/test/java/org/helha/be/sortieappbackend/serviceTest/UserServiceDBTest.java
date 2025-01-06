package org.helha.be.sortieappbackend.serviceTest;

import org.helha.be.sortieappbackend.ServiceImpl.QRCodeServiceImpl;
import org.helha.be.sortieappbackend.models.*;
import org.helha.be.sortieappbackend.repositories.jpa.ActivationTokenRepository;
import org.helha.be.sortieappbackend.repositories.jpa.UserRepository;
import org.helha.be.sortieappbackend.services.EmailService;
import org.helha.be.sortieappbackend.services.RoleServiceDB;
import org.helha.be.sortieappbackend.services.SchoolServiceDB;
import org.helha.be.sortieappbackend.services.UserServiceDB;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.imageio.ImageIO;
import jakarta.mail.MessagingException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ActivationTokenRepository activationTokenRepository;

    @Mock
    private RoleServiceDB roleServiceDB;

    @Mock
    private SchoolServiceDB schoolServiceDB;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private QRCodeServiceImpl qrCodeServiceImpl;

    @InjectMocks
    private UserServiceDB userService;

    private User userMock;
    private Role roleMock;
    private School schoolMock;
    private ActivationToken activationTokenMock;

    @BeforeEach
    void setup() {
        userMock = new User();
        userMock.setId(1);
        userMock.setEmail("test@example.com");
        userMock.setName_user("John");
        userMock.setLastname_user("Doe");

        roleMock = new Role();
        roleMock.setId_role(2);
        roleMock.setName_role("STUDENT");

        schoolMock = new School();
        schoolMock.setId_school(10);
        schoolMock.setName_school("Test School");

        activationTokenMock = new ActivationToken();
        activationTokenMock.setToken("testToken");
        activationTokenMock.setUser(userMock);
        activationTokenMock.setExpiryDate(LocalDateTime.now().plusHours(24));
    }

    @Test
    void testGetAllUsers_ShouldReturnListOfUsers() {
        List<User> userList = List.of(userMock);
        when(userRepository.findAll()).thenReturn(userList);

        List<User> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("test@example.com", result.get(0).getEmail());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetUsers_ShouldReturnActivatedUsers() {
        User activatedUser = new User();
        activatedUser.setActivated(true);
        when(userRepository.findByActivatedTrue()).thenReturn(List.of(activatedUser));

        List<User> result = userService.getUsers();

        assertEquals(1, result.size());
        assertTrue(result.get(0).getActivated());
        verify(userRepository, times(1)).findByActivatedTrue();
    }

    @Test
    void testGetUserById_ShouldReturnOptionalOfUser() {
        when(userRepository.findById(1)).thenReturn(Optional.of(userMock));

        Optional<User> result = userService.getUserById(1);

        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
        verify(userRepository, times(1)).findById(1);
    }

    @Test
    void testGetUserById_ShouldReturnEmpty_WhenNotFound() {
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById(999);

        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findById(999);
    }

    @Test
    void testAddUser_ShouldAddNewUserAndSendActivationEmail() throws MessagingException {
        userMock.setRole_user(roleMock);
        when(roleServiceDB.getRoleById(roleMock.getId_role())).thenReturn(Optional.of(roleMock));
        when(userRepository.save(any(User.class))).thenReturn(userMock);
        when(emailService.generateActivationToken()).thenReturn("generatedToken");

        User savedUser = userService.addUser(userMock);

        // Assertions
        assertNotNull(savedUser);
        assertEquals("test@example.com", savedUser.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
        verify(activationTokenRepository, times(1)).save(any(ActivationToken.class));
        verify(emailService, times(1)).sendActivationEmail(eq("test@example.com"), eq("generatedToken"));
    }

    @Test
    void testAddUser_ShouldThrowException_WhenRoleNotFound() {
        userMock.setRole_user(roleMock);
        when(roleServiceDB.getRoleById(roleMock.getId_role())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.addUser(userMock));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testAddUser_ShouldThrowRuntimeException_WhenFailedToSendEmail() throws MessagingException {
        userMock.setRole_user(roleMock);
        when(roleServiceDB.getRoleById(roleMock.getId_role())).thenReturn(Optional.of(roleMock));
        when(userRepository.save(any(User.class))).thenReturn(userMock);
        when(emailService.generateActivationToken()).thenReturn("generatedToken");
        doThrow(new MessagingException("Email error")).when(emailService).sendActivationEmail(anyString(), anyString());

        assertThrows(RuntimeException.class, () -> userService.addUser(userMock));
        verify(userRepository, times(1)).save(any(User.class));
        verify(activationTokenRepository, times(1)).save(any(ActivationToken.class));
    }

    @Test
    void testUpdateUser_ShouldUpdateExistingUser() {
        // Nouvel utilisateur avec de nouvelles infos
        User newUserData = new User();
        newUserData.setName_user("Jane");
        newUserData.setLastname_user("Smith");
        newUserData.setPassword_user("newPassword");
        newUserData.setEmail("jane@example.com");
        newUserData.setSchool_user(schoolMock);
        newUserData.setRole_user(roleMock);

        when(userRepository.findById(1)).thenReturn(Optional.of(userMock));
        when(roleServiceDB.getRoleById(roleMock.getId_role())).thenReturn(Optional.of(roleMock));
        when(schoolServiceDB.getSchoolById(schoolMock.getId_school())).thenReturn(Optional.of(schoolMock));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(userMock);

        User result = userService.updateUser(newUserData, 1);

        assertNotNull(result);
        assertEquals("Jane", result.getName_user());
        assertEquals("Smith", result.getLastname_user());
        assertEquals("encodedPassword", result.getPassword_user());
        assertEquals(schoolMock, result.getSchool_user());
        assertEquals(roleMock, result.getRole_user());
        verify(userRepository, times(1)).findById(1);
        verify(userRepository, times(1)).save(userMock);
    }

    @Test
    void testUpdateUser_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(999)).thenReturn(Optional.empty());
        User newUserData = new User();

        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(newUserData, 999));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testDeleteUser_ShouldDeactivateUser() {
        userMock.setActivated(true);
        when(userRepository.findById(1)).thenReturn(Optional.of(userMock));

        userService.deleteUser(1);

        assertFalse(userMock.getActivated());
        verify(userRepository, times(1)).save(userMock);
    }

    @Test
    void testDeleteUser_ShouldDoNothingIfUserNotFound() {
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        userService.deleteUser(999);

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testDeleteUserPhysically_ShouldDeleteUser() {
        doNothing().when(userRepository).deleteById(1);

        userService.deleteUserPhysically(1);

        verify(userRepository, times(1)).deleteById(1);
    }

    @Test
    void testUpdateProfilePicture_ShouldUpdateUserPicture() throws Exception {
        when(userRepository.findById(1)).thenReturn(Optional.of(userMock));

        BufferedImage testImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(testImage, "jpg", baos);
        String base64Image = Base64.getEncoder().encodeToString(baos.toByteArray());

        userService.updateProfilePicture(1, base64Image);

        assertNotNull(userMock.getPicture_user());
        verify(userRepository, times(1)).save(userMock);
    }

    @Test
    void testUpdateProfilePicture_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.updateProfilePicture(999, "someBase64"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateProfilePicture_ShouldThrowException_WhenBase64Invalid() {
        when(userRepository.findById(1)).thenReturn(Optional.of(userMock));

        String invalidBase64 = "???---???";

        assertThrows(RuntimeException.class, () -> userService.updateProfilePicture(1, invalidBase64));
        verify(userRepository, never()).save(any(User.class));
    }
}
