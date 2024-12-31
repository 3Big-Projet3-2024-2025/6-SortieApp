// SchoolServiceDBTest.java
package org.helha.be.sortieappbackend.serviceTest;

import org.helha.be.sortieappbackend.models.School;
import org.helha.be.sortieappbackend.repositories.jpa.SchoolRepository;
import org.helha.be.sortieappbackend.services.SchoolServiceDB;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SchoolServiceDBTest {

    @Mock
    private SchoolRepository schoolRepository;

    @InjectMocks
    private SchoolServiceDB schoolService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetSchools() {
        School school1 = new School(1, "High School", "123 Main St", null);
        School school2 = new School(2, "College", "456 Oak St", null);

        when(schoolRepository.findAll()).thenReturn(Arrays.asList(school1, school2));

        var schools = schoolService.getSchools();

        assertNotNull(schools);
        assertEquals(2, schools.size());
        assertEquals("High School", schools.get(0).getName_school());

        verify(schoolRepository, times(1)).findAll();
    }
}
