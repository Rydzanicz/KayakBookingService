package com.example.InvoiceMailer.service;

import com.example.InvoiceMailer.model.InvoiceEntity;
import com.example.InvoiceMailer.repository.InvoiceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;

    public InvoiceService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    public List<InvoiceEntity> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    public Invoice getLastInvoices() {
        final List<InvoiceEntity> invoices = invoiceRepository.findAll();

        if (invoices.isEmpty()) {
            throw new IllegalStateException("Brak faktur w bazie.");
        }
        return new Invoice(invoiceRepository.findAll()
                                            .stream()
                                            .reduce((x, y) -> x)
                                            .get());
    }

    public InvoiceEntity saveInvoice(final Invoice invoice) {
        if (invoice == null) {
            throw new IllegalArgumentException("Invoice cannot be null");
        }

        final InvoiceEntity invoiceEntity = new InvoiceEntity(invoice);

        return invoiceRepository.save(invoiceEntity);
    }
}