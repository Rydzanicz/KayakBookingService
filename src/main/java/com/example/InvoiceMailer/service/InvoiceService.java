package com.example.InvoiceMailer.service;

import com.example.InvoiceMailer.model.Invoice;
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

    public List<Invoice> getAllInvoices() {
        final List<InvoiceEntity> entities = invoiceRepository.findAll();
        return entities.stream()
                       .map(this::mapToInvoice)
                       .toList();
    }

    public List<String> getUniqueEmail() {
        return invoiceRepository.findUniqueEmails();
    }

    public Invoice getLastInvoices() {
        return new Invoice(invoiceRepository.getLastInvoices());
    }

    public List<Invoice> getInvoicesByInvoiceId(String invoiceId) {
        final List<InvoiceEntity> entities = invoiceRepository.findInvoicesByInvoiceId(invoiceId);
        return entities.stream()
                       .map(this::mapToInvoice)
                       .toList();
    }

    public List<Invoice> getInvoicesByAddressEmail(String addressEmail) {
        final List<InvoiceEntity> entities = invoiceRepository.findInvoicesByEmail(addressEmail);
        return entities.stream()
                       .map(this::mapToInvoice)
                       .toList();
    }

    private Invoice mapToInvoice(InvoiceEntity entity) {
        return new Invoice(Integer.parseInt(entity.getInvoiceId()
                                                  .split("/")[1]), entity.getName(), entity.getAddress(), entity.getEmail());
    }

    public InvoiceEntity saveInvoice(final Invoice invoice) {
        if (invoice == null) {
            throw new IllegalArgumentException("Invoice cannot be null");
        }

        final InvoiceEntity invoiceEntity = new InvoiceEntity(invoice);

        return invoiceRepository.save(invoiceEntity);
    }
}