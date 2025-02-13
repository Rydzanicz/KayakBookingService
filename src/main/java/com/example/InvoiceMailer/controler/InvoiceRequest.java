package com.example.InvoiceMailer.controler;

import com.example.InvoiceMailer.model.Order;

import java.util.List;

public class InvoiceRequest {
    private String buyerName;
    private String buyerAddressEmail;
    private String buyerAddress;
    private String buyerNip;
    private String buyerPhone;
    private List<Order> orders;

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

    public void setBuyerPhone(String buyerPhone) {
        this.buyerPhone = buyerPhone;
    }


    public void setOrders(List<Order> orders) {
        this.orders = orders;
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

    public String getBuyerPhone() {
        return buyerPhone;
    }

    public List<Order> getOrders() {
        return orders;
    }

}
