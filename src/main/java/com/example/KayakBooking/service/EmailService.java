package com.example.KayakBooking.service;


import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service
public class EmailService {

    private static final String EMAIL_FROM = "misiekr95@wp.pl";
    private static final String MY_EMAIL = "rydzanicz.mm@gmail.com";
    private static final String SUBJECT = "Dziękujemy za zakup – Twoja faktura w załączniku";
    private final JavaMailSender mailSender;

    private final String emailBody = "Szanowni Państwo,\n\n"
                                             + "Dziękujemy za zarejestrowanie się w spływu kajakowego.\n\n"
                                             + "W razie jakichkolwiek pytań lub wątpliwości związanych z użytkowaniem serwisu, prosimy o kontakt z naszym biurem obsługi klienta.\n"
                                             + "Pamiętajcie, że na tę wiadomość nie należy odpowiadać, ponieważ jest generowana automatycznie.\n\n"
                                             + "Z wyrazami szacunku,\n"
                                             + "Michał Rydzanicz\n"
                                             + "---\n\n"
                                             + "**Dane kontaktowe:**\n"
                                             + "Email: rydzanicz.mm@gmail.com\n"
                                             + "**Uwaga:** W przypadku jakichkolwiek problemów prosimy o kontakt pod adresem e-mail.\n\n"
                                             + "Dziękujemy za zaufanie i zapraszamy do korzystania z naszych usług!\n\n"
                                             + "Z poważaniem";
    private final String emailBodyPassword = "Szanowni Państwo,\n\n" + "Nowe hasło: ";

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmails(final String recipientEmail) {
        sendEmail(recipientEmail);
        sendEmail(MY_EMAIL);

    }

    public void sendEmailPassword(final String recipientEmail) {
        final MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(EMAIL_FROM);
            helper.setTo(recipientEmail);
            helper.setSubject(SUBJECT);
            helper.setText(emailBodyPassword);

            mailSender.send(message);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to send email.", e);
        }
    }

    private void sendEmail(final String recipientEmail) {
        final MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(EMAIL_FROM);
            helper.setTo(recipientEmail);
            helper.setSubject(SUBJECT);
            helper.setText(emailBody);

            mailSender.send(message);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to send email.", e);
        }
    }
}