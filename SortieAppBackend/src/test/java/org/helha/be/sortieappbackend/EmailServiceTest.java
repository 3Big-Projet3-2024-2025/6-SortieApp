package org.helha.be.sortieappbackend;

import org.helha.be.sortieappbackend.services.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    @Test
    void testSendActivationEmail() {
        // Arrange
        String testEmail = "la207263@student.helha.be";
        String activationLink = "http://your-app.com/activate?email=" + testEmail;

        // Act
        emailService.sendActivationEmail(testEmail, activationLink);

        // Assert
        // Manually verify that the email is received in your test SMTP inbox (e.g., Mailtrap).
        // Alternatively, if using a mock SMTP server, you can assert the received email.
    }
}