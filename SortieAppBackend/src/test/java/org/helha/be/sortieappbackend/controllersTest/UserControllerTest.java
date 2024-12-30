/**
 * Unit tests for the UserController class.
 * This class uses JUnit and Mockito to test the functionality of UserController,
 * which is responsible for handling HTTP requests related to User entities.
 */
package org.helha.be.sortieappbackend.controllersTest;

import org.helha.be.sortieappbackend.controllers.UserController;
import org.helha.be.sortieappbackend.models.Role;
import org.helha.be.sortieappbackend.models.School;
import org.helha.be.sortieappbackend.models.User;
import org.helha.be.sortieappbackend.services.UserServiceDB;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for {@link UserController}.
 */
public class UserControllerTest {

    /**
     * Mocked service for managing User entities.
     */
    @Mock
    private UserServiceDB serviceDB;

    /**
     * The UserController instance being tested, with mocked dependencies injected.
     */
    @InjectMocks
    private UserController controller;

    /**
     * MockMvc instance for simulating HTTP requests to the controller.
     */
    private MockMvc mockMvc;

    /**
     * Sets up the test environment by initializing Mockito mocks and configuring MockMvc.
     */
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    /**
     * Tests the {@link UserController#getUsers()} method.
     * Ensures the controller retrieves all users from the service and returns them in JSON format.
     *
     * @throws Exception if an error occurs during request processing.
     */
    @Test
    public void testGetUsers() throws Exception {
        Role role = new Role(1, "Admin Local", null);
        School school = new School(1,"HelHa montignies","Rue Trieu Kaisin 136", new ArrayList<>());
        User user1 = new User(1, "Ozudogru", "Huseyin", "hozu@helha.be", "password123", "Rue Lison 214, 6060 Gilly",school, role, true, "picture_user");
        school.getUsers_school().add(user1);
        User user2 = new User(2, "Gallet", "Noah", "ngal@helha.be", "password456", "Rue Trieu Kaisin 136, 6061 Montignies",school, role, false, "picture_user");
        school.getUsers_school().add(user2);
        List<User> users = Arrays.asList(user1, user2);

        when(serviceDB.getUsers()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name_user").value("Huseyin"))
                .andExpect(jsonPath("$[1].name_user").value("Noah"));
    }

    /**
     * Tests the {@link UserController#addUser(User)} method.
     * Ensures the controller adds a new user by delegating to the service and returns the created user.
     *
     * @throws Exception if an error occurs during request processing.
     */
    @Test
    public void testAddUser() throws Exception {
        Role role = new Role(1, "Admin Local", null);
        School school = new School(1,"HelHa montignies","Rue Trieu Kaisin 136", new ArrayList<>());
        User user = new User(1, "Ozudogru", "Huseyin", "hozu@helha.be", "password123", "Rue Lison 214, 6060 Gilly",school, role, true, "picture_user");
        school.getUsers_school().add(user);

        when(serviceDB.addUser(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"id_user\":0," +
                                "\"lastname_user\":\"Ozudogru\"," +
                                "\"name_user\":\"Huseyin\"," +
                                "\"email_user\":\"hozu@helha.be\"," +
                                "\"password_user\":\"password123\"," +
                                "\"address_user\":\"Rue Lison 214, 6060 Gilly\"," +
                                "\"role_user\":{\"id_role\":1}," +
                                "\"isActivated_user\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name_user").value("Huseyin"));
    }

    /**
     * Tests the {@link UserController#updateUser(int, User)} method.
     * Ensures the controller updates an existing user and returns the updated user.
     *
     * @throws Exception if an error occurs during request processing.
     */
    @Test
    public void testUpdateUser() throws Exception {
        Role role = new Role(1, "Admin Local", null);
        School school = new School(1,"HelHa montignies","Rue Trieu Kaisin 136", new ArrayList<>());
        User updatedUser = new User(2, "Gallet", "Noah", "ngal@helha.be", "password456", "Rue Trieu Kaisin 136, 6061 Montignies",school, role, false, "picture_user");

        when(serviceDB.updateUser(any(User.class), eq(1))).thenReturn(updatedUser);

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"id_user\":1," +
                                "\"lastname_user\":\"Gallet\"," +
                                "\"name_user\":\"Noah\"," +
                                "\"email_user\":\"ngal@helha.be\"," +
                                "\"password_user\":\"password456\"," +
                                "\"address_user\":\"Rue Trieu Kaisin 136, 6061 Montignies\"," +
                                "\"role_user\":{\"id_role\":1}," +
                                "\"isActivated_user\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name_user").value("Noah"));
    }

    /**
     * Tests the {@link UserController#deleteUser(int)} method.
     * Ensures the controller deletes a user by delegating to the service.
     *
     * @throws Exception if an error occurs during request processing.
     */
    @Test
    public void testDeleteUser() throws Exception {
        doNothing().when(serviceDB).deleteUser(1);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(serviceDB, times(1)).deleteUser(1);
    }
}
