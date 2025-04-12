package com.example.KayakBooking.service;


import com.example.KayakBooking.model.KayakBooking;
import com.example.KayakBooking.model.TypeTrip;
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

    private final String emailBodyPT1 = "Twój spływ został potwierdzony!<br><br> Dziękujemy, że chcesz z nami popływać." +
                                                " Poniżej przedstawiamy podsumowanie oraz dodatkowe informacje związane ze spływem.<br><br>";
    private final String emailBodyPT2 = "Prosimy o pojawienie się w miejscu zbiórki około 5-10 minut wcześniej.<br> W razie spóźnienia" +
                                                " prosimy nas wcześniej o tym poinformować telefonicznie.<br>";

    private final String emailBodyPassword = "Szanowni Państwo,<br><br> Nowe hasło: <br><br>";

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
            if (kayakBooking.getTypeTrip() == TypeTrip.Osmolice_Prawiedniki) {
                helper.setText(createEmailBodyOsmolice_Prawiedniki(kayakBooking), true);
            }
            if (kayakBooking.getTypeTrip() == TypeTrip.Prawiedniki_Zemborzycki) {
                helper.setText(createEmailBodyPrawiedniki_Zemborzycki(kayakBooking), true);
            }
            if (kayakBooking.getTypeTrip() == TypeTrip.Osmolice_Zemborzycki) {
                helper.setText(createEmailBodyOsmolice_Zemborzycki(kayakBooking), true);
            }
            mailSender.send(message);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to send email.", e);
        }
    }

    public String createEmailBodyOsmolice_Prawiedniki(final KayakBooking kayakBooking) {
        if (kayakBooking == null) {
            throw new IllegalArgumentException("KayakBooking cannot be null");
        }

        if (TypeTrip.Osmolice_Prawiedniki != kayakBooking.getTypeTrip()) {
            return null;
        }

        final String orderDate = kayakBooking.getOrderDate() != null ? kayakBooking.getOrderDate()
                                                                                   .format(formatter) : "Brak daty";

        final int numberOfPeople = kayakBooking.getKayakOne() + kayakBooking.getKayakTwo() * 2 + kayakBooking.getKayakOne_Two() * 3;

        final int kayakOne = kayakBooking.getKayakOne() * 70;
        final int kayakTwo = kayakBooking.getKayakTwo() * 100;
        final int kayakBookingKayakTwo = kayakBooking.getKayakOne_Two() * 120;
        final int price = kayakOne + kayakTwo + kayakBookingKayakTwo;

        return emailBodyPT1 +
                       "Data spływu: " +
                       orderDate +
                       "<br>Trasa: " +
                       kayakBooking.getTypeTrip() +
                       "<br>Liczba osób: " +
                       numberOfPeople +
                       "<br>Cena: " +
                       price +
                       "<br><br><b>Miejsce zbiórki:</b><br>" +
                       "ul. Prawiednicka 1A, parking przy moście na Bystrzycy obok łowiska i baru.<br>" +
                       "https://maps.app.goo.gl/diHvtXwSCDi3ThAE9" +
                       "<br><br><b>Miejsce zakończenia spływu:</b><br>" +
                       "Pomost przed mostem w Prawiednikach niedaleko miejsca zbiórki" +
                       "<br><br>" +
                       emailBodyPT2 +
                       "<br><br><b>PARKING</b><br>" +
                       "W Prawiednikach na miejscu zbiórki jest dostępny duży bezpłatny parking<br>" +
                       "<br><b>PŁATNOŚĆ ZA SPŁYW GOTÓWKĄ</b><br><br>" +
                       "W razie pytań lub sytuacji awaryjnych prosimy kontakt telefoniczny<br>" +
                       "660 826 302<br>" +
                       "602 520 166<br>";
    }

    public String createEmailBodyPrawiedniki_Zemborzycki(final KayakBooking kayakBooking) {
        if (kayakBooking == null) {
            throw new IllegalArgumentException("KayakBooking cannot be null");
        }

        if (TypeTrip.Prawiedniki_Zemborzycki != kayakBooking.getTypeTrip()) {
            return null;
        }

        final String orderDate = kayakBooking.getOrderDate() != null ? kayakBooking.getOrderDate()
                                                                                   .format(formatter) : "Brak daty";

        final int numberOfPeople = kayakBooking.getKayakOne() + kayakBooking.getKayakTwo() * 2 + kayakBooking.getKayakOne_Two() * 3;

        final int kayakOne = kayakBooking.getKayakOne() * 70;
        final int kayakTwo = kayakBooking.getKayakTwo() * 100;
        final int kayakBookingKayakTwo = kayakBooking.getKayakOne_Two() * 120;
        final int price = kayakOne + kayakTwo + kayakBookingKayakTwo;

        return emailBodyPT1 +
                       "Data spływu: " +
                       orderDate +
                       "<br>Trasa: " +
                       kayakBooking.getTypeTrip() +
                       "<br>Liczba osób: " +
                       numberOfPeople +
                       "<br>Cena: " +
                       price +
                       "<br><br><b>Miejsce zbiórki:</b><br>" +
                       "ul. Krężnicka 6a obok naszej bazy nad Zalewem Zemborzyckim przy yacht klubach.<br>" +
                       "<br>https://maps.app.goo.gl/oTcKzaw8fzAqP9FBA<br>" +
                       "<br><br><b>Miejsce zakończenia spływu:</b><br>" +
                       "Slip naprzeciwko naszej bazy:" +
                       "<br>https://maps.app.goo.gl/EGxjVhcrHwMXUu189<br>" +
                       "<br><br>" +
                       emailBodyPT2 +
                       "<br><br><b>PARKING</b><br>" +
                       "Nad Zalewem Zemborzyckim parking znajduje się wzdłuż ul Krężnickiej 6. <b>Parking jest PŁATNY MONETAMI ! 5zł/godzina albo 20 cały dzień.</b> <br>" +
                       "<br><b>PŁATNOŚĆ ZA SPŁYW GOTÓWKĄ</b><br><br>" +
                       "W razie pytań lub sytuacji awaryjnych prosimy kontakt telefoniczny<br>" +
                       "660 826 302<br>" +
                       "602 520 166<br>";
    }

    public String createEmailBodyOsmolice_Zemborzycki(final KayakBooking kayakBooking) {
        if (kayakBooking == null) {
            throw new IllegalArgumentException("KayakBooking cannot be null");
        }

        if (TypeTrip.Osmolice_Zemborzycki != kayakBooking.getTypeTrip()) {
            return null;
        }

        final String orderDate = kayakBooking.getOrderDate() != null ? kayakBooking.getOrderDate()
                                                                                   .format(formatter) : "Brak daty";

        final int numberOfPeople = kayakBooking.getKayakOne() + kayakBooking.getKayakTwo() * 2 + kayakBooking.getKayakOne_Two() * 3;

        final int kayakOne = kayakBooking.getKayakOne() * 90;
        final int kayakTwo = kayakBooking.getKayakTwo() * 140;
        final int kayakBookingKayakTwo = kayakBooking.getKayakOne_Two() * 160;
        final int price = kayakOne + kayakTwo + kayakBookingKayakTwo;

        return emailBodyPT1 +
                       "Data spływu: " +
                       orderDate +
                       "<br>Trasa: " +
                       kayakBooking.getTypeTrip() +
                       "<br>Liczba osób: " +
                       numberOfPeople +
                       "<br>Cena: " +
                       price +
                       "<br><br><b>! UWAGA ! Na trasie znajduje się jedna przenoska. W Prawiednikach, w połowie trasy należy wysiąść przed tamą i przenieść kajak na drugą stronę ulicy. Prosimy nie ciągnąć kajaków po asfalcie!</b><br>" +
                       "<br><br><b>Miejsce zbiórki:</b><br>" +
                       "ul. Krężnicka 6a obok naszej bazy nad Zalewem Zemborzyckim przy yacht klubach.<br>" +
                       "<br>https://maps.app.goo.gl/oTcKzaw8fzAqP9FBA<br>" +
                       "<br><br><b>Miejsce zakończenia spływu:</b><br>" +
                       "Slip naprzeciwko naszej bazy:" +
                       "<br>https://maps.app.goo.gl/EGxjVhcrHwMXUu189<br>" +
                       "<br><br>" +
                       emailBodyPT2 +
                       "<br><br><b>PARKING</b><br>" +
                       "Nad Zalewem Zemborzyckim parking znajduje się wzdłuż ul Krężnickiej 6. <b>Parking jest PŁATNY MONETAMI ! 5zł/godzina albo 20 cały dzień.</b> <br>" +
                       "<br><b>PŁATNOŚĆ ZA SPŁYW GOTÓWKĄ</b><br><br>" +
                       "W razie pytań lub sytuacji awaryjnych prosimy kontakt telefoniczny<br>" +
                       "660 826 302<br>" +
                       "602 520 166<br>";
    }

}