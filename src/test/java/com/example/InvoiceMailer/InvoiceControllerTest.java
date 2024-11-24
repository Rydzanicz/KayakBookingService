package com.example.InvoiceMailer;

import com.example.InvoiceMailer.RestControler.InvoiceController;
import com.example.InvoiceMailer.RestControler.InvoiceRequest;
import com.example.InvoiceMailer.model.Invoice;
import com.example.InvoiceMailer.model.InvoiceEntity;
import com.example.InvoiceMailer.model.Product;
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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
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

        final List<Product> products = new ArrayList<>();
        products.add(new Product("Produkt A", "Opis A", 1, 100.0));
        invoiceRequest.setProducts(products);

        final Invoice mockInvoice = new Invoice(1, "Jan Kowalski", "Popowicka 68", "jan.kowalski@example.com");
        final InvoiceEntity savedEntity = new InvoiceEntity(mockInvoice);

        when(invoiceService.getLastInvoices()).thenReturn(mockInvoice);
        when(invoiceService.saveInvoice(any(Invoice.class))).thenReturn(savedEntity);

        // when
        final ResponseEntity<?> response = invoiceController.generateInvoice(invoiceRequest);

        // then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        verify(invoiceService, times(1)).saveInvoice(any(Invoice.class));
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
        invoiceRequest.setProducts(List.of(new Product("Product A", "Description A", 1, 100.0)));

        byte[] pdfBytes = "PDF content".getBytes();
        when(pdfGeneratorService.generateInvoicePdf(anyString(), anyString(), anyString(), anyString(), anyString(), anyList())).thenReturn(
                new ByteArrayOutputStream() {{
                    write(pdfBytes);
                }});
        when(invoiceService.getLastInvoices()).thenReturn(new Invoice(1, "Last Buyer", "Last Address", "last@example.com"));

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
        final List<Invoice> invoices = new ArrayList<>();
        invoices.add(new Invoice(1, "Jan Kowalski", "Popowicka 68", "jan.kowalski@example.com"));
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
        final List<Invoice> invoices = new ArrayList<>();
        invoices.add(new Invoice(1, "Jan Kowalski", "Popowicka 68", "jan.kowalski@example.com"));
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
        final List<Invoice> invoices = new ArrayList<>();
        invoices.add(new Invoice(1, "Jan Kowalski", "Popowicka 68", "jan.kowalski@example.com"));
        invoices.add(new Invoice(2, "Anna Nowak", "Kwiatowa 12", "anna.nowak@example.com"));
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
