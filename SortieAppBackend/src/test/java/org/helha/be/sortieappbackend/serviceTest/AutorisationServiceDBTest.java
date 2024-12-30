package org.helha.be.sortieappbackend.services;

import org.helha.be.sortieappbackend.models.Autorisation;
import org.helha.be.sortieappbackend.repositories.jpa.AutorisationRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AutorisationServiceDBTest {

    @Mock
    private AutorisationRepository autorisationRepository;

    @InjectMocks
    private AutorisationServiceDB autorisationService;

    public AutorisationServiceDBTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAutorisations() {
        // Mock data
        Autorisation a1 = new Autorisation();
        Autorisation a2 = new Autorisation();
        List<Autorisation> mockAutorisations = Arrays.asList(a1, a2);

        // Mock repository behavior
        when(autorisationRepository.findAll()).thenReturn(mockAutorisations);

        // Call service
        List<Autorisation> autorisations = autorisationService.getAutorisations();

        // Verify and assert
        assertEquals(2, autorisations.size());
        verify(autorisationRepository, times(1)).findAll();
    }

    @Test
    void testGetAutorisationsPaged() {
        // Mock data
        Autorisation a1 = new Autorisation();
        Autorisation a2 = new Autorisation();
        Page<Autorisation> mockPage = new PageImpl<>(Arrays.asList(a1, a2));

        // Mock repository behavior
        when(autorisationRepository.findAll(any(PageRequest.class))).thenReturn(mockPage);

        // Call service
        Page<Autorisation> autorisations = autorisationService.getAutorisations(PageRequest.of(0, 2));

        // Verify and assert
        assertEquals(2, autorisations.getContent().size());
        verify(autorisationRepository, times(1)).findAll(any(PageRequest.class));
    }
}
