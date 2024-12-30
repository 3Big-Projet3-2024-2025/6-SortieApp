package org.helha.be.sortieappbackend.serviceTest;

import org.helha.be.sortieappbackend.models.Role;
import org.helha.be.sortieappbackend.models.School;
import org.helha.be.sortieappbackend.models.User;
import org.helha.be.sortieappbackend.repositories.jpa.UserRepository;
import org.helha.be.sortieappbackend.services.RoleServiceDB;
import org.helha.be.sortieappbackend.services.SchoolServiceDB;
import org.helha.be.sortieappbackend.services.UserServiceDB;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceDBTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleServiceDB roleServiceDB;

    @Mock
    private SchoolServiceDB schoolServiceDB;

    @InjectMocks
    private UserServiceDB userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUsers() {
        User user1 = new User(1, "Doe", "John", "john.doe@example.com", "password123", "123 Street", null, null, true, null);
        User user2 = new User(2, "Smith", "Jane", "jane.smith@example.com", "password456", "456 Avenue", null, null, true, null);

        when(userRepository.findByActivatedTrue()).thenReturn(Arrays.asList(user1, user2));

        var users = userService.getUsers();

        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals("John", users.get(0).getName_user());
        assertEquals("Jane", users.get(1).getName_user());

        verify(userRepository, times(1)).findByActivatedTrue();
    }

    @Test
    void testGetUserById_Found() {
        User user = new User(1, "Doe", "John", "john.doe@example.com", "password123", "123 Street", null, null, true, null);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        var result = userService.getUserById(1);

        assertTrue(result.isPresent());
        assertEquals("John", result.get().getName_user());

        verify(userRepository, times(1)).findById(1);
    }

    @Test
    void testGetUserById_NotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        var result = userService.getUserById(1);

        assertFalse(result.isPresent());

        verify(userRepository, times(1)).findById(1);
    }

    @Test
    void testAddUser() {
        Role role = new Role(1, "Admin", Collections.emptyList());
        User user = new User(0, "Doe", "John", "john.doe@example.com", "password123", "123 Street", null, role, true, null);
        User savedUser = new User(1, "Doe", "John", "john.doe@example.com", "password123", "123 Street", null, role, true, null);

        when(roleServiceDB.getRoleById(1)).thenReturn(Optional.of(role));
        when(userRepository.save(user)).thenReturn(savedUser);

        var result = userService.addUser(user);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("John", result.getName_user());
        assertEquals("Admin", result.getRole_user().getName_role());

        verify(roleServiceDB, times(1)).getRoleById(1);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testUpdateUser_Existing() {
        Role role = new Role(1, "Admin", Collections.emptyList());
        School school = new School();
        school.setId_school(1);

        User existingUser = new User(1, "Doe", "John", "john.doe@example.com", "password123", "123 Street", null, null, true, null);
        User updatedUser = new User(1, "Doe", "Johnny", "johnny.doe@example.com", "newpassword", "123 Street", school, role, true, null);

        when(userRepository.findById(1)).thenReturn(Optional.of(existingUser));
        when(roleServiceDB.getRoleById(1)).thenReturn(Optional.of(role));
        when(schoolServiceDB.getSchoolById(1)).thenReturn(Optional.of(school));
        when(userRepository.save(existingUser)).thenReturn(updatedUser);

        var result = userService.updateUser(updatedUser, 1);

        assertNotNull(result);
        assertEquals("Johnny", result.getName_user());
        assertEquals("johnny.doe@example.com", result.getEmail());
        assertEquals("Admin", result.getRole_user().getName_role());

        verify(userRepository, times(1)).findById(1);
        verify(roleServiceDB, times(1)).getRoleById(1);
        verify(schoolServiceDB, times(1)).getSchoolById(1);
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void testDeleteUser() {
        User user = new User(1, "Doe", "John", "john.doe@example.com", "password123", "123 Street", null, null, true, null);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        userService.deleteUser(1);

        assertFalse(user.getActivated());
        verify(userRepository, times(1)).findById(1);
        verify(userRepository, times(1)).save(user);
    }
}

