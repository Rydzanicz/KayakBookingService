package com.example.KayakBooking.repository;


import com.example.KayakBooking.model.OrdersEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrdersEntity, Long> {

    OrdersEntity findOrdersByOrderId(String orderId);

    List<OrdersEntity> findOrdersByEmail(String email);

    @Query(value = "SELECT * FROM orders ORDER BY order_id DESC LIMIT 1", nativeQuery = true)
    OrdersEntity getLastOrders();

    @Query(value = "SELECT * FROM orders", nativeQuery = true)
    List<OrdersEntity> findFutureTrips();

    @Modifying
    @Transactional
    @Query("UPDATE OrdersEntity i SET i.isEmailSend = :status WHERE i.orderId = :orderId")
    void updateEmailSendStatus(final String orderId,final boolean status);

    @Query("SELECT i FROM OrdersEntity i WHERE i.isEmailSend = false AND (i.orderId NOT IN :excludedIds OR :excludedIds IS NULL)")
    List<OrdersEntity> findUnsentOrdersExcluding(final @Param("excludedIds") List<String> excludedIds);

    @Query("SELECT i FROM OrdersEntity i WHERE i.isEmailSend = false")
    List<OrdersEntity> findNoSendOrders();
}