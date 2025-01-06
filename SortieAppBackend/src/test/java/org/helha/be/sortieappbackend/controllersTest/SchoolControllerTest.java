package org.helha.be.sortieappbackend.controllersTest;

import org.helha.be.sortieappbackend.controllers.SchoolController;
import org.helha.be.sortieappbackend.models.School;
import org.helha.be.sortieappbackend.models.User;
import org.helha.be.sortieappbackend.security.JWTFilter;
import org.helha.be.sortieappbackend.services.SchoolServiceDB;
import org.helha.be.sortieappbackend.utils.JWTUtils; // <-- ta classe JWTUtils
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SchoolController.class)
@AutoConfigureMockMvc(addFilters = false)
class SchoolControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // On mock la couche service
    @MockBean
    private SchoolServiceDB schoolService;

    // On mock tout ce qui dépend de la sécurité
    @MockBean
    private JWTUtils jwtUtils;

    @MockBean
    private JWTFilter jwtFilter;

    @Test
    @DisplayName("GET /schools doit renvoyer la liste de toutes les écoles")
    void testGetSchools() throws Exception {
        School s1 = new School(1, "School A", "Address A", null);
        School s2 = new School(2, "School B", "Address B", null);
        when(schoolService.getSchools()).thenReturn(Arrays.asList(s1, s2));

        mockMvc.perform(get("/schools"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id_school").value(1))
                .andExpect(jsonPath("$[0].name_school").value("School A"))
                .andExpect(jsonPath("$[1].id_school").value(2))
                .andExpect(jsonPath("$[1].name_school").value("School B"));
    }

    @Test
    @DisplayName("POST /schools doit créer une nouvelle école")
    void testAddSchool() throws Exception {
        School input = new School(0, "New School", "New Address", Collections.emptyList());
        School saved = new School(10, "New School", "New Address", Collections.emptyList());

        when(schoolService.addSchool(any(School.class))).thenReturn(saved);

        mockMvc.perform(post("/schools")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id_school").value(10))
                .andExpect(jsonPath("$.name_school").value("New School"))
                .andExpect(jsonPath("$.address_school").value("New Address"));
    }

    @Test
    @DisplayName("GET /schools/getUsersBySchool/{id} doit renvoyer la liste des utilisateurs si l'école existe")
    void testGetUsersBySchool() throws Exception {
        School s = new School(1, "School A", "Address A", null);
        User u1 = new User(); u1.setId(10); u1.setName_user("Alice");
        User u2 = new User(); u2.setId(11); u2.setName_user("Bob");
        s.setUsers_school(Arrays.asList(u1, u2));

        when(schoolService.getSchoolById(1)).thenReturn(Optional.of(s));

        mockMvc.perform(get("/schools/getUsersBySchool/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].name_user").value("Alice"))
                .andExpect(jsonPath("$[1].id").value(11))
                .andExpect(jsonPath("$[1].name_user").value("Bob"));
    }

    @Test
    @DisplayName("GET /schools/getUsersBySchool/{id} doit renvoyer 404 si l'école n'existe pas")
    void testGetUsersBySchoolNotFound() throws Exception {
        when(schoolService.getSchoolById(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/schools/getUsersBySchool/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /schools/{id} doit mettre à jour une école existante")
    void testUpdateSchool() throws Exception {
        School updatedData = new School(0, "Updated School", "Updated Address", null);
        School updatedSchool = new School(1, "Updated School", "Updated Address", null);

        when(schoolService.updateSchool(any(School.class), eq(1))).thenReturn(updatedSchool);

        mockMvc.perform(put("/schools/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id_school").value(1))
                .andExpect(jsonPath("$.name_school").value("Updated School"))
                .andExpect(jsonPath("$.address_school").value("Updated Address"));
    }

    @Test
    @DisplayName("DELETE /schools/{id} doit supprimer une école")
    void testDeleteSchool() throws Exception {
        doNothing().when(schoolService).deleteSchool(1);

        mockMvc.perform(delete("/schools/1"))
                .andExpect(status().isOk());

        verify(schoolService, times(1)).deleteSchool(1);
    }
}
