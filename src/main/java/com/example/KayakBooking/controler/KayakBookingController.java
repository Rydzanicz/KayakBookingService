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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class KayakBookingController {
    private final BookingService bookingService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
    public ResponseEntity<Object> getFutureTrips(@RequestParam(defaultValue = "true") boolean isFuture,
                                                 @RequestParam(required = false, defaultValue = "ASC") String sortDirection,
                                                 @RequestParam(required = false, defaultValue = "0") int page,
                                                 @RequestParam(required = false, defaultValue = "10") int size,
                                                 @RequestParam(required = false) String startDate,
                                                 @RequestParam(required = false) String endDate) {
        try {
            Optional<LocalDate> startLocalDate = Optional.ofNullable(startDate)
                                                         .map(LocalDate::parse);
            Optional<LocalDate> endLocalDate = Optional.ofNullable(endDate)
                                                       .map(LocalDate::parse);

            final List<OrdersEntity> orderDates;
            if (isFuture) {
                orderDates = bookingService.getFutureTrips();
            } else {
                orderDates = bookingService.getTrips();
            }

            final List<OrdersEntity> filteredSortedPaginatedData = applyFiltersSortAndPagination(orderDates,
                                                                                                 sortDirection,
                                                                                                 page,
                                                                                                 size,
                                                                                                 startLocalDate,
                                                                                                 endLocalDate);
            final Map<String, Object> response = new HashMap<>();
            response.put("currentPage", page);
            response.put("pageSize", size);
            response.put("totalItems", orderDates.size());
            response.put("totalPages", (int) Math.ceil((double) orderDates.size() / size));
            response.put("data", filteredSortedPaginatedData);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .build();
        }
    }

    private List<OrdersEntity> applyFiltersSortAndPagination(final List<OrdersEntity> data,
                                                             final String sortDirection,
                                                             final int page,
                                                             final int size,
                                                             final Optional<LocalDate> startDate,
                                                             final Optional<LocalDate> endDate) {
        return data.stream()
                   .filter(order -> applyStartDateFilter(order, startDate))
                   .filter(order -> applyEndDateFilter(order, endDate))
                   .sorted(getComparator(sortDirection))
                   .skip((long) page * size)
                   .limit(size)
                   .toList();
    }

    private boolean applyStartDateFilter(final OrdersEntity order, final Optional<LocalDate> startDate) {
        if (startDate.isEmpty()) {
            return true;
        }
        return !LocalDateTime.parse(order.getOrderDate(), formatter)
                             .isBefore(startDate.get()
                                                .atStartOfDay());

    }

    private boolean applyEndDateFilter(final OrdersEntity order, final Optional<LocalDate> endDate) {
        if (endDate.isEmpty()) {
            return true;
        }
        return !LocalDateTime.parse(order.getOrderDate(), formatter)
                             .isAfter(endDate.get()
                                             .atStartOfDay());
    }

    private Comparator<OrdersEntity> getComparator(final String sortDirection) {
        Comparator<OrdersEntity> comparator;

        comparator = Comparator.comparing(OrdersEntity::getOrderDate);
        if ("DESC".equalsIgnoreCase(sortDirection)) {
            comparator = comparator.reversed();
        }
        return comparator;
    }
}
