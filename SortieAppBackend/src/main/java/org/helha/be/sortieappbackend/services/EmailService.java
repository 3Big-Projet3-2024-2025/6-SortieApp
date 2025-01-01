package org.helha.be.sortieappbackend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.util.UUID;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendActivationEmail(String to, String activationToken) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject("Activate Your School Account");

        String activationLink = String.format(
                "http://localhost:8081/users/activate-form?token=%s",
                activationToken
        );

        String emailContent = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Activate Your Account</title>
            </head>
            <body>
                <h1>Welcome to your School!</h1>
                <p>Please click the link below to set your password and activate your account:</p>
                <a href="%s">Set Your Password</a>
            </body>
            </html>
        """.formatted(activationLink);

        helper.setText(emailContent, true);
        mailSender.send(message);
    }

    public String generateActivationToken() {
        return UUID.randomUUID().toString();
    }
}