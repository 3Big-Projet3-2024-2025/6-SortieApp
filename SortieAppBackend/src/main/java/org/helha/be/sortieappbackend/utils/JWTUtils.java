package org.helha.be.sortieappbackend.utils;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JWTUtils {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expirationTokenMs}")
    private long expirationToken;

    @Value("${jwt.expirationRefreshTokenMs}")
    private long expirationRefreshToken;

    public String generateAccesToken(org.springframework.security.core.userdetails.User user, org.helha.be.sortieappbackend.models.User customUser){return generateToken(user,customUser,expirationToken);}

    public String generateRefreshToken(org.springframework.security.core.userdetails.User user, org.helha.be.sortieappbackend.models.User customUser){return generateToken(user,customUser,expirationRefreshToken);}

    private String generateToken(org.springframework.security.core.userdetails.User user, org.helha.be.sortieappbackend.models.User customUser, long expiration){
        return Jwts.builder()
                .claims(Jwts.claims()
                        .subject(user.getUsername())
                        .issuedAt(new Date())
                        .expiration(new Date(new Date().getTime()+expiration))
                        .add("id",customUser.getId())
                        .add("name",customUser.getName_user())
                        .add("lastname",customUser.getLastname_user())
                        .add("roles",user.getAuthorities())
                        .build())
                .signWith(SignatureAlgorithm.HS256,secret).compact();
    }

    public boolean validateToken(String token){
        try{
            parseToken(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
    public Claims parseToken(String token) throws JwtException{
        return Jwts.parser()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getPayload();

    }

}

