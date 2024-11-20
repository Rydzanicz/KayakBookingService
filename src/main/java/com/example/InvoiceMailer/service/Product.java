package com.example.InvoiceMailer.service;

public class Product {
    private final int quantity;
    private final double price;
    private final double priceWithVAT;
    private final String name;
    private final String description;

    public Product(final int quantity, final double price, final String name, final String description) {
        this.quantity = quantity;
        this.price = price;
        this.priceWithVAT = price * 1.23;
        this.name = name;
        this.description = description;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public double getPriceWithVAT() {
        return priceWithVAT;
    }

    public double getVAT() {
        return priceWithVAT - price;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
