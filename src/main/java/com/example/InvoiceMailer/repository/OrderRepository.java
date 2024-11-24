package com.example.InvoiceMailer.repository;

import com.example.InvoiceMailer.model.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findByInvoice_InvoiceId(String invoiceId);
}