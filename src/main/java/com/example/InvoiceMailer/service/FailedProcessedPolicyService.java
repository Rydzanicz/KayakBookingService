package com.example.InvoiceMailer.service;

import com.example.InvoiceMailer.model.FailedProcessedPolicyEntity;
import com.example.InvoiceMailer.repository.FailedProcessedPolicyRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class FailedProcessedPolicyService {

    private final FailedProcessedPolicyRepository repository;

    public FailedProcessedPolicyService(FailedProcessedPolicyRepository repository) {
        this.repository = repository;
    }

    public Optional<FailedProcessedPolicyEntity> findInvoicesByInvoiceId(final String invoiceId) {
        return repository.findInvoicesByInvoiceId(invoiceId);
    }

    public void logError(final String policyName,
                         final String message,
                         final String invoiceId,
                         final Optional<FailedProcessedPolicyEntity> failedProcessedPolicyEntity) {
        if (failedProcessedPolicyEntity.isPresent()) {
            final FailedProcessedPolicyEntity failedProcessedPolicy = failedProcessedPolicyEntity.get();
            failedProcessedPolicy.increaseRetryCount();
            failedProcessedPolicy.setDate(LocalDateTime.now());
            failedProcessedPolicy.setMessage(message);

            updateRetryCount(failedProcessedPolicy);
            Optional<FailedProcessedPolicyEntity> policies = repository.findInvoicesById(failedProcessedPolicy.getId());
            return;
        }

        final FailedProcessedPolicyEntity failed = new FailedProcessedPolicyEntity();
        failed.setInvoiceId(invoiceId);
        failed.setName(policyName);
        failed.setMessage(message);
        failed.setRetryCount(1);
        failed.setDate(LocalDateTime.now());
        repository.save(failed);
    }

    @Transactional
    private int updateRetryCount(FailedProcessedPolicyEntity failedProcessedPolicyEntity) {
        return repository.updateFailedPolicy(failedProcessedPolicyEntity.getInvoiceId(), failedProcessedPolicyEntity.getId());
    }

}
