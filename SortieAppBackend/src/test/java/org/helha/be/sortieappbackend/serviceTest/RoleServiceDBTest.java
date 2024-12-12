package org.helha.be.sortieappbackend.serviceTest;

import org.helha.be.sortieappbackend.models.Role;
import org.helha.be.sortieappbackend.repositories.jpa.RoleRepository;
import org.helha.be.sortieappbackend.services.RoleServiceDB;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit test class for the RoleServiceDB.
 *
 * This class tests the functionality of the RoleServiceDB service layer, ensuring
 * the correct behavior for CRUD operations related to the Role entity.
 * It uses JUnit 5 for testing and Mockito for mocking dependencies.
 */
public class RoleServiceDBTest {

    @Mock
    private RoleRepository repository;

    @InjectMocks
    private RoleServiceDB serviceDB;

    /**
     * Initializes the mock environment before each test.
     */
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Tests the retrieval of all roles from the database.
     * Ensures the service returns the expected list of roles.
     */
    @Test
    public void testGetRoles() {
        Role role1 = new Role(1, "Admin Local", null);
        Role role2 = new Role(2, "Eleve", null);
        when(repository.findAll()).thenReturn(Arrays.asList(role1, role2));

        List<Role> roles = serviceDB.getRoles();

        assertEquals(2, roles.size());
        assertEquals("Admin Local", roles.get(0).getName_role());
        assertEquals("Eleve", roles.get(1).getName_role());
    }

    /**
     * Tests the retrieval of a specific role by ID.
     * Verifies that the correct role is returned if it exists.
     */
    @Test
    public void testGetRoleById() {
        Role role = new Role(1, "Admin Local", null);
        when(repository.findById(1)).thenReturn(Optional.of(role));

        Optional<Role> result = serviceDB.getRoleById(1);

        assertTrue(result.isPresent());
        assertEquals("Admin Local", result.get().getName_role());
    }

    /**
     * Tests the addition of a new role to the database.
     * Ensures that the role is saved correctly.
     */
    @Test
    public void testAddRole() {
        Role role = new Role(0, "Admin Local", null);
        when(repository.save(role)).thenReturn(new Role(1, "Admin Local", null));

        Role result = serviceDB.addRole(role);

        assertNotNull(result);
        assertEquals(1, result.getId_role());
        assertEquals("Admin Local", result.getName_role());
    }

    /**
     * Tests updating an existing role in the database.
     * Ensures that the role is updated with the new values.
     */
    @Test
    public void testUpdateRole() {
        Role existingRole = new Role(1, "Admin Local", null);
        Role updatedRole = new Role(1, "Eleve", null);

        when(repository.findById(1)).thenReturn(Optional.of(existingRole));
        when(repository.save(existingRole)).thenReturn(updatedRole);

        Role result = serviceDB.updateRole(updatedRole, 1);

        assertEquals("Eleve", result.getName_role());
    }

    /**
     * Tests the deletion of a role by ID.
     * Verifies that the delete operation is called once.
     */
    @Test
    public void testDeleteRole() {
        serviceDB.deleteRole(1);
        verify(repository, times(1)).deleteById(1);
    }
}
