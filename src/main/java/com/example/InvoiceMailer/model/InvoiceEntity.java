package com.example.InvoiceMailer.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "invoices")
public class InvoiceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private final String invoice_id;
    private final String name;
    private final String address;
    private final String email;

    public InvoiceEntity() {
        this.invoice_id = null;
        this.name = null;
        this.address = null;
        this.email = null;
    }

    public InvoiceEntity(final Invoice invoice) {
        this.invoice_id = invoice.getInvoiceId();
        this.name = invoice.getBuyerName();
        this.address = invoice.getBuyerAddress();
        this.email = invoice.getBuyerAddressEmail();
    }

    public String getInvoiceId() {
        return invoice_id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }
}