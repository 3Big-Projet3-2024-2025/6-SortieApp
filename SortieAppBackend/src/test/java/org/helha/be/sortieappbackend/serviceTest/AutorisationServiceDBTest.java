package org.helha.be.sortieappbackend.serviceTest;

import org.helha.be.sortieappbackend.models.*;
import org.helha.be.sortieappbackend.repositories.jpa.AutorisationRepository;
import org.helha.be.sortieappbackend.services.IAutorisationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AutorisationServiceDBTest {

    @Mock
    private AutorisationRepository autorisationRepository;

    @InjectMocks
    private IAutorisationService autorisationService;

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
        a2 = new Autorisation(2, Autorisation_Type.Hebdomadaire, "test2", new Date(2024 - 1900, Calendar.DECEMBER, 12), new Date(2025 - 1900, Calendar.JUNE, 30), "12:00", "12:30", "Wednesday", user2);
    }

    @Test
    public void testGetAutorisations() {
        when(autorisationRepository.findAll()).thenReturn(Arrays.asList(a1, a2));

        List<Autorisation> result = autorisationService.getAutorisations();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(autorisationRepository, times(1)).findAll();
    }

    @Test
    public void testAddAutorisation() {
        when(autorisationRepository.save(a1)).thenReturn(a1);

        Autorisation result = autorisationService.addAutorisation(a1);

        assertNotNull(result);
        assertEquals(a1.getId(), result.getId());
        verify(autorisationRepository, times(1)).save(a1);
    }

    @Test
    public void testUpdateAutorisation() {
        when(autorisationRepository.findById(1L)).thenReturn(Optional.of(a1));
        when(autorisationRepository.save(a1)).thenReturn(a1);

        Autorisation result = autorisationService.updateAutorisation(a1);

        assertNotNull(result);
        assertEquals(a1.getId(), result.getId());
        verify(autorisationRepository, times(1)).findById(1L);
        verify(autorisationRepository, times(1)).save(a1);
    }

    @Test
    public void testDeleteAutorisation() {
        when(autorisationRepository.existsById(1L)).thenReturn(true);
        doNothing().when(autorisationRepository).deleteById(1L);

        autorisationService.deleteAutorisation(1L);

        verify(autorisationRepository, times(1)).existsById(1L);
        verify(autorisationRepository, times(1)).deleteById(1L);
    }
}
