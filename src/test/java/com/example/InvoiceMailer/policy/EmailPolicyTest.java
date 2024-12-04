package com.example.InvoiceMailer.policy;

import com.example.InvoiceMailer.model.Invoice;
import com.example.InvoiceMailer.model.Order;
import com.example.InvoiceMailer.repository.FailedProcessedPolicyRepository;
import com.example.InvoiceMailer.service.EmailService;
import com.example.InvoiceMailer.service.InvoiceService;
import com.example.InvoiceMailer.service.PdfGeneratorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class EmailPolicyTest {

    private InvoiceService invoiceService;
    private EmailService emailService;
    private EmailPolicy emailPolicy;
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private FailedProcessedPolicyRepository failedProcessedPolicyRepository;

    @BeforeEach
    void setUp() {
        invoiceService = mock(InvoiceService.class);
        emailService = mock(EmailService.class);
        failedProcessedPolicyRepository = mock(FailedProcessedPolicyRepository.class);
        emailPolicy = new EmailPolicy(invoiceService, emailService,failedProcessedPolicyRepository);
    }

    @Test
    void executeEmailPolicy_ShouldDoNothing_WhenNoUnsentInvoices() {
        // Given
        when(invoiceService.getNoSendInvoices()).thenReturn(Collections.emptyList());

        // When
        emailPolicy.executeEmailPolicy();

        // Then
        verify(invoiceService, times(1)).getNoSendInvoices();
        verifyNoInteractions(emailService);
    }

    @Test
    void executeEmailPolicy_ShouldSendEmails_ForUnsentInvoices() throws IOException {
        // given
        final List<Order> orders = new ArrayList<>();
        orders.add(new Order("Produkt A", "Opis A", 1, 100.0));
        final LocalDateTime ordersDate = LocalDateTime.parse("2024-01-01 14:30:00", formatter);

        final Invoice mockInvoice = new Invoice(1,
                                                "Jan Kowalski",
                                                "Popowicka 68",
                                                "jan.kowalski@example.com",
                                                null,
                                                ordersDate,
                                                false,
                                                orders);

        final List<Invoice> unsentInvoices = List.of(mockInvoice);
        when(invoiceService.getNoSendInvoices()).thenReturn(unsentInvoices);

        PdfGeneratorService pdfGeneratorServiceMock = mock(PdfGeneratorService.class);
        when(pdfGeneratorServiceMock.generateInvoicePdf(mockInvoice)).thenReturn(new ByteArrayOutputStream());

        byte[] mockPdf = "Mock PDF Content".getBytes();
        doReturn(mockPdf).when(emailService)
                         .sendEmails(anyString(), any(), anyString());

        // When
        emailPolicy.executeEmailPolicy();

        // Then
        verify(invoiceService, times(1)).getNoSendInvoices();
        verify(emailService, times(1)).sendEmails(eq(mockInvoice.getBuyerAddressEmail()),
                                                  eq(mockPdf),
                                                  eq("Faktura-" + mockInvoice.getInvoiceId() + ".pdf"));
        verify(invoiceService, times(1)).updateEmailSendStatus(eq(mockInvoice.getInvoiceId()), eq(true));
    }

    @Test
    void executeEmailPolicy_ShouldHandleFailure_AndNotUpdateStatus() throws IOException {
        // given
        final List<Order> orders = new ArrayList<>();
        orders.add(new Order("Produkt A", "Opis A", 1, 100.0));
        final LocalDateTime ordersDate = LocalDateTime.parse("2024-01-01 14:30:00", formatter);

        final Invoice mockInvoice = new Invoice(1,
                                                "Jan Kowalski",
                                                "Popowicka 68",
                                                "jan.kowalski@example.com",
                                                null,
                                                ordersDate,
                                                false,
                                                orders);

        List<Invoice> unsentInvoices = List.of(mockInvoice);
        when(invoiceService.getNoSendInvoices()).thenReturn(unsentInvoices);

        doThrow(new IOException("PDF Generation Failed")).when(emailService)
                                                         .sendEmails(anyString(), any(), anyString());

        // when
        Exception exception = assertThrows(IllegalStateException.class, () -> emailPolicy.executeEmailPolicy());

        // then
        assertTrue(exception.getMessage()
                            .contains("Failed to send email."));
        verify(invoiceService, times(1)).getNoSendInvoices();
        verify(emailService, times(1)).sendEmails(anyString(), any(), anyString());
        verify(invoiceService, never()).updateEmailSendStatus(anyString(), anyBoolean());
    }
}
