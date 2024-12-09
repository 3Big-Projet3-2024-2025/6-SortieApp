package org.helha.be.sortieappbackend.events;

import org.helha.be.sortieappbackend.models.User;
import org.springframework.context.ApplicationEvent;

public class UserRegisterEvent extends ApplicationEvent {

    private User user;

    public UserRegisterEvent(Object source, User user) {
        super(source);
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
