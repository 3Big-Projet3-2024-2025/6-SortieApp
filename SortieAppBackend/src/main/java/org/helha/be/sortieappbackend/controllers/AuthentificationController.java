package org.helha.be.sortieappbackend.controllers;

import io.jsonwebtoken.JwtException;
import jakarta.annotation.security.PermitAll;
import org.helha.be.sortieappbackend.models.JWT;
import org.helha.be.sortieappbackend.repositories.jpa.UserRepository;
import org.helha.be.sortieappbackend.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthentificationController {
    @Autowired
    JWTUtils jwtUtils;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @PostMapping("login")
    public ResponseEntity<?> authenticate(@RequestParam String email, @RequestParam String password){
        try{
            Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email,password));
            SecurityContextHolder.getContext().setAuthentication(auth);

            org.springframework.security.core.userdetails.User user = (User)auth.getPrincipal();
            org.helha.be.sortieappbackend.models.User customUser = userRepository.findUserByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User not found with email: "+ email));
            if (!customUser.getActivated())return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Account disabled");
            JWT jwt = new JWT(jwtUtils.generateAccesToken(user,customUser), jwtUtils.generateRefreshToken(user,customUser));
            return ResponseEntity.ok(jwt);
        } catch (AuthenticationException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid username or password");
        }
    }

    @PostMapping("refresh-token")
    public ResponseEntity<?> refreshAccessToken(@RequestParam String refreshToken) {
        try {
            if (!jwtUtils.validateToken(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
            }

            int userId = jwtUtils.getUserIdFromToken(refreshToken);

            org.helha.be.sortieappbackend.models.User customUser = userRepository.findById(userId)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));

            if (!customUser.getActivated()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Account disabled");
            }

            User user = new User(customUser.getEmail(), customUser.getPassword_user(),
                    customUser.getRole_user().getName_role().equals("ROLE_ADMIN") ?
                            List.of(() -> "ROLE_ADMIN") : List.of(() -> "ROLE_USER"));

            JWT jwt = new JWT(jwtUtils.generateAccesToken(user, customUser), jwtUtils.generateRefreshToken(user, customUser));
            return ResponseEntity.ok(jwt);
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }
}
