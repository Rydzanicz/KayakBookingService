package com.example.InvoiceMailer;

import com.example.InvoiceMailer.model.Invoice;
import com.example.InvoiceMailer.model.InvoiceEntity;
import com.example.InvoiceMailer.model.Product;
import com.example.InvoiceMailer.repository.InvoiceRepository;
import com.example.InvoiceMailer.service.InvoiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class InvoiceServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    private InvoiceService invoiceService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        invoiceService = new InvoiceService(invoiceRepository);
    }

    @Test
    public void testGetAllInvoices() {
        // given
        final List<Product> products = new ArrayList<>();
        products.add(new Product("Produkt A", "Opis A", 1, 100.0));

        final List<InvoiceEntity> entities = new ArrayList<>();
        entities.add(new InvoiceEntity(new Invoice(1, "Jan Kowalski", "Popowicka 68", "jan.kowalski@example.com", null, products)));
        entities.add(new InvoiceEntity(new Invoice(2, "Anna Nowak", "Kwiatowa 12", "anna.nowak@example.com", null, products)));
        when(invoiceRepository.findAll()).thenReturn(entities);

        // when
        final List<Invoice> invoices = invoiceService.getAllInvoices();

        // then
        assertNotNull(invoices);
        assertEquals(2, invoices.size());
        assertEquals("Jan Kowalski",
                     invoices.get(0)
                             .getBuyerName());
        assertEquals("Anna Nowak",
                     invoices.get(1)
                             .getBuyerName());
        verify(invoiceRepository, times(1)).findAll();
    }

    @Test
    public void testGetUniqueEmail() {
        // given
        final List<String> emails = List.of("jan.kowalski@example.com", "anna.nowak@example.com");
        when(invoiceRepository.findUniqueEmails()).thenReturn(emails);

        // when
        final List<String> uniqueEmails = invoiceService.getUniqueEmail();

        // then
        assertNotNull(uniqueEmails);
        assertEquals(2, uniqueEmails.size());
        assertTrue(uniqueEmails.contains("jan.kowalski@example.com"));
        assertTrue(uniqueEmails.contains("anna.nowak@example.com"));
        verify(invoiceRepository, times(1)).findUniqueEmails();
    }

    @Test
    public void testGetInvoicesByInvoiceId() {
        // given
        final List<Product> products = new ArrayList<>();
        products.add(new Product("Produkt A", "Opis A", 1, 100.0));
        final String invoiceId = "FV/000000001/2024";
        final List<InvoiceEntity> entities = List.of(new InvoiceEntity(new Invoice(1,
                                                                                   "Jan Kowalski",
                                                                                   "Popowicka 68",
                                                                                   "jan.kowalski@example.com",
                                                                                   null,
                                                                                   products)));
        when(invoiceRepository.findInvoicesByInvoiceId(invoiceId)).thenReturn(entities);

        // when
        final List<Invoice> invoices = invoiceService.getInvoicesByInvoiceId(invoiceId);

        // then
        assertNotNull(invoices);
        assertEquals(1, invoices.size());
        assertEquals("FV/000000001/2024",
                     invoices.get(0)
                             .getInvoiceId());
        verify(invoiceRepository, times(1)).findInvoicesByInvoiceId(invoiceId);
    }

    @Test
    public void testGetLastInvoices() {
        // given
        final List<Product> products = new ArrayList<>();
        products.add(new Product("Produkt A", "Opis A", 1, 100.0));

        final InvoiceEntity lastInvoiceEntity = new InvoiceEntity(new Invoice(5,
                                                                              "Anna Nowak",
                                                                              "Kwiatowa 12",
                                                                              "anna.nowak@example.com",
                                                                              null,
                                                                              products));
        when(invoiceRepository.getLastInvoices()).thenReturn(lastInvoiceEntity);

        // when
        final Invoice lastInvoice = invoiceService.getLastInvoices();

        // then
        assertNotNull(lastInvoice);
        assertEquals("FV/000000005/2024", lastInvoice.getInvoiceId());
        assertEquals("Anna Nowak", lastInvoice.getBuyerName());
        assertEquals("Kwiatowa 12", lastInvoice.getBuyerAddress());
        assertEquals("anna.nowak@example.com", lastInvoice.getBuyerAddressEmail());
        verify(invoiceRepository, times(1)).getLastInvoices();
    }

    @Test
    public void testGetInvoicesByAddressEmail() {
        // given
        final List<Product> products = new ArrayList<>();
        products.add(new Product("Produkt A", "Opis A", 1, 100.0));
        final String email = "jan.kowalski@example.com";
        final List<InvoiceEntity> entities = List.of(new InvoiceEntity(new Invoice(1,
                                                                                   "Jan Kowalski",
                                                                                   "Popowicka 68",
                                                                                   email,
                                                                                   null,
                                                                                   products)));
        when(invoiceRepository.findInvoicesByEmail(email)).thenReturn(entities);

        // when
        final List<Invoice> invoices = invoiceService.getInvoicesByAddressEmail(email);

        // then
        assertNotNull(invoices);
        assertEquals(1, invoices.size());
        assertEquals("jan.kowalski@example.com",
                     invoices.get(0)
                             .getBuyerAddressEmail());
        verify(invoiceRepository, times(1)).findInvoicesByEmail(email);
    }

    @Test
    public void testSaveInvoice() {
        // given
        final List<Product> products = new ArrayList<>();
        products.add(new Product("Produkt A", "Opis A", 1, 100.0));
        final Invoice invoice = new Invoice(1, "Jan Kowalski", "Popowicka 68", "jan.kowalski@example.com", null, products);
        final InvoiceEntity savedEntity = new InvoiceEntity(invoice);
        when(invoiceRepository.save(any(InvoiceEntity.class))).thenReturn(savedEntity);

        // when
        final InvoiceEntity result = invoiceService.saveInvoiceWithOrders(invoice, products);

        // then
        assertNotNull(result);
        assertEquals("Jan Kowalski", result.getName());
        verify(invoiceRepository, times(1)).save(any(InvoiceEntity.class));
    }

    @Test
    public void testSaveInvoiceThrowsExceptionForNullInvoice() {
        // given
        // when
        // then
        assertThrows(IllegalArgumentException.class, () -> invoiceService.saveInvoiceWithOrders(null, null));
    }
}