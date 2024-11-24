package com.example.InvoiceMailer.model;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoices")
public class InvoiceEntity {

    @Id
    @Column(name = "invoice_id", nullable = false)
    private String invoiceId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "nip")
    private String nip;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderEntity> orders;

    public InvoiceEntity() {
        this.invoiceId = null;
        this.name = null;
        this.address = null;
        this.email = null;
        this.nip = null;
        this.orders = new ArrayList<>();
    }

    public InvoiceEntity(final Invoice invoice) {
        this.invoiceId = invoice.getInvoiceId();
        this.name = invoice.getBuyerName();
        this.address = invoice.getBuyerAddress();
        this.email = invoice.getBuyerAddressEmail();
        this.nip = invoice.getBuyerNIP();
        this.orders = invoice.getProduct()
                             .stream()
                             .map(OrderEntity::new)
                             .toList();
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNip() {
        return nip;
    }

    public void setNip(String nip) {
        this.nip = nip;
    }

    public List<OrderEntity> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderEntity> orders) {
        this.orders = orders;
    }
}