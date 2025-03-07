package com.example.KayakBooking.service;

import com.example.KayakBooking.model.KayakBooking;
import com.example.KayakBooking.model.OrdersEntity;
import com.example.KayakBooking.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class KayakBookingServiceTest {

    @Mock
    private OrderRepository orderRepository;

    private BookingService bookingService;
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        bookingService = new BookingService(orderRepository);
    }



    @Test
    public void testGetLastOrders() {
        // given
        final String buyerName = "Jan Kowalski";
        final String buyerEmail = "jan.kowalski@example.com";
        final String buyerPhone = "123123123";
        final LocalDateTime ordersDate = LocalDateTime.parse("2025-01-01 14:30:00", formatter);
        final int kayakOne = 1;
        final int kayakTwo = 1;
        final int kayakOne_Two = 1;

        final OrdersEntity lastOrdersEntity = new OrdersEntity(new KayakBooking(5,
                                                                                buyerName,
                                                                                buyerEmail,
                                                                                buyerPhone,
                                                                                ordersDate,
                                                                                kayakOne,
                                                                                kayakTwo,
                                                                                kayakOne_Two,
                                                                                false));
        when(orderRepository.getLastOrders()).thenReturn(lastOrdersEntity);

        // when
        final KayakBooking lastKayakBooking = bookingService.getLastOrders();

        // then
        assertNotNull(lastKayakBooking);
        assertEquals("ORDER/000000005/2025", lastKayakBooking.getOrderId());
        assertEquals(buyerName, lastKayakBooking.getBuyerName());
        assertEquals(buyerEmail, lastKayakBooking.getBuyerAddressEmail());
        assertEquals(buyerPhone, lastKayakBooking.getBuyerPhone());
        assertEquals(ordersDate, lastKayakBooking.getOrderDate());
        assertEquals(kayakOne, lastKayakBooking.getKayakOne());
        assertEquals(kayakTwo, lastKayakBooking.getKayakTwo());
        assertEquals(kayakOne_Two, lastKayakBooking.getKayakOne_Two());
        verify(orderRepository, times(1)).getLastOrders();
    }


    @Test
    public void testSaveInvoice() {
        // given
        final String buyerName = "Jan Kowalski";
        final String buyerEmail = "jan.kowalski@example.com";
        final String buyerPhone = "123123123";
        final LocalDateTime ordersDate = LocalDateTime.parse("2025-01-01 14:30:00", formatter);
        final int kayakOne = 1;
        final int kayakTwo = 1;
        final int kayakOne_Two = 1;
        final KayakBooking kayakBooking = new KayakBooking(1,
                                                           buyerName,
                                                           buyerEmail,
                                                           buyerPhone,
                                                           ordersDate,
                                                           kayakOne,
                                                           kayakTwo,
                                                           kayakOne_Two,
                                                           false);
        final OrdersEntity savedEntity = new OrdersEntity(kayakBooking);
        when(orderRepository.save(any(OrdersEntity.class))).thenReturn(savedEntity);

        // when
        bookingService.saveOrder(kayakBooking);

        // then
        verify(orderRepository, times(1)).save(any(OrdersEntity.class));
    }

    @Test
    public void testSaveInvoiceThrowsExceptionForNullInvoice() {
        // given
        // when
        // then
        assertThrows(IllegalArgumentException.class, () -> bookingService.saveOrder(null));
    }

    @Test
    void testUpdateEmailSendStatusToTrue() {
        // Given
        final String invoiceId = "ORDER/001/2024";
        final boolean status = true;

        // When
        bookingService.updateEmailSendStatus(invoiceId, status);

        // Then
        verify(orderRepository, times(1)).updateEmailSendStatus(invoiceId, status);
    }

    @Test
    void testUpdateEmailSendStatusToFalse() {
        // Given
        final String invoiceId = "ORDER/002/2024";
        final boolean status = false;

        // When
        bookingService.updateEmailSendStatus(invoiceId, status);

        // Then
        verify(orderRepository, times(1)).updateEmailSendStatus(invoiceId, status);
    }

    @Test
    void testUpdateEmailSendStatusForNonExistingInvoiceId() {
        // Given
        final String invoiceId = "ORDER/999/2024"; // Non-existing invoice ID
        final boolean status = true;

        doThrow(new IllegalArgumentException("Invoice not found")).when(orderRepository)
                                                                  .updateEmailSendStatus(invoiceId, status);

        // When / Then
        assertThrows(IllegalArgumentException.class, () -> {
            bookingService.updateEmailSendStatus(invoiceId, status);
        });

        verify(orderRepository, times(1)).updateEmailSendStatus(invoiceId, status);
    }
}