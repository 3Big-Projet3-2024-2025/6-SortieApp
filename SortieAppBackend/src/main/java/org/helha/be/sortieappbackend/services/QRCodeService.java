package org.helha.be.sortieappbackend.services;

import org.helha.be.sortieappbackend.models.Autorisation;

import java.util.List;

public interface QRCodeService {
    byte[] generateQRCodeFromAutorisation(Autorisation autorisation, int width, int height) throws Exception;
}