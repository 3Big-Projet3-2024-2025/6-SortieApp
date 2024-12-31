// SchoolControllerTest.java
package org.helha.be.sortieappbackend.controllersTest;

import org.helha.be.sortieappbackend.controllers.SchoolController;
import org.helha.be.sortieappbackend.models.School;
import org.helha.be.sortieappbackend.models.User;
import org.helha.be.sortieappbackend.services.SchoolServiceDB;
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

class SchoolControllerTest {

    @Mock
    private SchoolServiceDB schoolService;

    @InjectMocks
    private SchoolController schoolController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(schoolController).build();
    }

    @Test
    void testGetSchools() throws Exception {
        School school1 = new School(1, "High School", "123 Main St", Collections.emptyList());
        School school2 = new School(2, "College", "456 Oak St", Collections.emptyList());

        when(schoolService.getSchools()).thenReturn(Arrays.asList(school1, school2));

        mockMvc.perform(get("/schools"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id_school").value(1))
                .andExpect(jsonPath("$[0].name_school").value("High School"))
                .andExpect(jsonPath("$[1].id_school").value(2))
                .andExpect(jsonPath("$[1].name_school").value("College"))
                .andDo(print());

        verify(schoolService, times(1)).getSchools();
    }

    @Test
    void testGetUsersBySchool_Found() throws Exception {
        School school = new School(1, "High School", "123 Main St", Collections.emptyList());
        User user = new User(1, "Doe", "John", "john.doe@example.com", "password123", "123 St", school, null, true, null);

        school.setUsers_school(Arrays.asList(user));

        when(schoolService.getSchoolById(1)).thenReturn(Optional.of(school));

        mockMvc.perform(get("/schools/getUsersBySchool/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id_user").value(1))
                .andExpect(jsonPath("$[0].name_user").value("John"))
                .andDo(print());

        verify(schoolService, times(1)).getSchoolById(1);
    }

    @Test
    void testGetUsersBySchool_NotFound() throws Exception {
        when(schoolService.getSchoolById(1)).thenReturn(Optional.empty());

        mockMvc.perform(get("/schools/getUsersBySchool/1"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void testAddSchool() throws Exception {
        School newSchool = new School(0, "New School", "789 Pine St", Collections.emptyList());
        School savedSchool = new School(3, "New School", "789 Pine St", Collections.emptyList());

        when(schoolService.addSchool(any(School.class))).thenReturn(savedSchool);

        mockMvc.perform(post("/schools")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name_school\":\"New School\",\"address_school\":\"789 Pine St\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id_school").value(3))
                .andExpect(jsonPath("$.name_school").value("New School"))
                .andDo(print());

        verify(schoolService, times(1)).addSchool(any(School.class));
    }

    @Test
    void testUpdateSchool() throws Exception {
        School updatedSchool = new School(1, "Updated School", "999 Maple St", Collections.emptyList());

        when(schoolService.updateSchool(any(School.class), eq(1))).thenReturn(updatedSchool);

        mockMvc.perform(put("/schools/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name_school\":\"Updated School\",\"address_school\":\"999 Maple St\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name_school").value("Updated School"))
                .andDo(print());

        verify(schoolService, times(1)).updateSchool(any(School.class), eq(1));
    }

    @Test
    void testDeleteSchool() throws Exception {
        doNothing().when(schoolService).deleteSchool(1);

        mockMvc.perform(delete("/schools/1"))
                .andExpect(status().isOk())
                .andDo(print());

        verify(schoolService, times(1)).deleteSchool(1);
    }
}
