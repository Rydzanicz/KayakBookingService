package com.example.KayakBooking.model;

import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KayakBooking {
    private static final String ORDER_ID_PATTERN = "^ORDER/(\\d{1,9})/(\\d{4})$";
    private static final String FORMAT = "ORDER/%09d/%s";
    private final String orderId;
    private final String buyerName;
    private final String buyerAddressEmail;
    private final String buyerPhone;
    private int kayakOne;
    private int kayakTwo;
    private int kayakOne_Two;
    private final LocalDateTime orderDate;
    private boolean isEmailSend;
    private final TypeTrip typeTrip;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public KayakBooking(final int orderId,
                        final String buyerName,
                        final String buyerAddressEmail,
                        final String buyerPhone,
                        final LocalDateTime orderDate,
                        final int kayakOne,
                        final int kayakTwo,
                        final int kayakOne_Two,
                        final boolean isEmailSend,
                        final TypeTrip typeTrip) {
        if (buyerName == null || buyerName.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty.");
        }
        if (buyerAddressEmail == null || buyerAddressEmail.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty.");
        }
        if (buyerPhone == null || buyerPhone.isEmpty()) {
            throw new IllegalArgumentException("Phone cannot be null or empty.");
        }
        if (orderDate == null) {
            throw new IllegalArgumentException("Order date cannot be null or empty.");
        }


        this.orderId = generateOrderId(orderId);
        this.buyerName = buyerName;
        this.buyerAddressEmail = buyerAddressEmail;
        this.buyerPhone = buyerPhone;
        this.orderDate = orderDate;
        this.isEmailSend = isEmailSend;
        this.kayakOne = kayakOne;
        this.kayakTwo = kayakTwo;
        this.kayakOne_Two = kayakOne_Two;
        this.typeTrip = typeTrip;
    }

    public KayakBooking(final int orderNR,
                        final String buyerName,
                        final String buyerAddressEmail,
                        final String buyerPhone,
                        final String orderDate,
                        final int kayakOne,
                        final int kayakTwo,
                        final int kayakOne_Two,
                        final boolean isEmailSend,
                        final TypeTrip typeTrip) {
        if (buyerName == null || buyerName.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty.");
        }

        if (buyerAddressEmail == null || buyerAddressEmail.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty.");
        }
        if (buyerPhone == null || buyerPhone.isEmpty()) {
            throw new IllegalArgumentException("Phone cannot be null or empty.");
        }
        if (orderDate == null || orderDate.equals("")) {
            throw new IllegalArgumentException("Order date cannot be null or empty.");
        }


        this.orderId = generateOrderId(orderNR);
        this.buyerName = buyerName;
        this.buyerAddressEmail = buyerAddressEmail;
        this.buyerPhone = buyerPhone;
        this.orderDate = LocalDateTime.parse(orderDate, formatter);
        this.isEmailSend = isEmailSend;
        this.kayakOne = kayakOne;
        this.kayakTwo = kayakTwo;
        this.kayakOne_Two = kayakOne_Two;
        this.typeTrip = typeTrip;
    }

    public KayakBooking(final OrdersEntity order) {

        validateOrder(order.getOrderId());
        this.orderId = order.getOrderId();
        this.buyerName = order.getName();
        this.buyerAddressEmail = order.getEmail();
        this.buyerPhone = order.getPhone();
        this.orderDate = LocalDateTime.parse(order.getOrderDate(), formatter);
        this.kayakOne = order.getKayakOne();
        this.kayakTwo = order.getKayakTwo();
        this.kayakOne_Two = order.getKayakOne_Two();
        this.isEmailSend = order.isEmailSend();
        this.typeTrip = TypeTrip.valueOf(order.getTypeTrip());

    }

    public static String generateOrderId(int orderNumber) {
        if (orderNumber <= 0) {
            throw new IllegalArgumentException("Order ID cannot be 0 or less than 0.");
        }
        final String year = String.valueOf(Year.now()
                                               .getValue());
        return String.format(FORMAT, orderNumber, year);
    }

    public static boolean validateOrder(String orderId) {
        if (orderId == null || orderId.isEmpty()) {
            throw new IllegalArgumentException("OrderId cannot be null.");
        }

        if (!Pattern.matches(ORDER_ID_PATTERN, orderId)) {
            throw new IllegalArgumentException("Invalid order ID format. Correct format: ORDER/{number}/{year}, e.g., FV/001/2024");
        }
        return true;
    }

    private int extractOrderNumber() {
        final Pattern pattern = Pattern.compile(ORDER_ID_PATTERN);
        final Matcher matcher = pattern.matcher(orderId);

        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(1));
        } else {
            throw new IllegalArgumentException("Invalid order ID format. Cannot extract order number.");
        }
    }

    public int extractAndIncreaseOrderNumber() {
        return extractOrderNumber() + 1;
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

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public boolean isEmailSend() {
        return isEmailSend;
    }

    public DateTimeFormatter getFormatter() {
        return formatter;
    }

    public String getOrderId() {
        return orderId;
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

    public void setKayakOne(int kayakOne) {
        this.kayakOne = kayakOne;
    }

    public void setKayakTwo(int kayakTwo) {
        this.kayakTwo = kayakTwo;
    }

    public void setKayakOne_Two(int kayakOne_Two) {
        this.kayakOne_Two = kayakOne_Two;
    }

    public void setEmailSend(boolean emailSend) {
        isEmailSend = emailSend;
    }

    public void setEmailSend(String emailSend) {
        isEmailSend = Boolean.parseBoolean(emailSend);
    }

    public TypeTrip getTypeTrip() {
        return typeTrip;
    }
}
