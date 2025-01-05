package org.helha.be.sortieappbackend.controllers;

import io.jsonwebtoken.JwtException;
import org.helha.be.sortieappbackend.ServiceImpl.QRCodeServiceImpl;
import org.helha.be.sortieappbackend.models.Autorisation;
import org.helha.be.sortieappbackend.models.User;
import org.helha.be.sortieappbackend.models.UserAutorisation;
import org.helha.be.sortieappbackend.repositories.jpa.UserRepository;
import org.helha.be.sortieappbackend.services.IAutorisationService;
import org.helha.be.sortieappbackend.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/qrcodes")
@CrossOrigin(origins = "*")
public class QRCodeController {

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private QRCodeServiceImpl qrCodeServiceImpl;

    @Autowired
    private IAutorisationService autorisationService;

    @Autowired
    private UserRepository userRepository;

    @PreAuthorize("hasAnyRole('STUDENT')")
    @GetMapping("/generateFromUser")
    public ResponseEntity<?> generateQRCode(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || authHeader.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Authorization header is missing");
        }
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        try {
            int userId = jwtUtils.getUserIdFromToken(token);
            byte[] qrCode = qrCodeServiceImpl.generateQRCodeWithMessage(String.valueOf(userId), 300, 300);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"autorisation_qrcode.png\"")
                    .contentType(MediaType.IMAGE_PNG)
                    .body(qrCode);

        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e);  // Internal server error
        }
    }

    @PreAuthorize("hasAnyRole('SUPERVISOR', 'LOCAL_ADMIN', 'RESPONSIBLE')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable("id") Integer id) {
        if (id == null) {
            return ResponseEntity.badRequest().body(null);
        }
        try {
            User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
            List<Autorisation> autorisations = autorisationService.getAutorisationsByUserID(user.getId());
            UserAutorisation userAutorisation = new UserAutorisation();
            userAutorisation.setUser(user);
            for (Autorisation autorisation : autorisations) {
                if (qrCodeServiceImpl.checkIfUserCanLeave(autorisation)) {
                    userAutorisation.setCanGo(true);
                    return ResponseEntity.ok(userAutorisation);
                }
            }
            userAutorisation.setCanGo(false);
            return ResponseEntity.ok(userAutorisation);
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}