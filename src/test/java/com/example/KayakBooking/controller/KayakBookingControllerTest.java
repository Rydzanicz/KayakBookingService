package com.example.KayakBooking.controller;

import com.example.KayakBooking.controler.KayakBookingController;
import com.example.KayakBooking.controler.KayakBookingRequest;
import com.example.KayakBooking.model.KayakBooking;
import com.example.KayakBooking.service.BookingService;
import com.example.KayakBooking.service.FailedProcessedPolicyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class KayakBookingControllerTest {

    @Mock
    private BookingService bookingService;
    private FailedProcessedPolicyService failedProcessedPolicyService;

    private KayakBookingController kayakBookingController;
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        kayakBookingController = new KayakBookingController(bookingService, failedProcessedPolicyService);
    }

    @Test
    void testSaveOrderSuccess() {
        // given
        final KayakBookingRequest validRequest = new KayakBookingRequest();
        validRequest.setBuyerName("Test Buyer");
        validRequest.setBuyerAddressEmail("buyer@example.com");
        validRequest.setBuyerPhone("123456789");
        validRequest.setOrderDate("2024-01-01 14:30:00");
        validRequest.setKayakOne(1);
        validRequest.setKayakTwo(1);
        validRequest.setKayakOne_Two(1);
        validRequest.setTraceId("Prawiedniki_Zemborzycki");

        final KayakBooking lastKayakBooking = new KayakBooking(1,
                                                               "Last Buyer",
                                                               "last@example.com",
                                                               "123456789",
                                                               LocalDateTime.now(),
                                                               1,
                                                               1,
                                                               1,
                                                               false,
                                                               "Prawiedniki_Zemborzycki");

        when(bookingService.getLastOrders()).thenReturn(lastKayakBooking);

        // when
        final ResponseEntity<String> response = kayakBookingController.saveOrder(validRequest);

        // then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Order saved successfully", response.getBody());

        verify(bookingService, times(1)).getLastOrders();
    }

    @Test
    void testSaveOrderNullRequest() {
        // given
        // when

        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            kayakBookingController.saveOrder(null);
        });

        // then
        assertEquals("Invalid request payload", exception.getMessage());
    }

    @Test
    void testSaveOrderMissingBuyerName() {
        // given
        final KayakBookingRequest invalidRequest = new KayakBookingRequest();

        // when
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            kayakBookingController.saveOrder(invalidRequest);
        });

        // then
        assertEquals("Invalid request payload", exception.getMessage());
    }

    @Test
    void testSaveOrderInternalServerError() {
        // given
        final KayakBookingRequest validRequest = new KayakBookingRequest();
        validRequest.setBuyerName("Test Buyer");
        validRequest.setBuyerAddressEmail("buyer@example.com");

        when(bookingService.getLastOrders()).thenThrow(new RuntimeException("Database error"));

        // when
        final ResponseEntity<String> response = kayakBookingController.saveOrder(validRequest);

        // then
        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Error saving order", response.getBody());
    }
}
