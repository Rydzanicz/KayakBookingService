package com.example.InvoiceMailer.service;

import com.example.InvoiceMailer.model.Invoice;
import com.example.InvoiceMailer.model.InvoiceEntity;
import com.example.InvoiceMailer.model.OrderEntity;
import com.example.InvoiceMailer.model.Product;
import com.example.InvoiceMailer.repository.InvoiceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;

    public InvoiceService(final InvoiceRepository invoiceRepository) {
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

    private Invoice mapToInvoice(final InvoiceEntity entity) {
        return new Invoice(Integer.parseInt(entity.getInvoiceId()
                                                  .split("/")[1]),
                           entity.getName(),
                           entity.getAddress(),
                           entity.getEmail(),
                           entity.getNip(),
                           entity.getOrders()
                                 .stream()
                                 .map(Product::new)
                                 .toList());
    }

    public InvoiceEntity saveInvoiceWithOrders(final Invoice invoice, final List<Product> products) {
        if (products == null || products.isEmpty()) {
            throw new IllegalArgumentException("List of Product cannot be null or empty.");
        }
        final InvoiceEntity invoiceEntity = new InvoiceEntity(invoice);

        final List<OrderEntity> ordersEntity = products.stream()
                                                       .map(order -> {
                                                           OrderEntity orderEntity = new OrderEntity(order);
                                                           orderEntity.setInvoice(invoiceEntity);
                                                           return orderEntity;
                                                       })
                                                       .toList();

        invoiceEntity.setOrders(ordersEntity);

        return invoiceRepository.save(invoiceEntity);
    }
}