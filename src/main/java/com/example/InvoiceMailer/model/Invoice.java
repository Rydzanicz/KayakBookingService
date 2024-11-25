package com.example.InvoiceMailer.model;

import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Invoice {
    private static final String INVOICE_ID_PATTERN = "^FV/(\\d{1,9})/(\\d{4})$";
    private static final String FORMAT = "FV/%09d/%s";
    private String invoiceId;
    private final String buyerName;
    private final String buyerAddress;
    private final String buyerAddressEmail;
    private final String buyerNIP;
    private final LocalDateTime orderDate;
    private final List<Order> order;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Invoice(final int invoiceNR,
                   final String buyerName,
                   final String buyerAddress,
                   final String buyerAddressEmail,
                   final String buyerNIP,
                   final LocalDateTime orderDate,
                   final List<Order> order) {
        if (buyerName == null || buyerName.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty.");
        }
        if (buyerAddress == null || buyerAddress.isEmpty()) {
            throw new IllegalArgumentException("Address cannot be null or empty.");
        }
        if (buyerAddressEmail == null || buyerAddressEmail.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty.");
        }
        if (orderDate == null) {
            throw new IllegalArgumentException("Order date cannot be null or empty.");
        }
        if (order.isEmpty()) {
            throw new IllegalArgumentException("List of Order cannot be null or empty.");
        }

        this.invoiceId = generateInvoiceId(invoiceNR);
        this.buyerName = buyerName;
        this.buyerAddress = buyerAddress;
        this.buyerAddressEmail = buyerAddressEmail;
        this.buyerNIP = buyerNIP;
        this.orderDate = orderDate;
        this.order = order;
    }

    public Invoice(final int invoiceNR,
                   final String buyerName,
                   final String buyerAddress,
                   final String buyerAddressEmail,
                   final String buyerNIP,
                   final String orderDate,
                   final List<Order> order) {
        if (buyerName == null || buyerName.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty.");
        }
        if (buyerAddress == null || buyerAddress.isEmpty()) {
            throw new IllegalArgumentException("Address cannot be null or empty.");
        }
        if (buyerAddressEmail == null || buyerAddressEmail.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty.");
        }
        if (orderDate == null || orderDate == "") {
            throw new IllegalArgumentException("Order date cannot be null or empty.");
        }
        if (order.isEmpty()) {
            throw new IllegalArgumentException("List of Order cannot be null or empty.");
        }

        this.invoiceId = generateInvoiceId(invoiceNR);
        this.buyerName = buyerName;
        this.buyerAddress = buyerAddress;
        this.buyerAddressEmail = buyerAddressEmail;
        this.buyerNIP = buyerNIP;
        this.orderDate = LocalDateTime.parse(orderDate, formatter);
        this.order = order;
    }

    public Invoice(final InvoiceEntity invoice) {

        validateInvoiceId(invoice.getInvoiceId());
        this.invoiceId = invoice.getInvoiceId();
        this.buyerName = invoice.getName();
        this.buyerAddress = invoice.getAddress();
        this.buyerAddressEmail = invoice.getEmail();
        this.buyerNIP = invoice.getNip();
        this.orderDate = LocalDateTime.parse(invoice.getOrderDate(), formatter);
        this.order = invoice.getOrders()
                            .stream()
                            .map(Order::new)
                            .toList();
    }

    public static String generateInvoiceId(int invoiceNumber) {
        if (invoiceNumber <= 0) {
            throw new IllegalArgumentException("Invoice ID cannot be 0 or less than 0.");
        }
        final String year = String.valueOf(Year.now()
                                               .getValue());
        return String.format(FORMAT, invoiceNumber, year);
    }

    public static boolean validateInvoiceId(String invoiceId) {
        if (invoiceId == null || invoiceId.isEmpty()) {
            throw new IllegalArgumentException("InvoiceEntity cannot be null.");
        }

        if (!Pattern.matches(INVOICE_ID_PATTERN, invoiceId)) {
            throw new IllegalArgumentException("Invalid Invoice ID format. Correct format: FV/{number}/{year}, e.g., FV/001/2024");
        }
        return true;
    }

    private int extractInvoiceNumber() {
        final Pattern pattern = Pattern.compile(INVOICE_ID_PATTERN);
        final Matcher matcher = pattern.matcher(invoiceId);

        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(1));
        } else {
            throw new IllegalArgumentException("Invalid Invoice ID format. Cannot extract invoice number.");
        }
    }

    public int extractAndIncreaseInvoiceNumber() {
        return extractInvoiceNumber() + 1;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(final String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public String getBuyerAddress() {
        return buyerAddress;
    }

    public String getBuyerAddressEmail() {
        return buyerAddressEmail;
    }

    public List<Order> getOrder() {
        return order;
    }

    public String getBuyerNIP() {
        return buyerNIP;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }
}
