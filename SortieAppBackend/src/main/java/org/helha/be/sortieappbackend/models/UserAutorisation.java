package org.helha.be.sortieappbackend.models;

import lombok.Data;

@Data
public class UserAutorisation {
    private User user;
    private Boolean canGo;
}
