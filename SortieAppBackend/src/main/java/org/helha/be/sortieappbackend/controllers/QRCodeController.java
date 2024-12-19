package org.helha.be.sortieappbackend.controllers;

import org.helha.be.sortieappbackend.ServiceImpl.QRCodeServiceImpl;
import org.helha.be.sortieappbackend.models.Autorisation;
import org.helha.be.sortieappbackend.repositories.jpa.AutorisationRepository;
import org.helha.be.sortieappbackend.services.IAutorisationService;
import org.helha.be.sortieappbackend.services.QRCodeService;
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
    private QRCodeServiceImpl qrCodeServiceImpl;

    @Autowired
    private IAutorisationService autorisationService;

    @GetMapping("/generateFromUser/{userId}")
    public ResponseEntity<byte[]> generateQRCode(@PathVariable int userId) {
        try {
            // Get all autorisations for the user
            List<Autorisation> autorisations = autorisationService.getAutorisationsByUserID(userId);

            if (autorisations.isEmpty()) {
                // If no autorisations are found, return the "Exit not authorized" QR code
                byte[] qrCode = qrCodeServiceImpl.generateQRCodeWithMessage("Exit non authorized", 300, 300);
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"autorisation_qrcode.png\"")
                        .contentType(MediaType.IMAGE_PNG)
                        .body(qrCode);
            }

            // Check if any of the autorisations are valid for the current time and day
            for (Autorisation autorisation : autorisations) {
                if (qrCodeServiceImpl.checkIfUserCanLeave(autorisation)) {
                    // If authorization is valid, generate and return the QR code
                    byte[] qrCode = qrCodeServiceImpl.generateQRCodeFromAutorisation(autorisation, 300, 300);
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"autorisation_qrcode.png\"")
                            .contentType(MediaType.IMAGE_PNG)
                            .body(qrCode);
                }
            }

            // If no valid autorisation found, return the "Exit not authorized" QR code
            byte[] qrCode = qrCodeServiceImpl.generateQRCodeWithMessage("Exit non authorized", 300, 300);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"autorisation_qrcode.png\"")
                    .contentType(MediaType.IMAGE_PNG)
                    .body(qrCode);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);  // Internal server error
        }
    }
}