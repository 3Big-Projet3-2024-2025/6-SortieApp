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
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoleServiceDBTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceDB roleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetRoles() {
        Role role1 = new Role(1, "Admin", Collections.emptyList());
        Role role2 = new Role(2, "User", Collections.emptyList());

        when(roleRepository.findAll()).thenReturn(Arrays.asList(role1, role2));

        var roles = roleService.getRoles();

        assertNotNull(roles);
        assertEquals(2, roles.size());
        assertEquals("Admin", roles.get(0).getName_role());
        assertEquals("User", roles.get(1).getName_role());

        verify(roleRepository, times(1)).findAll();
    }

    @Test
    void testGetRoleById_Found() {
        Role role = new Role(1, "Admin", Collections.emptyList());

        when(roleRepository.findById(1)).thenReturn(Optional.of(role));

        var result = roleService.getRoleById(1);

        assertTrue(result.isPresent());
        assertEquals("Admin", result.get().getName_role());

        verify(roleRepository, times(1)).findById(1);
    }

    @Test
    void testGetRoleById_NotFound() {
        when(roleRepository.findById(1)).thenReturn(Optional.empty());

        var result = roleService.getRoleById(1);

        assertFalse(result.isPresent());

        verify(roleRepository, times(1)).findById(1);
    }

    @Test
    void testAddRole() {
        Role role = new Role(0, "Manager", Collections.emptyList());
        Role savedRole = new Role(3, "Manager", Collections.emptyList());

        when(roleRepository.save(role)).thenReturn(savedRole);

        var result = roleService.addRole(role);

        assertNotNull(result);
        assertEquals(3, result.getId_role());
        assertEquals("Manager", result.getName_role());

        verify(roleRepository, times(1)).save(role);
    }

    @Test
    void testUpdateRole_Existing() {
        Role existingRole = new Role(1, "Admin", Collections.emptyList());
        Role updatedRole = new Role(1, "Super Admin", Collections.emptyList());

        when(roleRepository.findById(1)).thenReturn(Optional.of(existingRole));
        when(roleRepository.save(existingRole)).thenReturn(updatedRole);

        var result = roleService.updateRole(new Role(1, "Super Admin", null), 1);

        assertNotNull(result);
        assertEquals("Super Admin", result.getName_role());

        verify(roleRepository, times(1)).findById(1);
        verify(roleRepository, times(1)).save(existingRole);
    }

    @Test
    void testUpdateRole_New() {
        Role newRole = new Role(0, "Manager", Collections.emptyList());

        when(roleRepository.findById(1)).thenReturn(Optional.empty());
        when(roleRepository.save(newRole)).thenReturn(newRole);

        var result = roleService.updateRole(newRole, 1);

        assertNotNull(result);
        assertEquals("Manager", result.getName_role());

        verify(roleRepository, times(1)).findById(1);
        verify(roleRepository, times(1)).save(newRole);
    }

    @Test
    void testDeleteRole() {
        doNothing().when(roleRepository).deleteById(1);

        roleService.deleteRole(1);

        verify(roleRepository, times(1)).deleteById(1);
    }
}

