package com.example.InvoiceMailer.RestControler;

import com.example.InvoiceMailer.model.Invoice;
import com.example.InvoiceMailer.service.InvoiceService;
import com.example.InvoiceMailer.service.PdfGeneratorService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class InvoiceController {
    private final InvoiceService invoiceService;

    public InvoiceController(final InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping(value = "/save-invoice", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> saveInvoice(@RequestBody InvoiceRequest invoiceRequest) {
        if (invoiceRequest == null || invoiceRequest.getBuyerName() == null || invoiceRequest.getOrders() == null) {
            throw new IllegalArgumentException("Invalid request payload");
        }

        try {
            final Invoice lastInvoice = invoiceService.getLastInvoices();
            final Invoice newInvoice = new Invoice(lastInvoice.extractAndIncreaseInvoiceNumber(),
                                                   invoiceRequest.getBuyerName(),
                                                   invoiceRequest.getBuyerAddress(),
                                                   invoiceRequest.getBuyerAddressEmail(),
                                                   invoiceRequest.getBuyerNip(),
                                                   LocalDateTime.now(),
                                                   false,
                                                   invoiceRequest.getOrders());

            invoiceService.saveInvoiceWithOrders(newInvoice, invoiceRequest.getOrders());

            return ResponseEntity.ok("Invoice saved successfully");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error saving invoice");
        }
    }

    @PostMapping(value = "/generate-invoice", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> generateInvoice(@RequestParam() String invoiceId) {
        if (invoiceId == null) {
            throw new IllegalArgumentException("Invalid request payload");
        }

        try {
            final PdfGeneratorService pdfGeneratorService = new PdfGeneratorService();
            final Invoice invoice = invoiceService.getInvoicesByInvoiceId(invoiceId);

            final byte[] out = pdfGeneratorService.generateInvoicePdf(invoice)
                                                  .toByteArray();


            final String fileName = "Faktura-" + invoiceId + ".pdf";


            final Map<String, Object> response = new HashMap<>();
            response.put("fileName", fileName);

            final HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=" + fileName);
            return new ResponseEntity<>(out, headers, HttpStatus.OK);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error generating invoice");
        }
    }

    @GetMapping(value = "/get-invoices-data", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Invoice>> getInvoices(@RequestParam(required = false) String invoiceId,
                                                     @RequestParam(required = false) String addressEmail) {

        try {
            List<Invoice> invoices = new ArrayList<>();

            if (invoiceId != null && !invoiceId.isEmpty()) {
                invoices.add(invoiceService.getInvoicesByInvoiceId(invoiceId));
            } else {
                if (addressEmail != null && !addressEmail.isEmpty()) {
                    invoices = invoiceService.getInvoicesByAddressEmail(addressEmail);
                } else {
                    invoices = invoiceService.getAllInvoices();
                }
            }

            if (invoices == null || invoices.isEmpty()) {
                return ResponseEntity.notFound()
                                     .build();
            }

            return ResponseEntity.ok(invoices);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .build();
        }
    }

    @GetMapping(value = "/get-all-unique-email", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> getUniqueEmail() {

        try {
            final List<String> invoices = invoiceService.getUniqueEmail();

            return ResponseEntity.ok(invoices);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .build();
        }
    }
}
