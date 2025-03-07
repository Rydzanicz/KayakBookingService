package com.example.KayakBooking.controler;

public class KayakBookingRequest {
    private String buyerName;
    private String buyerAddressEmail;
    private String buyerPhone;
    private String orderDate;
    private int kayakOne;
    private int kayakTwo;
    private int kayakOne_Two;

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public void setBuyerAddressEmail(String buyerAddressEmail) {
        this.buyerAddressEmail = buyerAddressEmail;
    }

    public void setBuyerPhone(String buyerPhone) {
        this.buyerPhone = buyerPhone;
    }


    public String getBuyerName() {
        return buyerName;
    }

    public String getBuyerAddressEmail() {
        return buyerAddressEmail;
    }

    public String getBuyerPhone() {
        return buyerPhone;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public int getKayakOne() {
        return kayakOne;
    }

    public void setKayakOne(int kayakOne) {
        this.kayakOne = kayakOne;
    }

    public int getKayakTwo() {
        return kayakTwo;
    }

    public void setKayakTwo(int kayakTwo) {
        this.kayakTwo = kayakTwo;
    }

    public int getKayakOne_Two() {
        return kayakOne_Two;
    }

    public void setKayakOne_Two(int kayakOne_Two) {
        this.kayakOne_Two = kayakOne_Two;
    }
}
