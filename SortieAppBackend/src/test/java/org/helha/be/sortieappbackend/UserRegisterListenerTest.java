package org.helha.be.sortieappbackend;

import org.helha.be.sortieappbackend.events.UserRegisterEvent;
import org.helha.be.sortieappbackend.events.UserRegisterListener;
import org.helha.be.sortieappbackend.models.User;
import org.helha.be.sortieappbackend.services.EmailService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class UserRegisterListenerTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserRegisterListener listener;

    public UserRegisterListenerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHandleUserRegister_SendsActivationEmail() {
        // Arrange
        User mockUser = new User();
        mockUser.setEmail("la207263@student.helha.be");

        String expectedActivationLink = "http://your-app.com/activate?email=" + mockUser.getEmail();

        UserRegisterEvent event = new UserRegisterEvent(this, mockUser);

        // Act
        listener.handleUserRegister(event);

        // Assert
        verify(emailService, times(1)).sendActivationEmail(mockUser.getEmail(), expectedActivationLink);
    }
}