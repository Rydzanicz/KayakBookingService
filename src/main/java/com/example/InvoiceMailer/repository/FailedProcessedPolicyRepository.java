package com.example.InvoiceMailer.repository;

import com.example.InvoiceMailer.model.FailedProcessedPolicyEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FailedProcessedPolicyRepository extends JpaRepository<FailedProcessedPolicyEntity, Long> {

    @Query(value = "SELECT * FROM failedProcessedPolicy WHERE invoice_id = :invoiceId LIMIT 1", nativeQuery = true)
    Optional<FailedProcessedPolicyEntity> findInvoicesByInvoiceId(@Param("invoiceId") String invoiceId);

    @Query(value = "SELECT * FROM failedProcessedPolicy WHERE id = :id LIMIT 1", nativeQuery = true)
    Optional<FailedProcessedPolicyEntity> findInvoicesById(@Param("id") Long id);


    @Modifying
    @Transactional
    @Query("UPDATE FailedProcessedPolicyEntity f SET f.retryCount = 9 WHERE f.invoiceId = :invoiceId AND f.id = :id")
    int updateFailedPolicy(@Param("invoiceId") String invoiceId, @Param("id") Long id);

}

