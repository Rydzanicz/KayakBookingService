package com.example.InvoiceMailer.RestControler;

import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.*;
import com.itextpdf.layout.element.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.io.*;
import java.time.*;

@Service
public class PdfService {
    @Value("${invoice.sellerName}")
    private String sellerName;


    @Value("${invoice.sellerAddress}")
    private String sellerAddress;


    @Value("${invoice.sellerNip}")
    private String sellerNip;
    @Value("${invoice.vatRate}")
    private int vatRate;

    @Value("${invoice.unitPrice}")
    private int unitPrice;

    @Value("${invoice.invoiceNumber}")
    private int invoiceNumber;

    public OffsetDateTime now = ZonedDateTime.now()
                                             .toOffsetDateTime();

    public ByteArrayOutputStream generateInvoicePdf(final String buyerName,
                                                    final String buyerAddress,
                                                    final String buyerNip,
                                                    final String description,
                                                    final int quantity) {


        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        invoiceNumber++;
        // Dodajemy zawartość dokumentu
        document.add(new Paragraph("Faktura VAT nr: " + invoiceNumber));
        document.add(new Paragraph("Sprzedawca: " + sellerName));
        document.add(new Paragraph("Adres: " + sellerAddress));
        document.add(new Paragraph("NIP: " + sellerNip));
        document.add(new Paragraph("Buyer: " + buyerName));
        document.add(new Paragraph("Adres: " + buyerAddress));
        document.add(new Paragraph("NIP: " + buyerNip));

        document.add(new Paragraph("Data wystawienia: " + now));
        document.add(new Paragraph("Data sprzedaży: " + now));

        // Szczegóły pozycji na fakturze
        double netAmount = quantity * unitPrice;
        double vatAmount = netAmount * (vatRate / 100);
        double grossAmount = netAmount + vatAmount;

        document.add(new Paragraph("Opis: " + description));
        document.add(new Paragraph("Ilość: " + quantity));
        document.add(new Paragraph("Cena netto: " + unitPrice));
        document.add(new Paragraph("Wartość netto: " + netAmount));
        document.add(new Paragraph("Stawka VAT: " + vatRate));
        document.add(new Paragraph("Wartość VAT: " + vatAmount));
        document.add(new Paragraph("Wartość brutto: " + grossAmount));

        document.add(new Paragraph("Podsumowanie:"));
        document.add(new Paragraph("Suma netto: " + netAmount));
        document.add(new Paragraph("Suma VAT: " + vatAmount));
        document.add(new Paragraph("Suma brutto: " + grossAmount));

        document.close();

        return out;
    }
}