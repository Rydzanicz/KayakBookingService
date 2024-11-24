package com.example.InvoiceMailer;


import com.example.InvoiceMailer.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.javamail.JavaMailSender;

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

    @BeforeEach
    void setUp() {
        mailSender = mock(JavaMailSender.class);
        emailService = new EmailService(mailSender);
    }

    @Test
    void testSendEmailSuccess() {
        // given
        String recipientEmail = "test@example.com";
        String fileName = "invoice.pdf";
        byte[] pdfAttachment = "PDF Content".getBytes();

        MimeMessage mockMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mockMessage);

        // when
        emailService.sendEmails(recipientEmail, pdfAttachment, fileName);

        // then
        verify(mailSender, times(2)).send(mockMessage);
    }

    @Test
    void testSendEmailNoAttachment() {
        // given
        String recipientEmail = "test@example.com";
        String fileName = "invoice.pdf";

        MimeMessage mockMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mockMessage);

        // when
        // then
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                                                       () -> emailService.sendEmails(recipientEmail, null, fileName));

        assertEquals("Failed to send email.", exception.getMessage());
    }

    @Test
    void testSendEmailThrowsException() {
        // given
        String recipientEmail = "test@example.com";
        String fileName = "invoice.pdf";
        byte[] pdfAttachment = "PDF Content".getBytes();

        MimeMessage mockMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mockMessage);

        doThrow(new RuntimeException("Mail server not available")).when(mailSender)
                                                                  .send(any(MimeMessage.class));

        // when
        // then
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                                                       () -> emailService.sendEmails(recipientEmail, pdfAttachment, fileName));

        assertEquals("Failed to send email.", exception.getMessage());
        assertEquals("Mail server not available",
                     exception.getCause()
                              .getMessage());
    }

    @Test
    void testEmailContent() throws Exception {
        // given
        String recipientEmail = "test@example.com";
        String fileName = "invoice.pdf";
        byte[] pdfAttachment = "PDF Content".getBytes();

        MimeMessage mockMessage = mock(MimeMessage.class);
        ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mockMessage);

        // when
        emailService.sendEmails(recipientEmail, pdfAttachment, fileName);

        // then
        verify(mailSender, times(2)).send(messageCaptor.capture());

        MimeMessage sentMessage = messageCaptor.getValue();
        assertNotNull(sentMessage);
    }
}