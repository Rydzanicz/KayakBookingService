package com.example.KayakBooking.service;


import com.example.KayakBooking.model.KayakBooking;
import com.example.KayakBooking.model.TypeTrip;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EmailServiceTest {

    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private JavaMailSender mailSender;
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        mailSender = mock(JavaMailSender.class);
        emailService = new EmailService(mailSender);
    }

    @Test
    void testSendEmailSuccess() {
        // given
        final String buyerName = "Jan Kowalski";
        final String buyerEmail = "jan.kowalski@example.com";
        final String buyerPhone = "123123123";
        final LocalDateTime ordersDate = LocalDateTime.parse("2025-01-01 14:30:00", formatter);
        final int kayakOne = 1;
        final int kayakTwo = 1;
        final int kayakOne_Two = 1;
        final TypeTrip traceID = TypeTrip.Prawiedniki_Zemborzycki;

        final KayakBooking kayakBooking = new KayakBooking(1,
                                                           buyerName,
                                                           buyerEmail,
                                                           buyerPhone,
                                                           ordersDate,
                                                           kayakOne,
                                                           kayakTwo,
                                                           kayakOne_Two,
                                                           false,
                                                           traceID);

        final MimeMessage mockMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mockMessage);

        // when
        emailService.sendEmails(kayakBooking);

        // then
        verify(mailSender, times(2)).send(mockMessage);
    }

    @Test
    void testSendEmailThrowsException() {
        // given
        final String buyerName = "Jan Kowalski";
        final String buyerEmail = "jan.kowalski@example.com";
        final String buyerPhone = "123123123";
        final LocalDateTime ordersDate = LocalDateTime.parse("2025-01-01 14:30:00", formatter);
        final int kayakOne = 1;
        final int kayakTwo = 1;
        final int kayakOne_Two = 1;
        final TypeTrip traceID = TypeTrip.Prawiedniki_Zemborzycki;

        final KayakBooking kayakBooking = new KayakBooking(1,
                                                           buyerName,
                                                           buyerEmail,
                                                           buyerPhone,
                                                           ordersDate,
                                                           kayakOne,
                                                           kayakTwo,
                                                           kayakOne_Two,
                                                           false,
                                                           traceID);

        final MimeMessage mockMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mockMessage);

        doThrow(new RuntimeException("Mail server not available")).when(mailSender).send(any(MimeMessage.class));

        // when
        // then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> emailService.sendEmails(kayakBooking));

        assertEquals("Failed to send email.", exception.getMessage());
        assertEquals("Mail server not available", exception.getCause().getMessage());
    }

    @Test
    void testEmailContent() throws Exception {
        // given
        final String buyerName = "Jan Kowalski";
        final String buyerEmail = "jan.kowalski@example.com";
        final String buyerPhone = "123123123";
        final LocalDateTime ordersDate = LocalDateTime.parse("2025-01-01 14:30:00", formatter);
        final int kayakOne = 1;
        final int kayakTwo = 1;
        final int kayakOne_Two = 1;
        final TypeTrip traceID = TypeTrip.Prawiedniki_Zemborzycki;

        final KayakBooking kayakBooking = new KayakBooking(1,
                                                           buyerName,
                                                           buyerEmail,
                                                           buyerPhone,
                                                           ordersDate,
                                                           kayakOne,
                                                           kayakTwo,
                                                           kayakOne_Two,
                                                           false,
                                                           traceID);

        final MimeMessage mockMessage = mock(MimeMessage.class);
        final ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mockMessage);

        // when
        emailService.sendEmails(kayakBooking);

        // then
        verify(mailSender, times(2)).send(messageCaptor.capture());

        MimeMessage sentMessage = messageCaptor.getValue();
        assertNotNull(sentMessage);
    }

    @Test
    void testSendEmailPasswordSuccess() {
        // given
        final String recipientEmail = "test@example.com";
        final String generatedPassword = new BCryptPasswordEncoder().encode(UUID.randomUUID().toString());
        final MimeMessage mockMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mockMessage);

        // when
        emailService.sendEmailPassword(recipientEmail, generatedPassword);

        // then
        verify(mailSender, times(1)).send(mockMessage);
    }

    @Test
    void testSendEmailPasswordThrowsException() {
        // given
        final String recipientEmail = "test@example.com";
        final String generatedPassword = new BCryptPasswordEncoder().encode(UUID.randomUUID().toString());
        final MimeMessage mockMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mockMessage);

        doThrow(new RuntimeException("Mail server not available")).when(mailSender).send(any(MimeMessage.class));

        // when
        // then
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                                                       () -> emailService.sendEmailPassword(recipientEmail, generatedPassword));

        assertEquals("Failed to send email.", exception.getMessage());
        assertEquals("Mail server not available", exception.getCause().getMessage());
    }

    @Test
    void testEmailPasswordContent() throws Exception {
        // given
        final String recipientEmail = "test@example.com";
        final String generatedPassword = new BCryptPasswordEncoder().encode(UUID.randomUUID().toString());
        final MimeMessage mockMessage = mock(MimeMessage.class);
        final ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mockMessage);

        // when
        emailService.sendEmailPassword(recipientEmail, generatedPassword);

        // then
        verify(mailSender, times(1)).send(messageCaptor.capture());

        MimeMessage sentMessage = messageCaptor.getValue();
        assertNotNull(sentMessage);
    }

    @Test
    void shouldCreateCorrectEmailBodyPrawiedniki_Zemborzycki() {
        // Given
        final String buyerName = "Jan Kowalski";
        final String buyerEmail = "jan.kowalski@example.com";
        final String buyerPhone = "123123123";
        final LocalDateTime ordersDate = LocalDateTime.parse("2025-01-01 14:30:00", formatter);
        final int kayakOne = 1;
        final int kayakTwo = 1;
        final int kayakOne_Two = 1;
        final TypeTrip traceID = TypeTrip.Prawiedniki_Zemborzycki;

        final KayakBooking kayakBooking = new KayakBooking(1,
                                                           buyerName,
                                                           buyerEmail,
                                                           buyerPhone,
                                                           ordersDate,
                                                           kayakOne,
                                                           kayakTwo,
                                                           kayakOne_Two,
                                                           false,
                                                           traceID);

        final String expectedBody = "Twój spływ został potwierdzony!<br><br> Dziękujemy, że chcesz z nami popływać. Poniżej przedstawiamy" +
                                            " podsumowanie oraz dodatkowe informacje związane ze spływem." +
                                            "<br><br>Data spływu: 2025-01-01 14:30:00<br>Trasa: Prawiedniki_Zemborzycki" +
                                            "<br>Liczba osób: 6<br>Cena: 290<br><br><b>Miejsce zbiórki:</b><br>ul. Krężnicka 6a obok naszej" +
                                            " bazy nad Zalewem Zemborzyckim przy yacht klubach." +
                                            "<br><br>https://maps.app.goo.gl/oTcKzaw8fzAqP9FBA<br><br><br><b>Miejsce zakończenia spływu:" +
                                            "</b><br>Slip naprzeciwko naszej bazy:<br>https://maps.app.goo.gl/EGxjVhcrHwMXUu189<br><br><br>" +
                                            "Prosimy o pojawienie się w miejscu zbiórki około 5-10 minut wcześniej.<br> W razie spóźnienia" +
                                            " prosimy nas wcześniej o tym poinformować telefonicznie.<br><br><br><b>PARKING</b><br>Nad Zalewem" +
                                            " Zemborzyckim parking znajduje się wzdłuż ul Krężnickiej 6. <b>Parking jest PŁATNY MONETAMI !" +
                                            " 5zł/godzina albo 20 cały dzień.</b> <br><br><b>PŁATNOŚĆ ZA SPŁYW GOTÓWKĄ</b><br><br>W razie pytań" +
                                            " lub sytuacji awaryjnych prosimy kontakt telefoniczny<br>660 826 302<br>602 520 166<br>";
        final String actualBody = emailService.createEmailBodyPrawiedniki_Zemborzycki(kayakBooking);

        // Then
        assertEquals(expectedBody, actualBody);
    }

    @Test
    void shouldCreateCorrectEmailBodyOsmolice_Prawiedniki() {
        // Given
        final String buyerName = "Jan Kowalski";
        final String buyerEmail = "jan.kowalski@example.com";
        final String buyerPhone = "123123123";
        final LocalDateTime ordersDate = LocalDateTime.parse("2025-01-01 14:30:00", formatter);
        final int kayakOne = 1;
        final int kayakTwo = 1;
        final int kayakOne_Two = 1;
        final TypeTrip traceID = TypeTrip.Osmolice_Prawiedniki;

        final KayakBooking kayakBooking = new KayakBooking(1,
                                                           buyerName,
                                                           buyerEmail,
                                                           buyerPhone,
                                                           ordersDate,
                                                           kayakOne,
                                                           kayakTwo,
                                                           kayakOne_Two,
                                                           false,
                                                           traceID);

        final String expectedBody = "Twój spływ został potwierdzony!<br><br> Dziękujemy, że chcesz z nami popływać. Poniżej przedstawiamy" +
                                            " podsumowanie oraz dodatkowe informacje związane ze spływem.<br><br>Data spływu:" +
                                            " 2025-01-01 14:30:00<br>Trasa: Osmolice_Prawiedniki<br>Liczba osób: 6<br>Cena: 290<br>" +
                                            "<br><b>Miejsce zbiórki:</b><br>ul. Prawiednicka 1A, parking przy moście na Bystrzycy obok" +
                                            " łowiska i baru.<br>https://maps.app.goo.gl/diHvtXwSCDi3ThAE9<br><br><b>Miejsce zakończenia" +
                                            " spływu:</b><br>Pomost przed mostem w Prawiednikach niedaleko miejsca zbiórki<br><br>Prosimy" +
                                            " o pojawienie się w miejscu zbiórki około 5-10 minut wcześniej.<br> W razie spóźnienia prosimy" +
                                            " nas wcześniej o tym poinformować telefonicznie.<br><br><br><b>PARKING</b><br>W Prawiednikach" +
                                            " na miejscu zbiórki jest dostępny duży bezpłatny parking<br><br><b>PŁATNOŚĆ ZA SPŁYW GOTÓWKĄ" +
                                            "</b><br><br>W razie pytań lub sytuacji awaryjnych prosimy kontakt telefoniczny" +
                                            "<br>660 826 302<br>602 520 166<br>";
        final String actualBody = emailService.createEmailBodyOsmolice_Prawiedniki(kayakBooking);

        // Then
        assertEquals(expectedBody, actualBody);
    }

    @Test
    void shouldCreateCorrectEmailBodyOsmolice_Zemborzycki() {
        // Given
        final String buyerName = "Jan Kowalski";
        final String buyerEmail = "jan.kowalski@example.com";
        final String buyerPhone = "123123123";
        final LocalDateTime ordersDate = LocalDateTime.parse("2025-01-01 14:30:00", formatter);
        final int kayakOne = 1;
        final int kayakTwo = 1;
        final int kayakOne_Two = 1;
        final TypeTrip traceID = TypeTrip.Osmolice_Zemborzycki;

        final KayakBooking kayakBooking = new KayakBooking(1,
                                                           buyerName,
                                                           buyerEmail,
                                                           buyerPhone,
                                                           ordersDate,
                                                           kayakOne,
                                                           kayakTwo,
                                                           kayakOne_Two,
                                                           false,
                                                           traceID);

        final String expectedBody = "Twój spływ został potwierdzony!<br><br> Dziękujemy, że chcesz z nami popływać. Poniżej przedstawiamy" +
                                            " podsumowanie oraz dodatkowe informacje związane ze spływem.<br><br>Data spływu:" +
                                            " 2025-01-01 14:30:00<br>Trasa: Osmolice_Zemborzycki<br>Liczba osób: 6<br>Cena:" +
                                            " 390<br><br><b>! UWAGA ! Na trasie znajduje się jedna przenoska. W Prawiednikach," +
                                            " w połowie trasy należy wysiąść przed tamą i przenieść kajak na drugą stronę ulicy." +
                                            " Prosimy nie ciągnąć kajaków po asfalcie!</b><br><br><br><b>Miejsce zbiórki:</b><br>ul." +
                                            " Krężnicka 6a obok naszej bazy nad Zalewem Zemborzyckim przy yacht klubach.<br><br>" +
                                            "https://maps.app.goo.gl/oTcKzaw8fzAqP9FBA<br><br><br><b>Miejsce zakończenia spływu:" +
                                            "</b><br>Slip naprzeciwko naszej bazy:<br>https://maps.app.goo.gl/EGxjVhcrHwMXUu189" +
                                            "<br><br><br>Prosimy o pojawienie się w miejscu zbiórki około 5-10 minut wcześniej.<br>" +
                                            " W razie spóźnienia prosimy nas wcześniej o tym poinformować telefonicznie.<br><br><br><b>" +
                                            "PARKING</b><br>Nad Zalewem Zemborzyckim parking znajduje się wzdłuż ul Krężnickiej 6. <b>" +
                                            "Parking jest PŁATNY MONETAMI ! 5zł/godzina albo 20 cały dzień.</b> <br><br><b>PŁATNOŚĆ ZA" +
                                            " SPŁYW GOTÓWKĄ</b><br><br>W razie pytań lub sytuacji awaryjnych prosimy kontakt telefoniczny" +
                                            "<br>660 826 302<br>602 520 166<br>";
        final String actualBody = emailService.createEmailBodyOsmolice_Zemborzycki(kayakBooking);

        // Then
        assertEquals(expectedBody, actualBody);
    }

    @Test
    void shouldHandleEmptyKayakBooking() {
        final String buyerName = "Jan Kowalski";
        final String buyerEmail = "jan.kowalski@example.com";
        final String buyerPhone = "123123123";
        final LocalDateTime ordersDate = LocalDateTime.parse("2025-01-01 14:30:00", formatter);
        final int kayakOne = 0;
        final int kayakTwo = 0;
        final int kayakOne_Two = 0;
        final TypeTrip traceID = TypeTrip.Prawiedniki_Zemborzycki;

        final KayakBooking kayakBooking = new KayakBooking(1,
                                                           buyerName,
                                                           buyerEmail,
                                                           buyerPhone,
                                                           ordersDate,
                                                           kayakOne,
                                                           kayakTwo,
                                                           kayakOne_Two,
                                                           false,
                                                           traceID);


        final String expectedBody = "Twój spływ został potwierdzony!<br><br> Dziękujemy, że chcesz z nami popływać. Poniżej przedstawiamy" +
                                            " podsumowanie oraz dodatkowe informacje związane ze spływem.<br><br>Data spływu:" +
                                            " 2025-01-01 14:30:00<br>Trasa: Prawiedniki_Zemborzycki<br>Liczba osób: 0<br>Cena: 0" +
                                            "<br><br><b>Miejsce zbiórki:</b><br>ul. Krężnicka 6a obok naszej bazy nad Zalewem Zemborzyckim" +
                                            " przy yacht klubach.<br><br>https://maps.app.goo.gl/oTcKzaw8fzAqP9FBA<br><br><br><b>" +
                                            "Miejsce zakończenia spływu:</b><br>Slip naprzeciwko naszej bazy:<br>https://maps.app.goo.gl/" +
                                            "EGxjVhcrHwMXUu189<br><br><br>Prosimy o pojawienie się w miejscu zbiórki około 5-10" +
                                            " minut wcześniej.<br> W razie spóźnienia prosimy nas wcześniej o tym poinformować" +
                                            " telefonicznie.<br><br><br><b>PARKING</b><br>Nad Zalewem Zemborzyckim parking znajduje się" +
                                            " wzdłuż ul Krężnickiej 6. <b>Parking jest PŁATNY MONETAMI ! 5zł/godzina albo 20 cały dzień." +
                                            "</b> <br><br><b>PŁATNOŚĆ ZA SPŁYW GOTÓWKĄ</b><br><br>W razie pytań lub sytuacji awaryjnych" +
                                            " prosimy kontakt telefoniczny<br>660 826 302<br>602 520 166<br>";
        // When
        final String actualBody = emailService.createEmailBodyPrawiedniki_Zemborzycki(kayakBooking);

        // Then
        assertEquals(expectedBody, actualBody);
    }

}