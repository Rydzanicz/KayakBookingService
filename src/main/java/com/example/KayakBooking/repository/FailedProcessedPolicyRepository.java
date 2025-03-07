package com.example.KayakBooking.repository;

import com.example.KayakBooking.model.FailedProcessedPolicyEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FailedProcessedPolicyRepository extends JpaRepository<FailedProcessedPolicyEntity, Long> {

    @Query(value = "SELECT * FROM failed_processed_policy WHERE order_id = :orderId LIMIT 1", nativeQuery = true)
    Optional<FailedProcessedPolicyEntity> findOrdersByOrderId(@Param("orderId") String orderId);

    @Query(value = "SELECT * FROM failed_processed_policy WHERE id = :id LIMIT 1", nativeQuery = true)
    Optional<FailedProcessedPolicyEntity> findOrdersById(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("UPDATE FailedProcessedPolicyEntity f SET f.retryCount = :retryCount, f.message = :message, f.date = :date WHERE f.orderId = :orderId")
    int updateFailedPolicy(@Param("orderId") String orderId,
                           @Param("message") String message,
                           @Param("date") String date,
                           @Param("retryCount") int retryCount);

}

