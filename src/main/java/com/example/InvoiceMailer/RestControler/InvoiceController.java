package com.example.InvoiceMailer.RestControler;

import com.example.InvoiceMailer.model.Invoice;
import com.example.InvoiceMailer.service.InvoiceService;
import com.example.InvoiceMailer.service.PdfService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class InvoiceController {
    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping(value = "/generate-invoice", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> generateInvoice(@RequestBody InvoiceRequest invoiceRequest) {


        try {
            final PdfService pdfService = new PdfService();
            final Invoice invoice = invoiceService.getLastInvoices();
            final Invoice newInvoice = new Invoice(invoice.extractAndIncreaseInvoiceNumber(),
                                                   invoiceRequest.getBuyerName(),
                                                   invoiceRequest.getBuyerAddress(),
                                                   invoiceRequest.getBuyerAddressEmail());
            invoiceService.saveInvoice(newInvoice);

            final byte[] out = pdfService.generateInvoicePdf(invoice.getInvoiceId(),
                                                             invoiceRequest.getBuyerName(),
                                                             invoiceRequest.getBuyerAddress(),
                                                             invoiceRequest.getBuyerAddressEmail(),
                                                             invoiceRequest.getBuyerNip(),
                                                             invoiceRequest.getProducts())
                                         .toByteArray();

            final String fileName = "Faktura-" + invoice.getInvoiceId() + ".pdf";

            final Map<String, Object> response = new HashMap<>();
            response.put("fileName", fileName);

            final HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=" + fileName);
            return new ResponseEntity<>(out, headers, HttpStatus.OK);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .build();
        }
    }
}
