package org.helha.be.sortieappbackend.controllersTest;

import org.helha.be.sortieappbackend.controllers.UserController;
import org.helha.be.sortieappbackend.models.Role;
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

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

class UserControllerTest {

    @Mock
    private UserServiceDB userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void testGetUsers() throws Exception {
        User user1 = new User(1, "Doe", "John", "john.doe@example.com", "password123", "123 Street", null, null, true, null);
        User user2 = new User(2, "Smith", "Jane", "jane.smith@example.com", "password456", "456 Avenue", null, null, true, null);

        when(userService.getUsers()).thenReturn(Arrays.asList(user1, user2));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id_user").value(1))
                .andExpect(jsonPath("$[0].name_user").value("John"))
                .andExpect(jsonPath("$[1].id_user").value(2))
                .andExpect(jsonPath("$[1].name_user").value("Jane"))
                .andDo(print());

        verify(userService, times(1)).getUsers();
    }

    @Test
    void testAddUser() throws Exception {
        User newUser = new User(0, "Doe", "John", "john.doe@example.com", "password123", "123 Street", null, null, true, null);
        User savedUser = new User(1, "Doe", "John", "john.doe@example.com", "password123", "123 Street", null, null, true, null);

        when(userService.addUser(any(User.class))).thenReturn(savedUser);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"lastname_user\":\"Doe\",\"name_user\":\"John\",\"email_user\":\"john.doe@example.com\",\"password_user\":\"password123\",\"address_user\":\"123 Street\",\"activated\":true}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id_user").value(1))
                .andExpect(jsonPath("$.name_user").value("John"))
                .andDo(print());

        verify(userService, times(1)).addUser(any(User.class));
    }

    @Test
    void testUpdateUser() throws Exception {
        User existingUser = new User(1, "Doe", "John", "john.doe@example.com", "password123", "123 Street", null, null, true, null);
        User updatedUser = new User(1, "Doe", "Johnny", "johnny.doe@example.com", "password123", "123 Street", null, null, true, null);

        when(userService.updateUser(any(User.class), eq(1))).thenReturn(updatedUser);

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"lastname_user\":\"Doe\",\"name_user\":\"Johnny\",\"email_user\":\"johnny.doe@example.com\",\"password_user\":\"password123\",\"address_user\":\"123 Street\",\"activated\":true}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id_user").value(1))
                .andExpect(jsonPath("$.name_user").value("Johnny"))
                .andDo(print());

        verify(userService, times(1)).updateUser(any(User.class), eq(1));
    }

    @Test
    void testDeleteUser() throws Exception {
        doNothing().when(userService).deleteUser(1);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk())
                .andDo(print());

        verify(userService, times(1)).deleteUser(1);
    }
}

