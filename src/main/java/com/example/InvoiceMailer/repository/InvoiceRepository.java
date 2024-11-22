package com.example.InvoiceMailer.repository;


import com.example.InvoiceMailer.model.InvoiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<InvoiceEntity, Long> {
}