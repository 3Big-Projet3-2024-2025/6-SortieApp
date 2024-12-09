package org.helha.be.sortieappbackend.events;

import org.helha.be.sortieappbackend.events.UserRegisterEvent;
import org.helha.be.sortieappbackend.models.User;
import org.helha.be.sortieappbackend.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class UserRegisterListener {

    @Autowired
    private EmailService emailService;

    @EventListener
    public void handleUserRegister(UserRegisterEvent event) {
        User user = event.getUser();
        //TODO : Add correct link
        String activationLink = "http://your-app.com/activate?email=" + user.getEmail();
        emailService.sendActivationEmail(user.getEmail(), activationLink);
    }
}
