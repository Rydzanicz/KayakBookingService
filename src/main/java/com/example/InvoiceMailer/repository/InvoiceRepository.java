package com.example.InvoiceMailer.repository;


import com.example.InvoiceMailer.model.InvoiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<InvoiceEntity, Long> {
    InvoiceEntity findByInvoiceId(String invoiceId);

    List<InvoiceEntity> findInvoicesByInvoiceId(String invoiceId);

    List<InvoiceEntity> findInvoicesByEmail(String email);

    @Query(value = "SELECT * FROM invoices ORDER BY invoice_id DESC LIMIT 1", nativeQuery = true)
    InvoiceEntity getLastInvoices();

    @Query(value = "SELECT DISTINCT email FROM invoices", nativeQuery = true)
    List<String> findUniqueEmails();
}