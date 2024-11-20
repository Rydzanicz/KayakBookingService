package com.example.InvoiceMailer.RestControler;

import com.example.InvoiceMailer.service.PdfService;
import com.example.InvoiceMailer.service.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

@RestController
public class InvoiceController {
    @Value("${invoice.sellerName: Michał Rydzanicz}")
    private String sellerName;

    @Value("${invoice.sellerName: VIGGO-Programer sp. z o.o.}")
    private String sellerfirmaName;

    @Value("${invoice.sellerAddress: Popowicka 68/17, 54-237 Wrocław}")
    private String sellerAddress;

    @Value("${invoice.sellerNip}")
    private String sellerNip;
    @Value("${invoice.vatRate}")
    private int vatRate;

    @Value("${invoice.unitPrice}")
    private int unitPrice;

    @PostMapping(value = "/invoice", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getInvoice(@RequestParam(value = "buyerName", defaultValue = "Adam Nowak") String buyerName,
                                             @RequestParam(value = "buyerAddressEmail", defaultValue = "AdamNowak@wp.pl") String buyerAddressEmail,
                                             @RequestParam(value = "buyerAddress", defaultValue = "Popowicka 68/17, 54-237 Wrocław") String buyerAddress,
                                             @RequestParam(value = "buyerNip", defaultValue = "") String buyerNip,
                                             @RequestParam(value = "name", defaultValue = "456456456") String name,
                                             @RequestParam(value = "description", defaultValue = "Milka") String description,
                                             @RequestParam(value = "price", defaultValue = "100") int price,
                                             @RequestParam(value = "quantity", defaultValue = "1") int quantity) {


        try {
            final PdfService pdfService = new PdfService();
            final int invoiceNumber = 110;
            final ArrayList<Product> productList = new ArrayList<>();
            productList.add(new Product(quantity, price, name, description));
            productList.add(new Product(1, 123, name, description));

            final ByteArrayOutputStream out = pdfService.generateInvoicePdf(invoiceNumber,
                                                                            sellerName,
                                                                            sellerfirmaName,
                                                                            sellerAddress,
                                                                            sellerNip,
                                                                            buyerName,
                                                                            buyerAddress,
                                                                            buyerAddressEmail,
                                                                            buyerNip,
                                                                            productList);

            final HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "inline; filename=" + invoiceNumber + ".pdf");

            return ResponseEntity.ok()
                                 .contentType(MediaType.APPLICATION_PDF)
                                 .body(out.toByteArray());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .build();
        }
    }
}
