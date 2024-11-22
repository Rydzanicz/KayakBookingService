package com.example.InvoiceMailer;

import com.example.InvoiceMailer.service.Invoice;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class InvoiceTest {


    @Test
    public void testShouldBePositive() {
        //given
        //when
        final Invoice invoice = new Invoice(1, "Jan Kowalski", "popowicka 68", "jan.kowalski@example.com");

        //then
        assertNotNull(invoice);
        assertEquals("FV/000000001/2024", invoice.getInvoiceId());
        assertEquals("Jan Kowalski", invoice.getBuyerName());
        assertEquals("popowicka 68", invoice.getBuyerAddress());
        assertEquals("jan.kowalski@example.com", invoice.getBuyerAddressEmail());
    }

    @Test
    public void testShouldThrowExceptionForInvalidInvoiceNumber() {
        // given
        int invoiceNumber = 0;
        String buyerName = "Jan Kowalski";
        String buyerAddress = "popowicka 68";
        String buyerEmail = "jan.kowalski@example.com";

        // when & then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            new Invoice(invoiceNumber, buyerName, buyerAddress, buyerEmail);
        });
        assertEquals("Invoice ID cannot be 0 or less than 0.", thrown.getMessage());
    }

    @Test
    public void testThrowWhenNameIsNull() {
        //given
        //when
        //then
        assertThrows(IllegalArgumentException.class,
                     () -> new Invoice(1, null, "popowicka 68", "jan.kowalski@example.com"),
                     "Name cannot be null or empty.");
    }

    @Test
    public void testThrowWhenEmailIsNull() {
        //given
        //when
        //then
        assertThrows(IllegalArgumentException.class,
                     () -> new Invoice(1, null, "popowicka 68", "jan.kowalski@example.com"),
                     "Email cannot be null or empty.");
    }


    @Test
    public void testThrowWhenInvoiceEntityIsNull() {
        //given
        //when
        //then
        assertThrows(NullPointerException.class, () -> new Invoice(null), "InvoiceEntity cannot be null.");
    }

    @Test
    public void testShouldThrowExceptionForInvalidInvoiceIdFormat() {
        // given
        final String invalidInvoiceId = "FV/00001/22";

        //when
        //then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            Invoice.validateInvoiceId(invalidInvoiceId);
        });
        assertEquals("Invalid Invoice ID format. Correct format: FV/{number}/{year}, e.g., FV/001/2024", thrown.getMessage());
    }

    @Test
    public void testValidateInvalidInvoiceId() {
        // given
        final String invalidInvoiceId = "FV/1/24";

        //when
        //then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            Invoice.validateInvoiceId(invalidInvoiceId);
        });
        assertEquals("Invalid Invoice ID format. Correct format: FV/{number}/{year}, e.g., FV/001/2024", thrown.getMessage());
    }

    @Test
    public void testShouldIncreaseInvoiceNumber() {
        // given
        final Invoice invoice = new Invoice(900000000, "Jan Kowalski", "popowicka 68", "jan.kowalski@example.com");

        // when
        int extractedInvoiceNumber = invoice.extractAndIncreaseInvoiceNumber();

        // then
        assertEquals(900000001, extractedInvoiceNumber);
    }
}

