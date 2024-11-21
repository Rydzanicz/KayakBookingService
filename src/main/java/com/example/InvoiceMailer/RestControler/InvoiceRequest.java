package com.example.InvoiceMailer.RestControler;

import com.example.InvoiceMailer.service.Product;

import java.util.List;

public class InvoiceRequest {
    private String buyerName;
    private String buyerAddressEmail;
    private String buyerAddress;
    private String buyerNip;
    private List<Product> products;

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public void setBuyerAddressEmail(String buyerAddressEmail) {
        this.buyerAddressEmail = buyerAddressEmail;
    }

    public void setBuyerAddress(String buyerAddress) {
        this.buyerAddress = buyerAddress;
    }

    public void setBuyerNip(String buyerNip) {
        this.buyerNip = buyerNip;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public String getBuyerAddressEmail() {
        return buyerAddressEmail;
    }

    public String getBuyerAddress() {
        return buyerAddress;
    }

    public String getBuyerNip() {
        return buyerNip;
    }

    public List<Product> getProducts() {
        return products;
    }

}
