package com.example.InvoiceMailer;

import com.example.InvoiceMailer.service.PdfService;
import com.example.InvoiceMailer.model.Product;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class PdfServiceTest {

    @Test
    public void testGenerateInvoicePdf() throws Exception {
        //given
        final String invoiceNumber = "FV/01/2024";
        final String buyerName = "Nabywca";
        final String buyerAddress = "ul. Przykładowa 2, 00-000 Warszawa";
        final String buyerEmail = "buyer@example.com";
        final String buyerNip = "0987654321";

        final ArrayList<Product> products = new ArrayList<>();
        products.add(new Product("Produkt A", "Opis A", 2, 100.0));

        final PdfService pdfService = Mockito.spy(PdfService.class);

        //when
        final ByteArrayOutputStream pdfOutput = pdfService.generateInvoicePdf(invoiceNumber,
                                                                              buyerName,
                                                                              buyerAddress,
                                                                              buyerEmail,
                                                                              buyerNip,
                                                                              products);
        //then
        assertNotNull(pdfOutput);
        assertTrue(pdfOutput.size() > 0);
    }

    @Test
    public void testCalculateVat() {
        //given
        final ArrayList<Product> products = new ArrayList<>();
        products.add(new Product("Produkt A", "Opis A", 2, 100.0));

        final PdfService pdfService = new PdfService();
        //when

        final double sumNet = products.stream()
                                      .mapToDouble(p -> p.getPrice() * p.getQuantity())
                                      .sum();
        final double sumVat = products.stream()
                                      .mapToDouble(p -> (p.getVAT()) * p.getQuantity())
                                      .sum();
        //then
        assertEquals(200.0, sumNet);
        assertEquals(46.0, sumVat);
    }

    @Test
    public void testCurrencyFormatting() throws Exception {
        //given
        final String invoiceNumber = "FV/01/2024";
        final String buyerName = "Nabywca";
        final String buyerAddress = "ul. Przykładowa 2";
        final String buyerEmail = "buyer@example.com";
        final String buyerNip = "0987654321";

        final ArrayList<Product> products = new ArrayList<>();
        products.add(new Product("Produkt A", "Opis A", 2, 100.0));

        final PdfService pdfService = new PdfService();
        final ByteArrayOutputStream pdfOutput = pdfService.generateInvoicePdf(invoiceNumber,
                                                                              buyerName,
                                                                              buyerAddress,
                                                                              buyerEmail,
                                                                              buyerNip,
                                                                              products);


        //when
        final double sumNet = products.stream()
                                      .mapToDouble(p -> p.getPrice() * p.getQuantity())
                                      .sum();

        //then
        assertNotNull(pdfOutput);
        assertTrue(pdfOutput.size() > 0);
        assertEquals("200,00", new DecimalFormat("#.00").format(sumNet));
    }

    @Test
    public void testLargeNumberOfProducts() throws Exception {
        //given
        final String invoiceNumber = "FV/01/2024";
        final String buyerName = "Nabywca";
        final String buyerAddress = "ul. Przykładowa 2";
        final String buyerEmail = "buyer@example.com";
        final String buyerNip = "0987654321";

        final ArrayList<Product> products = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            products.add(new Product("Produkt " + i, "Opis " + i, 2, 100.0));
        }

        final PdfService pdfService = new PdfService();

        //when
        final ByteArrayOutputStream pdfOutput = pdfService.generateInvoicePdf(invoiceNumber,
                                                                              buyerName,
                                                                              buyerAddress,
                                                                              buyerEmail,
                                                                              buyerNip,
                                                                              products);
        //then
        assertNotNull(pdfOutput);
        assertTrue(pdfOutput.size() > 0);
    }

    @Test
    public void testVatCalculation() throws Exception {
        //given
        final String invoiceNumber = "FV/01/2024";
        final String buyerName = "Nabywca";
        final String buyerAddress = "ul. Przykładowa 2";
        final String buyerEmail = "buyer@example.com";
        final String buyerNip = "0987654321";

        final ArrayList<Product> products = new ArrayList<>();
        products.add(new Product("Produkt A", "Opis A", 2, 100.0));

        final PdfService pdfService = new PdfService();

        //when
        final ByteArrayOutputStream pdfOutput = pdfService.generateInvoicePdf(invoiceNumber,
                                                                              buyerName,
                                                                              buyerAddress,
                                                                              buyerEmail,
                                                                              buyerNip,
                                                                              products);

        //then
        assertNotNull(pdfOutput);
        assertTrue(pdfOutput.size() > 0);
        assertEquals(46.0,
                     products.stream()
                             .mapToDouble(p -> (p.getPriceWithVAT() - p.getPrice()) * p.getQuantity())
                             .sum());
    }

    @Test
    public void testEmptyProductList() {
        //given
        final String invoiceNumber = "FV/01/2024";
        final String buyerName = "Nabywca";
        final String buyerAddress = "ul. Przykładowa 2";
        final String buyerEmail = "buyer@example.com";
        final String buyerNip = "0987654321";

        final ArrayList<Product> products = new ArrayList<>();

        assertThrows(IllegalArgumentException.class, () -> {
            PdfService pdfService = new PdfService();
            pdfService.generateInvoicePdf(invoiceNumber, buyerName, buyerAddress, buyerEmail, buyerNip, products);
        });
    }
}
