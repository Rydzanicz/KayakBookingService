package com.example.KayakBooking.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KayakBookingTest {
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @Test
    public void testShouldBePositive() {
        //given
        final String buyerName = "Jan Kowalski";
        final String buyerEmail = "jan.kowalski@example.com";
        final String buyerPhone = "123123123";
        final LocalDateTime ordersDate = LocalDateTime.parse("2025-01-01 14:30:00", formatter);
        final int kayakOne = 1;
        final int kayakTwo = 1;
        final int kayakOne_Two = 1;

        //when
        final KayakBooking kayakBooking = new KayakBooking(1,
                                                           buyerName,
                                                           buyerEmail,
                                                           buyerPhone,
                                                           ordersDate,
                                                           kayakOne,
                                                           kayakTwo,
                                                           kayakOne_Two,
                                                           false);

        //then
        assertNotNull(kayakBooking);
        assertEquals("ORDER/000000001/2025", kayakBooking.getOrderId());
        assertEquals(buyerName, kayakBooking.getBuyerName());
        assertEquals(buyerEmail, kayakBooking.getBuyerAddressEmail());
        assertEquals(buyerPhone, kayakBooking.getBuyerPhone());
        assertEquals(ordersDate, kayakBooking.getOrderDate());
        assertEquals(kayakOne, kayakBooking.getKayakOne());
        assertEquals(kayakTwo, kayakBooking.getKayakTwo());
        assertEquals(kayakOne_Two, kayakBooking.getKayakOne_Two());
    }

    @Test
    public void testShouldThrowExceptionForInvalidOrderNumber() {
        // given
        final int orderNumber = 0;
        final String buyerName = "Jan Kowalski";
        final String buyerEmail = "jan.kowalski@example.com";
        final String buyerPhone = "123123123";
        final LocalDateTime ordersDate = LocalDateTime.parse("2025-01-01 14:30:00", formatter);
        final int kayakOne = 1;
        final int kayakTwo = 1;
        final int kayakOne_Two = 1;

        //when
        //then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            new KayakBooking(orderNumber, buyerName, buyerEmail, buyerPhone, ordersDate, kayakOne, kayakTwo, kayakOne_Two, false);
        });
        assertEquals("Order ID cannot be 0 or less than 0.", thrown.getMessage());
    }

    @Test
    public void testThrowWhenNameIsNull() {
        //given
        final int orderNumber = 0;
        final String buyerName = null;
        final String buyerEmail = "jan.kowalski@example.com";
        final String buyerPhone = "123123123";
        final LocalDateTime ordersDate = LocalDateTime.parse("2025-01-01 14:30:00", formatter);
        final int kayakOne = 1;
        final int kayakTwo = 1;
        final int kayakOne_Two = 1;

        //when
        //then
        assertThrows(IllegalArgumentException.class,
                     () -> new KayakBooking(orderNumber,
                                            buyerName,
                                            buyerEmail,
                                            buyerPhone,
                                            ordersDate,
                                            kayakOne,
                                            kayakTwo,
                                            kayakOne_Two,
                                            false),
                     "Name cannot be null or empty.");
    }



    @Test
    public void testThrowWhenEmailIsNull() {
        //given
        final int orderNumber = 0;
        final String buyerName = "Jan Kowalski";
        final String buyerEmail = null;
        final String buyerPhone = "123123123";
        final LocalDateTime ordersDate = LocalDateTime.parse("2025-01-01 14:30:00", formatter);
        final int kayakOne = 1;
        final int kayakTwo = 1;
        final int kayakOne_Two = 1;

        //when
        //then
        assertThrows(IllegalArgumentException.class,
                     () -> new KayakBooking(orderNumber,
                                            buyerName,
                                            buyerEmail,
                                            buyerPhone,
                                            ordersDate,
                                            kayakOne,
                                            kayakTwo,
                                            kayakOne_Two,
                                            false),
                     "Email cannot be null or empty.");
    }

    @Test
    public void testThrowWhenOrdersDateIsNull() {
        //given
        final int orderNumber = 0;
        final String buyerName = "Jan Kowalski";
        final String buyerEmail = "jan.kowalski@example.com";
        final String buyerPhone = "123123123";
        final LocalDateTime ordersDate = null;
        final int kayakOne = 1;
        final int kayakTwo = 1;
        final int kayakOne_Two = 1;
        //when
        //then
        assertThrows(IllegalArgumentException.class,
                     () -> new KayakBooking(orderNumber,
                                            buyerName,
                                            buyerEmail,
                                            buyerPhone,
                                            ordersDate,
                                            kayakOne,
                                            kayakTwo,
                                            kayakOne_Two,
                                            false),
                     "List of Order cannot be null or empty.");
    }

    @Test
    public void testShouldThrowExceptionForInvalidOrderIdFormat() {
        // given
        final String invalidOrderId = "ORDER/00001/22";

        //when
        //then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            KayakBooking.validateOrder(invalidOrderId);
        });
        assertEquals("Invalid order ID format. Correct format: ORDER/{number}/{year}, e.g., FV/001/2024", thrown.getMessage());
    }

    @Test
    public void testValidateInvalidOrderId() {
        // given
        final String invalidOrderId = "ORDER/1/24";

        //when
        //then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            KayakBooking.validateOrder(invalidOrderId);
        });
        assertEquals("Invalid order ID format. Correct format: ORDER/{number}/{year}, e.g., FV/001/2024", thrown.getMessage());
    }

    @Test
    public void testShouldThrowWhenOrderIdIsNull() {
        // given


        //when
        //then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            KayakBooking.validateOrder(null);
        });
        assertEquals("OrderId cannot be null.", thrown.getMessage());
    }

    @Test
    void validateOrderId_ShouldReturnTrueForValidOrder() {
        // Arrange
        String validOrderId = "ORDER/001/2024";

        // Act
        boolean result = KayakBooking.validateOrder(validOrderId);

        // Assert
        assertTrue(result, "The validation should return true for a valid order ID.");
    }

    @Test
    public void testShouldIncreaseOrderNumber() {
        // given
        final String buyerName = "Jan Kowalski";
        final String buyerEmail = "jan.kowalski@example.com";
        final String buyerPhone = "123123123";
        final LocalDateTime ordersDate = LocalDateTime.parse("2024-01-01 14:30:00", formatter);
        final int kayakOne = 1;
        final int kayakTwo = 1;
        final int kayakOne_Two = 1;
        final KayakBooking kayakBooking = new KayakBooking(900000000,
                                                           buyerName,
                                                           buyerEmail,
                                                           buyerPhone,
                                                           ordersDate,
                                                           kayakOne,
                                                           kayakTwo,
                                                           kayakOne_Two,
                                                           false);

        // when
        int extractedOrderNumber = kayakBooking.extractAndIncreaseOrderNumber();

        // then
        assertEquals(900000001, extractedOrderNumber);
    }
}

