package org.helha.be.sortieappbackend.ServiceImpl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.helha.be.sortieappbackend.models.Autorisation;
import org.helha.be.sortieappbackend.services.AutorisationServiceDB;
import org.helha.be.sortieappbackend.services.IAutorisationService;
import org.helha.be.sortieappbackend.services.QRCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.*;


import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class QRCodeServiceImpl implements QRCodeService {

    @Override
    public byte[] generateQRCodeFromAutorisation(Autorisation autorisation, int width, int height) throws Exception {
        String content = generateContent(autorisation);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            return outputStream.toByteArray();
        }
    }

    public byte[] generateQRCodeWithMessage(String message, int width, int height) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(message, BarcodeFormat.QR_CODE, width, height);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            return outputStream.toByteArray();
        }
    }

    private String generateContent(Autorisation autorisation) {
        boolean canLeave = checkIfUserCanLeave(autorisation);
        return canLeave ? "Exit authorized" : "Exit not authorized";
    }

    public boolean checkIfUserCanLeave(Autorisation autorisation) {
        long currentTimeMillis = System.currentTimeMillis();
        LocalDate currentDate = LocalDate.now();  // Current date
        LocalTime currentTime = LocalTime.now();  // Current time

        //console log to check the current date and time
        System.out.println("Current date: " + currentDate);
        System.out.println("Current time: " + currentTime);


        // Check if the start date is valid
        if (autorisation.getDate_debut() == null) return false;
        System.out.println("Autorisation date debut: " + autorisation.getDate_debut());

        // Convert start date (date_debut) to LocalDate and LocalTime
        LocalDate startDate = convertToLocalDate(autorisation.getDate_debut());
        LocalTime startTime = convertToLocalTime(autorisation.getHeure_debut());
        System.out.println("Start date: " + startDate);
        System.out.println("Start time: " + startTime);


        // Check if the end date is valid
        long dateFin = autorisation.getDate_fin() != null ? autorisation.getDate_fin().getTime() : Long.MAX_VALUE;
        System.out.println("Autorisation date fin: " + autorisation.getDate_fin());

        // Day validation: Check if the current day matches the "jours" field
        boolean isAuthorizedDay = checkIfAuthorizedDay(currentDate, autorisation.getJours());
        System.out.println("Is authorized day: " + isAuthorizedDay);

        // If the authorization has no start date or the day does not match, deny access
        if (!isAuthorizedDay || startDate.isAfter(currentDate) || (currentDate.isEqual(startDate) && currentTime.isBefore(startTime))) {
            return false;
        }

        return currentTimeMillis >= autorisation.getDate_debut().getTime() && currentTimeMillis <= dateFin;
    }

    private boolean checkIfAuthorizedDay(LocalDate currentDate, String jours) {
        // Split the jours field and check if the current day matches
        String[] days = jours.split(",");
        for (String day : days) {
            if (day.trim().equalsIgnoreCase(currentDate.getDayOfWeek().toString())) {
                return true;
            }
        }
        return false;
    }

    private LocalDate convertToLocalDate(Date date) {
        return LocalDate.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    private LocalTime convertToLocalTime(String heure) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return LocalTime.parse(heure, formatter);
    }
}