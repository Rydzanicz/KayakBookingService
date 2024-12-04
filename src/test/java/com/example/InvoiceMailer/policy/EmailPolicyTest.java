package com.example.InvoiceMailer.policy;

import com.example.InvoiceMailer.model.FailedProcessedPolicyEntity;
import com.example.InvoiceMailer.model.Invoice;
import com.example.InvoiceMailer.model.Order;
import com.example.InvoiceMailer.service.EmailService;
import com.example.InvoiceMailer.service.FailedProcessedPolicyService;
import com.example.InvoiceMailer.service.InvoiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

class EmailPolicyTest {

    private InvoiceService invoiceService;
    private EmailService emailService;
    private FailedProcessedPolicyService failedProcessedPolicyService;
    private EmailPolicy emailPolicy;
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @BeforeEach
    void setUp() {
        invoiceService = mock(InvoiceService.class);
        emailService = mock(EmailService.class);
        failedProcessedPolicyService = mock(FailedProcessedPolicyService.class);
        emailPolicy = new EmailPolicy(invoiceService, emailService, failedProcessedPolicyService);
    }

    @Test
    void shouldSendEmailForUnsentInvoices() {
        // Given
        final String buyerName = "Nabywca";
        final String buyerAddress = "ul. Przykładowa 2, 00-000 Warszawa";
        final String buyerEmail = "buyer@example.com";
        final String buyerNip = "0987654321";
        final LocalDateTime ordersDate = LocalDateTime.parse("2024-01-01 14:30:00", formatter);
        final ArrayList<Order> orders = new ArrayList<>();
        orders.add(new Order("Produkt A", "Opis A", 2, 100.0));

        final Invoice invoice = new Invoice(1, buyerName, buyerAddress, buyerEmail, buyerNip, ordersDate, false, orders);
        final List<Invoice> unsentInvoices = List.of(invoice);

        when(invoiceService.getNoSendInvoicesWithExcluding(anyList())).thenReturn(unsentInvoices);
        when(failedProcessedPolicyService.findInvoicesByInvoiceId(invoice.getInvoiceId())).thenReturn(Optional.empty());
        doNothing().when(emailService)
                   .sendEmails(anyString(), any(byte[].class), anyString());
        doNothing().when(invoiceService)
                   .updateEmailSendStatus(anyString(), eq(true));

        // When
        emailPolicy.executeEmailPolicy();

        // Then
        verify(emailService, times(1)).sendEmails(eq(invoice.getBuyerAddressEmail()), any(byte[].class), anyString());
        verify(invoiceService, times(1)).updateEmailSendStatus(eq(invoice.getInvoiceId()), eq(true));
    }

    @Test
    void shouldLogErrorWhenEmailSendingFails() {
        // Given
        final String buyerName = "Nabywca";
        final String buyerAddress = "ul. Przykładowa 2, 00-000 Warszawa";
        final String buyerEmail = "buyer@example.com";
        final String buyerNip = "0987654321";
        final LocalDateTime ordersDate = LocalDateTime.parse("2024-01-01 14:30:00", formatter);
        final ArrayList<Order> orders = new ArrayList<>();
        orders.add(new Order("Produkt A", "Opis A", 2, 100.0));

        final Invoice invoice = new Invoice(1, buyerName, buyerAddress, buyerEmail, buyerNip, ordersDate, false, orders);
        final List<Invoice> unsentInvoices = List.of(invoice);

        when(invoiceService.getNoSendInvoicesWithExcluding(anyList())).thenReturn(unsentInvoices);
        when(failedProcessedPolicyService.findInvoicesByInvoiceId(anyString())).thenReturn(Optional.empty());
        doThrow(new RuntimeException("Email service failed")).when(emailService)
                                                             .sendEmails(anyString(), any(byte[].class), anyString());

        // when
        emailPolicy.executeEmailPolicy();

        // then
        verify(failedProcessedPolicyService, times(1)).logError(eq("EmailPolicy"),
                                                                eq("Email service failed"),
                                                                eq(invoice.getInvoiceId()),
                                                                any());
        verify(invoiceService, never()).updateEmailSendStatus(anyString(), eq(true));
    }

    @Test
    void shouldSkipInvoicesWithMaxRetryCount() {
        // Given
        final String buyerName = "Nabywca";
        final String buyerAddress = "ul. Przykładowa 2, 00-000 Warszawa";
        final String buyerEmail = "buyer@example.com";
        final String buyerNip = "0987654321";
        final LocalDateTime ordersDate = LocalDateTime.parse("2024-01-01 14:30:00", formatter);
        final ArrayList<Order> orders = new ArrayList<>();
        orders.add(new Order("Produkt A", "Opis A", 2, 100.0));

        final Invoice invoice = new Invoice(1, buyerName, buyerAddress, buyerEmail, buyerNip, ordersDate, false, orders);
        final List<Invoice> unsentInvoices = List.of(invoice);

        final FailedProcessedPolicyEntity failedPolicy = new FailedProcessedPolicyEntity();
        failedPolicy.setRetryCount(11);

        when(invoiceService.getNoSendInvoicesWithExcluding(anyList())).thenReturn(unsentInvoices);
        when(failedProcessedPolicyService.findInvoicesByInvoiceId(invoice.getInvoiceId())).thenReturn(Optional.of(failedPolicy));

        // When
        emailPolicy.executeEmailPolicy();

        // Then
        verify(emailService, never()).sendEmails(anyString(), any(byte[].class), anyString());
        verify(failedProcessedPolicyService, never()).logError(anyString(), anyString(), anyString(), any());
    }

    @Test
    void shouldNotProcessWhenNoUnsentInvoices() {
        // Given
        when(invoiceService.getNoSendInvoicesWithExcluding(anyList())).thenReturn(new ArrayList<>());

        // When
        emailPolicy.executeEmailPolicy();

        // Then
        verify(emailService, never()).sendEmails(anyString(), any(byte[].class), anyString());
        verify(failedProcessedPolicyService, never()).logError(anyString(), anyString(), anyString(), any());
    }
}
