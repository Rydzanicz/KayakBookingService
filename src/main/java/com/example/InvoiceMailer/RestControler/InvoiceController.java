package com.example.InvoiceMailer.RestControler;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.*;

@RestController
public class InvoiceController {

    @PostMapping(value = "/invoice", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getInvoice(@RequestParam(value = "buyerName", defaultValue = "Adam Nowak") String buyerName,
                                             @RequestParam(value = "buyerAddress", defaultValue = "Popowicka 68/17, 54-237 Wrocław") String buyerAddress,
                                             @RequestParam(value = "buyerNip", defaultValue = "456456456") String buyerNip,
                                             @RequestParam(value = "description", defaultValue = "Milka") String description,
                                             @RequestParam(value = "quantity", defaultValue = "1") int quantity) {


        try {
            final PdfService pdfService = new PdfService();
            ByteArrayOutputStream out = pdfService.generateInvoicePdf(buyerName,
                                                                      buyerAddress,
                                                                      buyerNip,
                                                                      description,
                                                                      quantity);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition",
                        "inline; filename=lastBalance.pdf");

            return ResponseEntity.ok()
                                 .headers(headers)
                                 .contentType(MediaType.APPLICATION_PDF)
                                 .body(out.toByteArray());
        } catch (
                Exception e) {
            return ResponseEntity.status(500)
                                 .body(null);
        }
    }
}