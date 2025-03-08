package com.example.KayakBooking.service;

import com.example.KayakBooking.model.KayakBooking;
import com.example.KayakBooking.model.OrdersEntity;
import com.example.KayakBooking.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class BookingService {
    private final OrderRepository orderRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public BookingService(final OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public KayakBooking getLastOrders() {
        return new KayakBooking(orderRepository.getLastOrders());
    }

    public List<OrdersEntity> getFutureTrips() {

        return orderRepository.findFutureTrips()
                              .stream()
                              .filter(x -> LocalDateTime.parse(x.getOrderDate(), formatter)
                                                        .isAfter(LocalDateTime.now()))
                              .toList();

    }

    public void updateEmailSendStatus(final String orderId, final boolean status) {
        orderRepository.updateEmailSendStatus(orderId, status);
    }

    public List<KayakBooking> getNoSendOrdersWithExcluding(final List<KayakBooking> processedFailed) {
        final List<OrdersEntity> entities;
        if (processedFailed.isEmpty()) {
            entities = orderRepository.findNoSendOrders();
        } else {
            entities = orderRepository.findUnsentOrdersExcluding(processedFailed.stream()
                                                                                .map(KayakBooking::getOrderId)
                                                                                .toList());
        }
        return entities.stream()
                       .map(this::mapToOrdere)
                       .toList();
    }

    private KayakBooking mapToOrdere(final OrdersEntity entity) {
        return new KayakBooking(Integer.parseInt(entity.getOrderId()
                                                       .split("/")[1]),
                                entity.getName(),
                                entity.getEmail(),
                                entity.getPhone(),
                                entity.getOrderDate(),
                                entity.getKayakOne(),
                                entity.getKayakTwo(),
                                entity.getKayakOne_Two(),
                                entity.isEmailSend());
    }

    public void saveOrder(final KayakBooking kayakBooking) {
        if (kayakBooking == null) {
            throw new IllegalArgumentException("Kayak order cannot be null or empty.");
        }
        final OrdersEntity ordersEntity = new OrdersEntity(kayakBooking);
        orderRepository.save(ordersEntity);
    }
}