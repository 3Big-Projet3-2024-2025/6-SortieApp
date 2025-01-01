package org.helha.be.sortieappbackend.controllers;

import io.jsonwebtoken.JwtException;
import org.helha.be.sortieappbackend.ServiceImpl.QRCodeServiceImpl;
import org.helha.be.sortieappbackend.models.Autorisation;
import org.helha.be.sortieappbackend.models.User;
import org.helha.be.sortieappbackend.repositories.jpa.AutorisationRepository;
import org.helha.be.sortieappbackend.repositories.jpa.UserRepository;
import org.helha.be.sortieappbackend.services.IAutorisationService;
import org.helha.be.sortieappbackend.services.QRCodeService;
import org.helha.be.sortieappbackend.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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

    @GetMapping("/generateFromUser")
    public ResponseEntity<?> generateQRCode(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || authHeader.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Authorization header is missing");
        }
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        try {
            // VÃ©rification de l'activation de l'utilisateur
            int userId = jwtUtils.getUserIdFromToken(token);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            if (!user.getActivated()) {
                byte[] qrCode = qrCodeServiceImpl.generateQRCodeWithMessage("User is not activated", 300, 300);
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"autorisation_qrcode.png\"")
                        .contentType(MediaType.IMAGE_PNG)
                        .body(qrCode);
            }

            // Get all autorisations for the user
            List<Autorisation> autorisations = autorisationService.getAutorisationsByUserID(userId);

            if (autorisations.isEmpty()) {
                byte[] qrCode = qrCodeServiceImpl.generateQRCodeWithMessage("Exit not authorized", 300, 300);
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"autorisation_qrcode.png\"")
                        .contentType(MediaType.IMAGE_PNG)
                        .body(qrCode);
            }

            for (Autorisation autorisation : autorisations) {
                if (qrCodeServiceImpl.checkIfUserCanLeave(autorisation)) {
                    byte[] qrCode = qrCodeServiceImpl.generateQRCodeFromAutorisation(autorisation, 300, 300);
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"autorisation_qrcode.png\"")
                            .contentType(MediaType.IMAGE_PNG)
                            .body(qrCode);
                }
            }

            byte[] qrCode = qrCodeServiceImpl.generateQRCodeWithMessage("Exit not authorized", 300, 300);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"autorisation_qrcode.png\"")
                    .contentType(MediaType.IMAGE_PNG)
                    .body(qrCode);

        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);  // Internal server error
        }
    }
}