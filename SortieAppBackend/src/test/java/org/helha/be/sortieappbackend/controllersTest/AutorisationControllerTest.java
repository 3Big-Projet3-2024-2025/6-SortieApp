package org.helha.be.sortieappbackend.controllersTest;

import org.helha.be.sortieappbackend.controllers.AutorisationController;
import org.helha.be.sortieappbackend.models.*;
import org.helha.be.sortieappbackend.services.IAutorisationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class AutorisationControllerTest {

    @Mock
    private IAutorisationService autorisationService;

    @InjectMocks
    private AutorisationController autorisationController;

    private Role role;
    private School school;
    private User user1;
    private User user2;
    private Autorisation a1;
    private Autorisation a2;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        role = new Role(1, "Admin Local", null);
        school = new School(1, "HelHa Montignies", "Rue Trieu Kaisin 136", new ArrayList<>());
        user1 = new User(1, "Ozudogru", "Huseyin", "hozu@helha.be", "password123", "Rue Lison 214, 6060 Gilly", school, role, true, "picture_user");
        school.getUsers_school().add(user1);
        user2 = new User(2, "Gallet", "Noah", "ngal@helha.be", "password456", "Rue Trieu Kaisin 136, 6061 Montignies", school, role, false, "picture_user");
        school.getUsers_school().add(user2);

        a1 = new Autorisation(1, Autorisation_Type.Unique, "test", new Date(2024 - 1900, Calendar.DECEMBER, 12), new Date(2024 - 1900, Calendar.DECEMBER, 12), "12:00", "12:30", null, user1);
        a2 = new Autorisation(2, Autorisation_Type.Weekly, "test2", new Date(2024 - 1900, Calendar.DECEMBER, 12), new Date(2025 - 1900, Calendar.JUNE, 30), "12:00", "12:30", "Wednesday", user2);
    }

    @Test
    public void testGetAutorisations() {
        when(autorisationService.getAutorisations()).thenReturn(Arrays.asList(a1, a2));

        ResponseEntity<List<Autorisation>> response = autorisationController.getAutorisations();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        verify(autorisationService, times(1)).getAutorisations();
    }

    @Test
    public void testAddAutorisation() {
        when(autorisationService.addAutorisation(a1)).thenReturn(a1);

        ResponseEntity<?> response = autorisationController.addAutorisation(a1);

        // Vérifier le statut de la réponse
        assertEquals(201, response.getStatusCodeValue());

        // Vérifier que le corps de la réponse contient l'objet attendu
        assertTrue(response.getBody() instanceof Autorisation);
        Autorisation returnedAutorisation = (Autorisation) response.getBody();
        assertEquals(a1.getId(), returnedAutorisation.getId());

        // Vérifier que le service a été appelé
        verify(autorisationService, times(1)).addAutorisation(a1);
    }


    @Test
    public void testUpdateAutorisation() {
        Autorisation updatedAutorisation = new Autorisation(
                1,
                Autorisation_Type.Weekly,
                "Updated Test",
                new Date(2024 - 1900, Calendar.DECEMBER, 12),
                new Date(2025 - 1900, Calendar.JUNE, 30),
                "10:00",
                "12:00",
                "Monday",
                user1
        );

        when(autorisationService.updateAutorisation(updatedAutorisation)).thenReturn(updatedAutorisation);

        ResponseEntity<?> response = autorisationController.updateAutorisation(updatedAutorisation);

        // Vérifier le statut de la réponse
        assertEquals(201, response.getStatusCodeValue());

        // Vérifier que le corps de la réponse contient l'objet attendu
        assertTrue(response.getBody() instanceof Autorisation);
        Autorisation returnedAutorisation = (Autorisation) response.getBody();
        assertEquals("Updated Test", returnedAutorisation.getNote());

        // Vérifier que le service a été appelé
        verify(autorisationService, times(1)).updateAutorisation(updatedAutorisation);
    }


    @Test
    public void testDeleteAutorisation() {
        doNothing().when(autorisationService).deleteAutorisation(1);

        ResponseEntity<Void> response = autorisationController.deleteAutorisation(1);

        assertEquals(204, response.getStatusCodeValue());
        verify(autorisationService, times(1)).deleteAutorisation(1);
    }
}
