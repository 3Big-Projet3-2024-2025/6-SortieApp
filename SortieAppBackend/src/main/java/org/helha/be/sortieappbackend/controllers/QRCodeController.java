package org.helha.be.sortieappbackend.controllers;

import org.helha.be.sortieappbackend.models.Autorisation;
import org.helha.be.sortieappbackend.services.QRCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/qrcodes")
@CrossOrigin(origins = "*")
public class QRCodeController {

    @Autowired
    private QRCodeService qrCodeService;

    @PostMapping("/generateFromAutorisation")
    public ResponseEntity<byte[]> generateQRCodeFromAutorisation(@RequestBody Autorisation autorisation) {
        try {
            byte[] qrCode = qrCodeService.generateQRCodeFromAutorisation(autorisation, 300, 300);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"autorisation_qrcode.png\"")
                    .contentType(MediaType.IMAGE_PNG)
                    .body(qrCode);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}