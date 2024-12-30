/**
 * Unit tests for the UserServiceDB class.
 * This class uses JUnit and Mockito to test the functionality of UserServiceDB,
 * which is responsible for managing User entities in the application.
 */
package org.helha.be.sortieappbackend.serviceTest;

import org.helha.be.sortieappbackend.models.Role;
import org.helha.be.sortieappbackend.models.School;
import org.helha.be.sortieappbackend.models.User;
import org.helha.be.sortieappbackend.repositories.jpa.UserRepository;
import org.helha.be.sortieappbackend.services.RoleServiceDB;
import org.helha.be.sortieappbackend.services.UserServiceDB;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link UserServiceDB}.
 */
public class UserServiceDBTest {

    /**
     * Mocked repository for accessing User data.
     */
    @Mock
    private UserRepository repository;

    /**
     * Mocked service for managing Role entities.
     */
    @Mock
    private RoleServiceDB roleServiceDB;

    /**
     * The UserServiceDB instance being tested, with mocked dependencies injected.
     */
    @InjectMocks
    private UserServiceDB serviceDB;

    /**
     * Sets up the test environment by initializing Mockito mocks.
     */
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Tests the {@link UserServiceDB#getUsers()} method.
     * Ensures the service retrieves all users from the repository correctly.
     */
    @Test
    public void testGetUsers() {
        Role role = new Role(1, "Admin Local", null);
        School school = new School(1,"HelHa montignies","Rue Trieu Kaisin 136", new ArrayList<>());
        User user1 = new User(1, "Ozudogru", "Huseyin", "hozu@helha.be", "password123", "Rue Lison 214, 6060 Gilly",school, role, true, "picture_user");
        User user2 = new User(2, "Gallet", "Noah", "ngal@helha.be", "password456", "Rue Trieu Kaisin 136, 6061 Montignies",school, role, false, "picture_user");
        when(repository.findAll()).thenReturn(Arrays.asList(user1, user2));

        List<User> users = serviceDB.getUsers();

        assertEquals(2, users.size());
        assertEquals("Huseyin", users.get(0).getName_user());
        assertEquals("Noah", users.get(1).getName_user());
    }

    /**
     * Tests the {@link UserServiceDB#addUser(User)} method.
     * Ensures the service adds a new user correctly by saving it to the repository.
     */
    @Test
    public void testAddUser() {
        Role role = new Role(1, "Admin Local", null);
        School school = new School(1,"HelHa montignies","Rue Trieu Kaisin 136", new ArrayList<>());
        User user = new User(1, "Ozudogru", "Huseyin", "hozu@helha.be", "password123", "Rue Lison 214, 6060 Gilly",school, role, true, "picture_user");
        when(roleServiceDB.getRoleById(1)).thenReturn(Optional.of(role));
        when(repository.save(any(User.class))).thenReturn(user);

        User result = serviceDB.addUser(user);

        assertNotNull(result);
        assertEquals("Huseyin", result.getName_user());
        assertEquals("hozu@helha.be", result.getEmail());
    }

    /**
     * Tests the {@link UserServiceDB#updateUser(User, int)} method.
     * Ensures the service updates an existing user correctly by modifying its details.
     */
    @Test
    public void testUpdateUser() {
        Role role = new Role(1, "Admin Local", null);
        School school = new School(1,"HelHa montignies","Rue Trieu Kaisin 136", new ArrayList<>());
        User existingUser = new User(1, "Ozudogru", "Huseyin", "hozu@helha.be", "password123", "Rue Lison 214, 6060 Gilly",school, role, true, "picture_user");
        User updatedUser = new User(2, "Gallet", "Noah", "ngal@helha.be", "password456", "Rue Trieu Kaisin 136, 6061 Montignies",school, role, false, "picture_user");

        when(repository.findById(1)).thenReturn(Optional.of(existingUser));
        when(roleServiceDB.getRoleById(1)).thenReturn(Optional.of(role));
        when(repository.save(any(User.class))).thenReturn(updatedUser);

        User result = serviceDB.updateUser(updatedUser, 1);

        assertEquals("Noah", result.getName_user());
        assertEquals("ngal@helha.be", result.getEmail());
    }

    /**
     * Tests the {@link UserServiceDB#deleteUser(int)} method.
     * Ensures the service deletes a user by ID correctly.
     */
    @Test
    public void testDeleteUser() {
        doNothing().when(repository).deleteById(1);

        serviceDB.deleteUser(1);

        verify(repository, times(1)).deleteById(1);
    }
}
