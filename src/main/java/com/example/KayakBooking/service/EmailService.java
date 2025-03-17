package com.example.KayakBooking.service;


import com.example.KayakBooking.model.KayakBooking;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;


@Service
public class EmailService {

    private static final String EMAIL_FROM = "noReplykayakBook@wp.pl";
    private static final String MY_EMAIL = "rydzanicz.mm@gmail.com";
    private static final String SUBJECT = "Dziękujemy za rejestracje";
    private final JavaMailSender mailSender;
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String emailBodyPT1 = "Szanowni Państwo,\n\n Dziękujemy za zarejestrowanie się w spływu kajakowego.\n\n";
    private final String emailBodyPT2 = "W razie jakichkolwiek pytań lub wątpliwości związanych z użytkowaniem serwisu, prosimy o kontakt z naszym biurem obsługi klienta.\n"
                                                + "Pamiętajcie, że na tę wiadomość nie należy odpowiadać, ponieważ jest generowana automatycznie.\n\n"
                                                + "**Uwaga:** W przypadku jakichkolwiek problemów prosimy o kontakt pod adresem e-mail.\n\n"
                                                + "Email: " + MY_EMAIL + "\nDziękujemy za zaufanie i zapraszamy do korzystania z naszych usług!\n\n Z poważaniem";
    private final String emailBodyPassword = "Szanowni Państwo,\n\n Nowe hasło: \n\n";

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmails(final KayakBooking kayakBooking) {
        sendEmail(kayakBooking, kayakBooking.getBuyerAddressEmail());
        sendEmail(kayakBooking, MY_EMAIL);

    }

    public void sendEmailPassword(final String recipientEmail, final String password) {
        final MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(EMAIL_FROM);
            helper.setTo(recipientEmail);
            helper.setSubject(SUBJECT);
            helper.setText(emailBodyPassword + password);

            mailSender.send(message);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to send email.", e);
        }
    }

    private void sendEmail(final KayakBooking kayakBooking, final String recipientEmail) {
        final MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(EMAIL_FROM);
            helper.setTo(recipientEmail);
            helper.setSubject(SUBJECT);
            helper.setText(createEmailBody(kayakBooking));

            mailSender.send(message);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to send email.", e);
        }
    }

    public String createEmailBody(final KayakBooking kayakBooking) {
        if (kayakBooking == null) {
            throw new IllegalArgumentException("KayakBooking cannot be null");
        }

        final String orderDate = kayakBooking.getOrderDate() != null ? kayakBooking.getOrderDate().format(formatter)
                                                                                   .toString() : "Brak daty";
        final int kayakOne = kayakBooking.getKayakOne();
        final int kayakTwo = kayakBooking.getKayakTwo();
        final int kayakBookingKayakTwo = kayakBooking.getKayakOne_Two();

        return emailBodyPT1 + "Data spływu: " + orderDate + "\n" + "Liczba zarejestrowanych kajaków jednoosobowych: " + kayakOne + "\n" + "Liczba zarejestrowanych kajaków dwuosobowych: " + kayakTwo + "\n" + "Liczba zarejestrowanych kajaków dwuosobowych + dziecko: " + kayakBookingKayakTwo + "\n\n" + emailBodyPT2;
    }
}