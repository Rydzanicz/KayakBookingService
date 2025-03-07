package com.example.KayakBooking.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "orders")
public class OrdersEntity {

    @Id
    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phone")
    private final String phone;

    @Column(name = "order_date", nullable = false)
    private String orderDate;

    @Column(name = "is_email_send", nullable = false)
    private final boolean isEmailSend;

    @Column(name = "kayak_one", nullable = false)
    private final int kayakOne;

    @Column(name = "kayak_two", nullable = false)
    private final int kayakTwo;

    @Column(name = "kayak_one_two", nullable = false)
    private final int kayakOne_Two;


    public OrdersEntity() {
        this.orderId = null;
        this.name = null;
        this.email = null;
        this.phone = null;
        this.orderDate = null;
        this.kayakOne = 0;
        this.kayakTwo = 0;
        this.kayakOne_Two = 0;
        this.isEmailSend = false;
    }

    public OrdersEntity(final KayakBooking kayakBooking) {
        this.orderId = kayakBooking.getOrderId();
        this.name = kayakBooking.getBuyerName();
        this.email = kayakBooking.getBuyerAddressEmail();
        this.phone = kayakBooking.getBuyerPhone();
        this.orderDate = kayakBooking.getOrderDate()
                                     .format(kayakBooking.getFormatter());
        this.kayakOne = kayakBooking.getKayakOne();
        this.kayakTwo = kayakBooking.getKayakTwo();
        this.kayakOne_Two = kayakBooking.getKayakOne_Two();
        this.isEmailSend = kayakBooking.isEmailSend();
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getPhone() {
        return phone;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public boolean isEmailSend() {
        return isEmailSend;
    }

    public int getKayakOne() {
        return kayakOne;
    }

    public int getKayakTwo() {
        return kayakTwo;
    }

    public int getKayakOne_Two() {
        return kayakOne_Two;
    }
}