package org.helha.be.sortieappbackend.controllersTest;

import org.helha.be.sortieappbackend.controllers.RoleController;
import org.helha.be.sortieappbackend.models.Role;
import org.helha.be.sortieappbackend.models.User;
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
import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

class RoleControllerTest {

    @Mock
    private RoleServiceDB roleService;

    @InjectMocks
    private RoleController roleController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(roleController).build();
    }

    @Test
    void testGetRoles() throws Exception {
        Role role1 = new Role(1, "Admin", Collections.emptyList());
        Role role2 = new Role(2, "User", Collections.emptyList());

        when(roleService.getRoles()).thenReturn(Arrays.asList(role1, role2));

        mockMvc.perform(get("/roles"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id_role").value(1))
                .andExpect(jsonPath("$[0].name_role").value("Admin"))
                .andExpect(jsonPath("$[1].id_role").value(2))
                .andExpect(jsonPath("$[1].name_role").value("User"))
                .andDo(print());

        verify(roleService, times(1)).getRoles();
    }

    @Test
    void testAddRole() throws Exception {
        Role newRole = new Role(0, "Manager", Collections.emptyList());
        Role savedRole = new Role(3, "Manager", Collections.emptyList());

        when(roleService.addRole(any(Role.class))).thenReturn(savedRole);

        mockMvc.perform(post("/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name_role\":\"Manager\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id_role").value(3))
                .andExpect(jsonPath("$.name_role").value("Manager"))
                .andDo(print());

        verify(roleService, times(1)).addRole(any(Role.class));
    }

    @Test
    void testUpdateRole() throws Exception {
        Role existingRole = new Role(1, "Admin", Collections.emptyList());
        Role updatedRole = new Role(1, "Super Admin", Collections.emptyList());

        when(roleService.updateRole(any(Role.class), eq(1))).thenReturn(updatedRole);

        mockMvc.perform(put("/roles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name_role\":\"Super Admin\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id_role").value(1))
                .andExpect(jsonPath("$.name_role").value("Super Admin"))
                .andDo(print());

        verify(roleService, times(1)).updateRole(any(Role.class), eq(1));
    }

    @Test
    void testDeleteRole() throws Exception {
        doNothing().when(roleService).deleteRole(1);

        mockMvc.perform(delete("/roles/1"))
                .andExpect(status().isOk())
                .andDo(print());

        verify(roleService, times(1)).deleteRole(1);
    }
}
