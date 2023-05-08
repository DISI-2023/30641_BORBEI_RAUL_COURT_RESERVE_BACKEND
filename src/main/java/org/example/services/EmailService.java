package org.example.services;

import org.example.entities.Reservation;
import org.example.entities.Subscription;
import org.example.repositories.ReservationRepository;
import org.example.repositories.SubscriptionRepository;
import org.example.repositories.TariffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    private final TariffRepository tariffRepository;

    private final ReservationRepository reservationRepository;

    private final SubscriptionRepository subscriptionRepository;

    @Autowired
    public EmailService(JavaMailSender mailSender, TariffRepository tariffRepository, ReservationRepository reservationRepository, SubscriptionRepository subscriptionRepository) {
        this.mailSender = mailSender;
        this.tariffRepository = tariffRepository;
        this.reservationRepository = reservationRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    public void sendReservationEmail(UUID reservationID) throws MessagingException, FileNotFoundException {
        Optional<Reservation> reservation = reservationRepository.findById(reservationID);
        MimeMessage message = mailSender.createMimeMessage();

        message.setFrom(new InternetAddress("confirmation@court-reserve.com"));
        message.setRecipients(MimeMessage.RecipientType.TO, reservation.get().getAppUser().getEmail());
        message.setSubject("Reservation confirmation");

        String htmlTemplate = generateReservationMessage(reservation.get());

        message.setContent(htmlTemplate, "text/html; charset=utf-8");

        mailSender.send(message);
    }

    public void sendSubscriptionEmail(UUID subscriptionID) throws MessagingException, FileNotFoundException {
        Optional<Subscription> subscription = subscriptionRepository.findById(subscriptionID);
        MimeMessage message = mailSender.createMimeMessage();

        message.setFrom(new InternetAddress("confirmation@court-reserve.com"));
        message.setRecipients(MimeMessage.RecipientType.TO, subscription.get().getAppUser().getEmail());
        message.setSubject("Subscription confirmation");

        String htmlTemplate = generateSubscriptionMessage(subscription.get());

        message.setContent(htmlTemplate, "text/html; charset=utf-8");

        mailSender.send(message);
    }

    private String generateReservationMessage(Reservation reservation) throws FileNotFoundException {
        StringBuilder contentBuilder = new StringBuilder();
        try {
            BufferedReader in = new BufferedReader(new FileReader("src/main/resources/public/reservation.html"));
            String str;
            while ((str = in.readLine()) != null) {
                contentBuilder.append(str);
            }
            in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        LocalDateTime localNow = LocalDateTime.now();
        ZonedDateTime zonedUTC = localNow.atZone(ZoneId.of("UTC"));
        ZonedDateTime zonedIST = zonedUTC.withZoneSameInstant(ZoneId.of("Europe/Bucharest"));
        String content = contentBuilder.toString();
        content = content.replace("#USERNAME", reservation.getAppUser().getUsername());
        content = content.replace("#CURRENT_DATE",zonedIST.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        content = content.replace("#START",reservation.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        content = content.replace("#END",reservation.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        content = content.replace("#FIELD_NAME",reservation.getField().getName());
        content = content.replace("#RESERVATION_TYPE", reservation.getTariffType());
        double price = tariffRepository.findByFieldAndType(reservation.getField(),reservation.getTariffType()).get().getPrice();
        content = content.replace("#PRICE", String.valueOf(price));
        double hours = (double)reservation.getFinalPrice() / (double)price;
        content = content.replace("#NUMBER_OF_HOURS", String.valueOf(hours));
        content = content.replace("#FINAL_PRICE", String.valueOf(reservation.getFinalPrice()));
        return content;
    }

    private String generateSubscriptionMessage(Subscription subscription) throws FileNotFoundException {
        StringBuilder contentBuilder = new StringBuilder();
        try {
            BufferedReader in = new BufferedReader(new FileReader("src/main/resources/public/subscription.html"));
            String str;
            while ((str = in.readLine()) != null) {
                contentBuilder.append(str);
            }
            in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        LocalDateTime localNow = LocalDateTime.now();
        ZonedDateTime zonedUTC = localNow.atZone(ZoneId.of("UTC"));
        ZonedDateTime zonedIST = zonedUTC.withZoneSameInstant(ZoneId.of("Europe/Bucharest"));
        String content = contentBuilder.toString();
        content = content.replace("#USERNAME", subscription.getAppUser().getUsername());
        content = content.replace("#CURRENT_DATE",zonedIST.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        content = content.replace("#START",subscription.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        content = content.replace("#END",subscription.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        content = content.replace("#HOURSTART",subscription.getStartHour().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        content = content.replace("#HOUREND",subscription.getEndHour().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        content = content.replace("#DAY", subscription.getDayOfWeek().toString());
        content = content.replace("#FIELD_NAME",subscription.getField().getName());
        content = content.replace("#SUBSCRIPTION_TYPE", subscription.getType());
        double price = tariffRepository.findByFieldAndType(subscription.getField(),subscription.getType()).get().getPrice();
        content = content.replace("#PRICE", String.valueOf(price));
        double hours = (double)subscription.getFinalPrice() / (double)price;
        content = content.replace("#TYPE", getPeriod(subscription.getType()));
        content = content.replace("#NUMBER_OF_RESERVATIONS", String.valueOf(hours));
        content = content.replace("#FINAL_PRICE", String.valueOf(subscription.getFinalPrice()));
        return content;
    }

    private String getPeriod(String type){
        if(type.equals("Weekly"))
            return "Week";
        if(type.equals("Monthly"))
            return "Month";
        return "Year";
    }
}