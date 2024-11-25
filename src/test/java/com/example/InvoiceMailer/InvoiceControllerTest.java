package com.example.InvoiceMailer;

import com.example.InvoiceMailer.RestControler.InvoiceController;
import com.example.InvoiceMailer.RestControler.InvoiceRequest;
import com.example.InvoiceMailer.model.Invoice;
import com.example.InvoiceMailer.model.InvoiceEntity;
import com.example.InvoiceMailer.model.Order;
import com.example.InvoiceMailer.service.EmailService;
import com.example.InvoiceMailer.service.InvoiceService;
import com.example.InvoiceMailer.service.PdfGeneratorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class InvoiceControllerTest {

    @Mock
    private InvoiceService invoiceService;

    @Mock
    private EmailService emailService;

    private InvoiceController invoiceController;
    @Mock
    private PdfGeneratorService pdfGeneratorService;
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        invoiceController = new InvoiceController(invoiceService, emailService);
    }

    @Test
    public void testGenerateInvoiceSuccess() throws Exception {
        // given
        final InvoiceRequest invoiceRequest = new InvoiceRequest();
        invoiceRequest.setBuyerName("Jan Kowalski");
        invoiceRequest.setBuyerAddress("Popowicka 68");
        invoiceRequest.setBuyerAddressEmail("jan.kowalski@example.com");
        invoiceRequest.setBuyerNip("1234567890");

        final LocalDateTime localDateTime = LocalDateTime.parse("2024-01-01 14:30:00", formatter);
        final List<Order> orders = new ArrayList<>();
        orders.add(new Order("Produkt A", "Opis A", 1, 100.0));
        invoiceRequest.setOrders(orders);

        final Invoice mockInvoice = new Invoice(1, "Jan Kowalski", "Popowicka 68", "jan.kowalski@example.com", null, localDateTime, orders);
        final InvoiceEntity savedEntity = new InvoiceEntity(mockInvoice);

        when(invoiceService.getLastInvoices()).thenReturn(mockInvoice);
        when(invoiceService.saveInvoiceWithOrders(any(Invoice.class), anyList())).thenReturn(savedEntity);

        // when
        final ResponseEntity<?> response = invoiceController.generateInvoice(invoiceRequest);

        // then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        verify(invoiceService, times(1)).saveInvoiceWithOrders(any(Invoice.class), anyList());
        verify(invoiceService, times(1)).getLastInvoices();
    }

    @Test
    public void testGenerateInvoiceError() throws IOException {
        // given
        InvoiceRequest invoiceRequest = new InvoiceRequest();
        invoiceRequest.setBuyerName("Test Buyer");
        invoiceRequest.setBuyerAddress("Test Address");
        invoiceRequest.setBuyerAddressEmail("test@example.com");
        invoiceRequest.setBuyerNip("1234567890");
        final LocalDateTime localDateTime = LocalDateTime.parse("2024-01-01 14:30:00", formatter);

        final List<Order> orders = new ArrayList<>();
        orders.add(new Order("Produkt A", "Opis A", 1, 100.0));
        invoiceRequest.setOrders(orders);
        byte[] pdfBytes = "PDF content".getBytes();
        when(pdfGeneratorService.generateInvoicePdf(any(Invoice.class))).thenReturn(new ByteArrayOutputStream() {{
            write(pdfBytes);
        }});
        when(invoiceService.getLastInvoices()).thenReturn(new Invoice(1,
                                                                      "Last Buyer",
                                                                      "Last Address",
                                                                      "last@example.com",
                                                                      null,
                                                                      localDateTime,
                                                                      orders));

        // when
        ResponseEntity<?> response = invoiceController.generateInvoice(invoiceRequest);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        HttpHeaders headers = response.getHeaders();
        assertTrue(headers.containsKey(HttpHeaders.CONTENT_DISPOSITION));
        assertEquals("attachment; filename=Faktura-FV/000000002/2024.pdf", headers.getFirst(HttpHeaders.CONTENT_DISPOSITION));
    }

    @Test
    public void testGetInvoicesByInvoiceId() {
        // given
        final LocalDateTime localDateTime = LocalDateTime.parse("2024-01-01 14:30:00", formatter);
        final List<Order> orders = new ArrayList<>();
        orders.add(new Order("Produkt A", "Opis A", 1, 100.0));
        final List<Invoice> invoices = new ArrayList<>();
        invoices.add(new Invoice(1, "Jan Kowalski", "Popowicka 68", "jan.kowalski@example.com", null, localDateTime, orders));
        when(invoiceService.getInvoicesByInvoiceId("FV/001/2024")).thenReturn(invoices);

        // when
        final ResponseEntity<List<Invoice>> response = invoiceController.getInvoices("FV/001/2024", null);

        // then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1,
                     response.getBody()
                             .size());
        verify(invoiceService, times(1)).getInvoicesByInvoiceId("FV/001/2024");
    }

    @Test
    public void testGetInvoicesByEmail() {
        // given
        final LocalDateTime localDateTime = LocalDateTime.parse("2024-01-01 14:30:00", formatter);
        final List<Order> orders = new ArrayList<>();
        orders.add(new Order("Produkt A", "Opis A", 1, 100.0));
        final List<Invoice> invoices = new ArrayList<>();
        invoices.add(new Invoice(1, "Jan Kowalski", "Popowicka 68", "jan.kowalski@example.com", null, localDateTime, orders));
        when(invoiceService.getInvoicesByAddressEmail("jan.kowalski@example.com")).thenReturn(invoices);

        // when
        final ResponseEntity<List<Invoice>> response = invoiceController.getInvoices(null, "jan.kowalski@example.com");

        // then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1,
                     response.getBody()
                             .size());
        verify(invoiceService, times(1)).getInvoicesByAddressEmail("jan.kowalski@example.com");
    }

    @Test
    public void testGetAllInvoices() {
        // given
        final LocalDateTime localDateTime = LocalDateTime.parse("2024-01-01 14:30:00", formatter);

        final List<Order> orders = new ArrayList<>();
        orders.add(new Order("Produkt A", "Opis A", 1, 100.0));
        final List<Invoice> invoices = new ArrayList<>();
        invoices.add(new Invoice(1, "Jan Kowalski", "Popowicka 68", "jan.kowalski@example.com", null, localDateTime, orders));
        invoices.add(new Invoice(2, "Anna Nowak", "Kwiatowa 12", "anna.nowak@example.com", null, localDateTime, orders));
        when(invoiceService.getAllInvoices()).thenReturn(invoices);

        // when
        final ResponseEntity<List<Invoice>> response = invoiceController.getInvoices(null, null);

        // then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2,
                     response.getBody()
                             .size());
        verify(invoiceService, times(1)).getAllInvoices();
    }

    @Test
    public void testGetAllUniqueEmails() {
        // given
        final List<String> emails = List.of("jan.kowalski@example.com", "anna.nowak@example.com");
        when(invoiceService.getUniqueEmail()).thenReturn(emails);

        // when
        final ResponseEntity<List<String>> response = invoiceController.getUniqueEmail();

        // then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2,
                     response.getBody()
                             .size());
        verify(invoiceService, times(1)).getUniqueEmail();
    }

    @Test
    public void testGetUniqueEmailError() {
        // given
        doThrow(new RuntimeException("Database error")).when(invoiceService)
                                                       .getUniqueEmail();

        // when
        ResponseEntity<List<String>> response = invoiceController.getUniqueEmail();

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testGetInvoicesNoContent() {
        // given
        when(invoiceService.getAllInvoices()).thenReturn(new ArrayList<>());

        // when
        final ResponseEntity<List<Invoice>> response = invoiceController.getInvoices(null, null);

        // then
        assertNotNull(response);
        assertEquals(204, response.getStatusCodeValue());
        verify(invoiceService, times(1)).getAllInvoices();
    }

    @Test
    public void testGetAllInvoicesError() {
        // given
        doThrow(new RuntimeException("Database error")).when(invoiceService)
                                                       .getAllInvoices();

        // when
        ResponseEntity<List<Invoice>> response = invoiceController.getInvoices(null, null);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

}
