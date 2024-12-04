package com.example.InvoiceMailer.service;

import com.example.InvoiceMailer.model.Invoice;
import com.example.InvoiceMailer.model.InvoiceEntity;
import com.example.InvoiceMailer.model.Order;
import com.example.InvoiceMailer.model.OrderEntity;
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

    public void updateEmailSendStatus(final String invoiceId, final boolean status) {
        invoiceRepository.updateEmailSendStatus(invoiceId, status);
    }

    public Invoice getInvoicesByInvoiceId(String invoiceId) {
        final InvoiceEntity entities = invoiceRepository.findInvoicesByInvoiceId(invoiceId);
        return new Invoice(entities);
    }

    public List<Invoice> getInvoicesByAddressEmail(String addressEmail) {
        final List<InvoiceEntity> entities = invoiceRepository.findInvoicesByEmail(addressEmail);
        return entities.stream()
                       .map(this::mapToInvoice)
                       .toList();
    }
    public List<Invoice> getNoSendInvoices() {
        final List<InvoiceEntity> entities = invoiceRepository.findNoSendInvoices();
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
                           entity.getOrderDate(),
                           entity.isEmailSend(),
                           entity.getOrders()
                                 .stream()
                                 .map(Order::new)
                                 .toList());
    }

    public void saveInvoiceWithOrders(final Invoice invoice, final List<Order> orders) {
        if (orders == null || orders.isEmpty()) {
            throw new IllegalArgumentException("List of Product cannot be null or empty.");
        }
        final InvoiceEntity invoiceEntity = new InvoiceEntity(invoice);

        final List<OrderEntity> ordersEntity = orders.stream()
                                                     .map(order -> {
                                                         OrderEntity orderEntity = new OrderEntity(order);
                                                         orderEntity.setInvoice(invoiceEntity);
                                                         return orderEntity;
                                                     })
                                                     .toList();

        invoiceEntity.setOrders(ordersEntity);

        invoiceRepository.save(invoiceEntity);
    }
}