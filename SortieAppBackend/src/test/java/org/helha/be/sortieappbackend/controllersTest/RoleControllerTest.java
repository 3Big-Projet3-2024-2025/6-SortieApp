/**
 * Unit tests for the RoleController class.
 * This class uses JUnit and Mockito to test the functionality of RoleController,
 * which is responsible for handling HTTP requests related to Role entities.
 */
package org.helha.be.sortieappbackend.controllersTest;

import org.helha.be.sortieappbackend.controllers.RoleController;
import org.helha.be.sortieappbackend.models.Role;
import org.helha.be.sortieappbackend.services.RoleServiceDB;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for {@link RoleController}.
 */
public class RoleControllerTest {

    /**
     * Mocked service for managing Role entities.
     */
    @Mock
    private RoleServiceDB serviceDB;

    /**
     * The RoleController instance being tested, with mocked dependencies injected.
     */
    @InjectMocks
    private RoleController controller;

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
     * Tests the {@link RoleController#getRoles()} method.
     * Ensures the controller retrieves all roles from the service and returns them in JSON format.
     *
     * @throws Exception if an error occurs during request processing.
     */
    @Test
    public void testGetRoles() throws Exception {
        Role role1 = new Role(1, "Admin Local", null);
        Role role2 = new Role(2, "Eleve", null);
        List<Role> roles = Arrays.asList(role1, role2);

        when(serviceDB.getRoles()).thenReturn(roles);

        mockMvc.perform(get("/roles"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name_role").value("Admin Local"))
                .andExpect(jsonPath("$[1].name_role").value("Eleve"));
    }

    /**
     * Tests the {@link RoleController#addRole(Role)} method.
     * Ensures the controller adds a new role by delegating to the service and returns the created role.
     *
     * @throws Exception if an error occurs during request processing.
     */
    @Test
    public void testAddRole() throws Exception {
        Role role = new Role(1, "Admin Local", null);

        when(serviceDB.addRole(any(Role.class))).thenReturn(role);

        mockMvc.perform(post("/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id_role\":0,\"name_role\":\"Admin Local\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name_role").value("Admin Local"));
    }

    /**
     * Tests the {@link RoleController#updateRole(int, Role)} method.
     * Ensures the controller updates an existing role and returns the updated role.
     *
     * @throws Exception if an error occurs during request processing.
     */
    @Test
    public void testUpdateRole() throws Exception {
        Role updatedRole = new Role(1, "Eleve", null);

        when(serviceDB.updateRole(any(Role.class), eq(1))).thenReturn(updatedRole);

        mockMvc.perform(put("/roles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id_role\":1,\"name_role\":\"Eleve\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name_role").value("Eleve"));
    }

    /**
     * Tests the {@link RoleController#deleteRole(int)} method.
     * Ensures the controller deletes a role by delegating to the service.
     *
     * @throws Exception if an error occurs during request processing.
     */
    @Test
    public void testDeleteRole() throws Exception {
        doNothing().when(serviceDB).deleteRole(1);

        mockMvc.perform(delete("/roles/1"))
                .andExpect(status().isOk());

        verify(serviceDB, times(1)).deleteRole(1);
    }
}
