package com.example.InvoiceMailer;

import com.example.InvoiceMailer.model.Order;
import com.example.InvoiceMailer.service.PdfGeneratorService;
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
public class PdfGeneratorServiceTest {

    @Test
    public void testGenerateInvoicePdf() throws Exception {
        //given
        final String invoiceNumber = "FV/01/2024";
        final String buyerName = "Nabywca";
        final String buyerAddress = "ul. Przykładowa 2, 00-000 Warszawa";
        final String buyerEmail = "buyer@example.com";
        final String buyerNip = "0987654321";

        final ArrayList<Order> orders = new ArrayList<>();
        orders.add(new Order("Produkt A", "Opis A", 2, 100.0));

        final PdfGeneratorService pdfGeneratorService = Mockito.spy(PdfGeneratorService.class);

        //when
        final ByteArrayOutputStream pdfOutput = pdfGeneratorService.generateInvoicePdf(invoiceNumber,
                                                                                       buyerName,
                                                                                       buyerAddress,
                                                                                       buyerEmail,
                                                                                       buyerNip,
                                                                                       orders);
        //then
        assertNotNull(pdfOutput);
        assertTrue(pdfOutput.size() > 0);
    }

    @Test
    public void testCalculateVat() {
        //given
        final ArrayList<Order> orders = new ArrayList<>();
        orders.add(new Order("Produkt A", "Opis A", 2, 100.0));
        //when

        final double sumNet = orders.stream()
                                    .mapToDouble(p -> p.getPrice() * p.getQuantity())
                                    .sum();
        final double sumVat = orders.stream()
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

        final ArrayList<Order> orders = new ArrayList<>();
        orders.add(new Order("Produkt A", "Opis A", 2, 100.0));

        final PdfGeneratorService pdfGeneratorService = new PdfGeneratorService();
        final ByteArrayOutputStream pdfOutput = pdfGeneratorService.generateInvoicePdf(invoiceNumber,
                                                                                       buyerName,
                                                                                       buyerAddress,
                                                                                       buyerEmail,
                                                                                       buyerNip,
                                                                                       orders);


        //when
        final double sumNet = orders.stream()
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

        final ArrayList<Order> orders = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            orders.add(new Order("Produkt " + i, "Opis " + i, 2, 100.0));
        }

        final PdfGeneratorService pdfGeneratorService = new PdfGeneratorService();

        //when
        final ByteArrayOutputStream pdfOutput = pdfGeneratorService.generateInvoicePdf(invoiceNumber,
                                                                                       buyerName,
                                                                                       buyerAddress,
                                                                                       buyerEmail,
                                                                                       buyerNip,
                                                                                       orders);
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

        final ArrayList<Order> orders = new ArrayList<>();
        orders.add(new Order("Produkt A", "Opis A", 2, 100.0));

        final PdfGeneratorService pdfGeneratorService = new PdfGeneratorService();

        //when
        final ByteArrayOutputStream pdfOutput = pdfGeneratorService.generateInvoicePdf(invoiceNumber,
                                                                                       buyerName,
                                                                                       buyerAddress,
                                                                                       buyerEmail,
                                                                                       buyerNip,
                                                                                       orders);

        //then
        assertNotNull(pdfOutput);
        assertTrue(pdfOutput.size() > 0);
        assertEquals(46.0,
                     orders.stream()
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

        final ArrayList<Order> orders = new ArrayList<>();

        assertThrows(IllegalArgumentException.class, () -> {
            PdfGeneratorService pdfGeneratorService = new PdfGeneratorService();
            pdfGeneratorService.generateInvoicePdf(invoiceNumber, buyerName, buyerAddress, buyerEmail, buyerNip, orders);
        });
    }
}
