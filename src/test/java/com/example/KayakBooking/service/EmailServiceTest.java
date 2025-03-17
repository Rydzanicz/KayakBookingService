package com.example.KayakBooking.service;


import com.example.KayakBooking.model.KayakBooking;
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

    private JavaMailSender mailSender;
    private EmailService emailService;
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
        final String traceID = "Prawiedniki_Zemborzycki";

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
        final String traceID = "Prawiedniki_Zemborzycki";

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

        doThrow(new RuntimeException("Mail server not available")).when(mailSender)
                                                                  .send(any(MimeMessage.class));

        // when
        // then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> emailService.sendEmails(kayakBooking));

        assertEquals("Failed to send email.", exception.getMessage());
        assertEquals("Mail server not available",
                     exception.getCause()
                              .getMessage());
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
        final String traceID = "Prawiedniki_Zemborzycki";

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
        final String generatedPassword = new BCryptPasswordEncoder().encode(UUID.randomUUID()
                                                                                .toString());
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
        final String generatedPassword = new BCryptPasswordEncoder().encode(UUID.randomUUID()
                                                                                .toString());
        final MimeMessage mockMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mockMessage);

        doThrow(new RuntimeException("Mail server not available")).when(mailSender)
                                                                  .send(any(MimeMessage.class));

        // when
        // then
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                                                       () -> emailService.sendEmailPassword(recipientEmail, generatedPassword));

        assertEquals("Failed to send email.", exception.getMessage());
        assertEquals("Mail server not available",
                     exception.getCause()
                              .getMessage());
    }

    @Test
    void testEmailPasswordContent() throws Exception {
        // given
        final String recipientEmail = "test@example.com";
        final String generatedPassword = new BCryptPasswordEncoder().encode(UUID.randomUUID()
                                                                                .toString());
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
    void shouldCreateCorrectEmailBody() {
        // Given
        final String buyerName = "Jan Kowalski";
        final String buyerEmail = "jan.kowalski@example.com";
        final String buyerPhone = "123123123";
        final LocalDateTime ordersDate = LocalDateTime.parse("2025-01-01 14:30:00", formatter);
        final int kayakOne = 1;
        final int kayakTwo = 1;
        final int kayakOne_Two = 1;
        final String traceID = "Prawiedniki_Zemborzycki";

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

        final String expectedBody = "Szanowni Państwo,\n\n Dziękujemy za zarejestrowanie się w spływu kajakowego.\n\nData spływu: 2025-01-01 14:30:00\n"
                                            + "Liczba zarejestrowanych kajaków jednoosobowych: 1\nLiczba zarejestrowanych kajaków dwuosobowych: 1\n"
                                            + "Liczba zarejestrowanych kajaków dwuosobowych + dziecko: 1\n\nW razie jakichkolwiek pytań lub wątpliwości związanych z użytkowaniem serwisu, prosimy o kontakt z naszym biurem obsługi klienta.\n"
                                            + "Pamiętajcie, że na tę wiadomość nie należy odpowiadać, ponieważ jest generowana automatycznie.\n\n"
                                            + "**Uwaga:** W przypadku jakichkolwiek problemów prosimy o kontakt pod adresem e-mail.\n\nEmail: rydzanicz.mm@gmail.com\n" +
                                            "Dziękujemy za zaufanie i zapraszamy do korzystania z naszych usług!\n\n Z poważaniem";
        final String actualBody = emailService.createEmailBody(kayakBooking);

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
        final String traceID = "Prawiedniki_Zemborzycki";

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


        final String expectedBody = "Szanowni Państwo,\n\n Dziękujemy za zarejestrowanie się w spływu kajakowego.\n\nData spływu: 2025-01-01 14:30:00\n"
                                            + "Liczba zarejestrowanych kajaków jednoosobowych: 0\nLiczba zarejestrowanych kajaków dwuosobowych: 0\n"
                                            + "Liczba zarejestrowanych kajaków dwuosobowych + dziecko: 0\n\nW razie jakichkolwiek pytań lub wątpliwości związanych z użytkowaniem serwisu, prosimy o kontakt z naszym biurem obsługi klienta.\n"
                                            + "Pamiętajcie, że na tę wiadomość nie należy odpowiadać, ponieważ jest generowana automatycznie.\n\n"
                                            + "**Uwaga:** W przypadku jakichkolwiek problemów prosimy o kontakt pod adresem e-mail.\n\nEmail: rydzanicz.mm@gmail.com\n" +
                                            "Dziękujemy za zaufanie i zapraszamy do korzystania z naszych usług!\n\n Z poważaniem";
        // When
        final String actualBody = emailService.createEmailBody(kayakBooking);

        // Then
        assertEquals(expectedBody, actualBody);
    }

}