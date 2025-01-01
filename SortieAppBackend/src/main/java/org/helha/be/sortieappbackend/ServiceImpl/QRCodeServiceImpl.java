package org.helha.be.sortieappbackend.ServiceImpl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.helha.be.sortieappbackend.models.Autorisation;
import org.helha.be.sortieappbackend.models.User;
import org.helha.be.sortieappbackend.repositories.jpa.UserRepository;
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
import java.util.Map;

@Service
public class QRCodeServiceImpl implements QRCodeService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public byte[] generateQRCodeFromAutorisation(Autorisation autorisation, int width, int height) throws Exception {
        String content;

        if (autorisation != null && autorisation.getUser() != null) {
            User user = autorisation.getUser();
            String userInfo = user.getName_user() + " " + user.getLastname_user();

            if (!user.getActivated()) {
                content = userInfo + "\nUser is not activated";
            } else {
                boolean canLeave = checkIfUserCanLeave(autorisation);
                content = userInfo + "\n" + (canLeave
                        ? "Exit authorized\nFrom: " + autorisation.getHeure_debut() + " to " + autorisation.getHeure_fin()
                        : "Exit not authorized");
            }
        } else if (autorisation == null || autorisation.getUser() == null) {
            // Gestion des cas où aucune autorisation n'est trouvée
            content = "No valid autorisation found\nUser unknown";
        } else {
            content = "Invalid autorisation";
        }

        // Debug pour voir le contenu final
        System.out.println("QR Code Content: " + content);
        return generateQRCodeWithMessage(content, width, height);
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
        User user = autorisation.getUser();
        String userName = user.getName_user() + " " + user.getLastname_user();

        if (!user.getActivated()) {
            return userName + "\nUser is not activated";
        }

        boolean canLeave = checkIfUserCanLeave(autorisation);

        if (canLeave) {
            return userName + "\nExit authorized\nFrom: " + autorisation.getHeure_debut() + " to " + autorisation.getHeure_fin();
        } else {
            return userName + "\nExit not authorized";
        }
    }



    public boolean checkIfUserCanLeave(Autorisation autorisation) {
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        LocalDate startDate = convertToLocalDate(autorisation.getDate_debut());
        LocalDate endDate = convertToLocalDate(autorisation.getDate_fin());
        LocalTime startTime = convertToLocalTime(autorisation.getHeure_debut());
        LocalTime endTime = convertToLocalTime(autorisation.getHeure_fin());

        // Vérifier si la date actuelle est dans l'intervalle des dates autorisées
        if ((currentDate.isEqual(startDate) || currentDate.isAfter(startDate)) &&
                (currentDate.isBefore(endDate) || currentDate.isEqual(endDate))) {

            // Vérification de l'heure
            if ((currentTime.isAfter(startTime) || currentTime.equals(startTime)) &&
                    (currentTime.isBefore(endTime) || currentTime.equals(endTime))) {

                // Vérification du jour
                boolean isAuthorizedDay = checkIfAuthorizedDay(currentDate, autorisation.getJours());
                if (isAuthorizedDay) {
                    return true;
                }
            }
        }
        return false;
    }


    private boolean checkIfAuthorizedDay(LocalDate currentDate, String jours) {
        // Mapping des jours de la semaine
        Map<String, DayOfWeek> dayMapping = Map.of(
                "MONDAY", DayOfWeek.MONDAY,
                "TUESDAY", DayOfWeek.TUESDAY,
                "WEDNESDAY", DayOfWeek.WEDNESDAY,
                "THURSDAY", DayOfWeek.THURSDAY,
                "FRIDAY", DayOfWeek.FRIDAY,
                "SATURDAY", DayOfWeek.SATURDAY,
                "SUNDAY", DayOfWeek.SUNDAY
        );

        // Split the jours field and check if the current day matches
        String[] days = jours.split(",");
        for (String day : days) {
            String trimmedDay = day.trim().toUpperCase(); // Normalisation en majuscule

            // Debug pour voir ce qui est comparé
            System.out.println("Comparing authorized day: " + trimmedDay);
            System.out.println("Actual current day: " + currentDate.getDayOfWeek().toString());

            // Comparaison avec le mapping des jours
            if (dayMapping.containsKey(trimmedDay) && dayMapping.get(trimmedDay) == currentDate.getDayOfWeek()) {
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