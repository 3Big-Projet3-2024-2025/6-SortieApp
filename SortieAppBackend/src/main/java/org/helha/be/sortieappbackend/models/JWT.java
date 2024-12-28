package org.helha.be.sortieappbackend.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JWT {

    private String accesToken;
    private String refreshToken;
}