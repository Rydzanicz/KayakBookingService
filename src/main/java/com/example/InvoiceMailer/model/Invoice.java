package com.example.InvoiceMailer.model;

import java.time.Year;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Invoice {
    private static final String INVOICE_ID_PATTERN = "^FV/(\\d{1,9})/(\\d{4})$";
    private static final String FORMAT = "FV/%09d/%s";
    private String invoiceId;
    private final String buyerName;
    private final String buyerAddress;
    private final String buyerAddressEmail;

    public Invoice(final int invoiceNR, final String buyerName, final String buyerAddress, final String buyerAddressEmail) {
        if (buyerName == null || buyerName.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty.");
        }
        if (buyerAddress == null || buyerAddress.isEmpty()) {
            throw new IllegalArgumentException("Address cannot be null or empty.");
        }
        if (buyerAddressEmail == null || buyerAddressEmail.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty.");
        }

        this.invoiceId = generateInvoiceId(invoiceNR);
        this.buyerName = buyerName;
        this.buyerAddress = buyerAddress;
        this.buyerAddressEmail = buyerAddressEmail;
    }

    public Invoice(final InvoiceEntity invoice) {

        validateInvoiceId(invoice.getInvoiceId());
        this.invoiceId = invoice.getInvoiceId();
        this.buyerName = invoice.getName();
        this.buyerAddress = invoice.getAddress();
        this.buyerAddressEmail = invoice.getEmail();
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
}
