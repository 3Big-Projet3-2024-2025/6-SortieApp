package org.helha.be.sortieappbackend.serviceTest;

import org.helha.be.sortieappbackend.models.School;
import org.helha.be.sortieappbackend.models.User;
import org.helha.be.sortieappbackend.repositories.jpa.SchoolRepository;
import org.helha.be.sortieappbackend.services.SchoolServiceDB;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SchoolServiceDBTest {

    @Mock
    private SchoolRepository schoolRepository;

    @InjectMocks
    private SchoolServiceDB schoolServiceDB;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("getSchools() doit renvoyer la liste de toutes les écoles")
    void testGetSchools() {
        // GIVEN
        School s1 = new School(1, "School A", "Address A", null);
        School s2 = new School(2, "School B", "Address B", null);
        when(schoolRepository.findAll()).thenReturn(Arrays.asList(s1, s2));

        // WHEN
        List<School> result = schoolServiceDB.getSchools();

        // THEN
        assertEquals(2, result.size());
        assertEquals("School A", result.get(0).getName_school());
        verify(schoolRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getSchoolById() doit retourner une école si elle existe sinon Optional.empty")
    void testGetSchoolById() {
        // GIVEN
        School s = new School(1, "Test School", "Test Address", null);
        when(schoolRepository.findById(1)).thenReturn(Optional.of(s));

        // WHEN
        Optional<School> result = schoolServiceDB.getSchoolById(1);

        // THEN
        assertTrue(result.isPresent());
        assertEquals("Test School", result.get().getName_school());
        verify(schoolRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("addSchool() doit sauvegarder l'école et définir l'école dans chacun de ses users")
    void testAddSchool() {
        // GIVEN
        User u1 = new User();
        u1.setName_user("User1");

        User u2 = new User();
        u2.setName_user("User2");

        School s = new School(0, "New School", "New Address", Arrays.asList(u1, u2));
        School saved = new School(10, "New School", "New Address", Arrays.asList(u1, u2));

        when(schoolRepository.save(s)).thenReturn(saved);

        // WHEN
        School result = schoolServiceDB.addSchool(s);

        // THEN
        assertEquals(10, result.getId_school());
        assertEquals("New School", result.getName_school());
        assertNotNull(result.getUsers_school());
        assertEquals(2, result.getUsers_school().size());
        // Vérif que l'école est bien associée aux users
        for (User u : result.getUsers_school()) {
            assertEquals(result, u.getSchool_user());
        }
        verify(schoolRepository, times(1)).save(s);
    }

    @Test
    @DisplayName("updateSchool() doit mettre à jour l'école existante si elle existe")
    void testUpdateSchool() {
        // GIVEN
        School existingSchool = new School(1, "Old Name", "Old Address", null);
        School newSchoolData = new School(0, "New Name", "New Address", null);

        when(schoolRepository.findById(1)).thenReturn(Optional.of(existingSchool));
        when(schoolRepository.save(any(School.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // WHEN
        School updated = schoolServiceDB.updateSchool(newSchoolData, 1);

        // THEN
        assertEquals(1, updated.getId_school());
        assertEquals("New Name", updated.getName_school());
        assertEquals("New Address", updated.getAddress_school());
        verify(schoolRepository, times(1)).findById(1);
        verify(schoolRepository, times(1)).save(existingSchool);
    }

    @Test
    @DisplayName("updateSchool() doit lancer RuntimeException si l'école n'existe pas")
    void testUpdateSchoolNotFound() {
        // GIVEN
        School newSchoolData = new School(0, "New Name", "New Address", null);
        when(schoolRepository.findById(999)).thenReturn(Optional.empty());

        // WHEN - THEN
        assertThrows(RuntimeException.class, () -> {
            schoolServiceDB.updateSchool(newSchoolData, 999);
        });
        verify(schoolRepository, times(1)).findById(999);
        verify(schoolRepository, never()).save(any(School.class));
    }

    @Test
    @DisplayName("deleteSchool() doit supprimer l'école par son ID")
    void testDeleteSchool() {
        // GIVEN
        doNothing().when(schoolRepository).deleteById(1);

        // WHEN
        schoolServiceDB.deleteSchool(1);

        // THEN
        verify(schoolRepository, times(1)).deleteById(1);
    }
}
