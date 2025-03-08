package com.example.KayakBooking.controler;

import com.example.KayakBooking.model.KayakBooking;
import com.example.KayakBooking.model.OrdersEntity;
import com.example.KayakBooking.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class KayakBookingController {
    private final BookingService bookingService;

    public KayakBookingController(final BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping(value = "/save-order", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> saveOrder(@RequestBody KayakBookingRequest kayakBookingRequest) {
        if (kayakBookingRequest == null || kayakBookingRequest.getBuyerName() == null) {
            throw new IllegalArgumentException("Invalid request payload");
        }

        try {
             final KayakBooking lastKayakBooking = bookingService.getLastOrders();
            final KayakBooking newKayakBooking = new KayakBooking(lastKayakBooking.extractAndIncreaseOrderNumber(),
                                                                  kayakBookingRequest.getBuyerName(),
                                                                  kayakBookingRequest.getBuyerAddressEmail(),
                                                                  kayakBookingRequest.getBuyerPhone(),
                                                                  kayakBookingRequest.getOrderDate(),
                                                                  kayakBookingRequest.getKayakOne(),
                                                                  kayakBookingRequest.getKayakTwo(),
                                                                  kayakBookingRequest.getKayakOne_Two(),
                                                                  false);

            bookingService.saveOrder(newKayakBooking);
            return ResponseEntity.ok()
                                 .contentType(MediaType.TEXT_PLAIN)
                                 .body("Order saved successfully");


        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error saving order");
        }
    }

    @GetMapping(value = "/get-future-trips", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<OrdersEntity> > getFutureTrips() {
        try {
            final List<OrdersEntity>  orderDates = bookingService.getFutureTrips();
            return ResponseEntity.ok(orderDates);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .build();
        }
    }
}
