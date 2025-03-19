package com.example.KayakBooking.service;

import com.example.KayakBooking.model.FailedProcessedPolicyEntity;
import com.example.KayakBooking.repository.FailedProcessedPolicyRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FailedProcessedPolicyService {

    private final FailedProcessedPolicyRepository repository;

    public FailedProcessedPolicyService(final FailedProcessedPolicyRepository repository) {
        this.repository = repository;
    }

    public Optional<FailedProcessedPolicyEntity> findOrdersByOrderId(final String orderId) {
        return repository.findOrdersByOrderId(orderId);
    }

    public List<FailedProcessedPolicyEntity> findAllFailedPolicies() {
        return repository.findAllFailedPolicies();
    }

    public void logError(final String policyName,
                         final String message,
                         final String orderId,
                         final Optional<FailedProcessedPolicyEntity> failedProcessedPolicyEntity) {
        if (failedProcessedPolicyEntity.isPresent()) {
            final FailedProcessedPolicyEntity failedProcessedPolicy = failedProcessedPolicyEntity.get();
            failedProcessedPolicy.increaseRetryCount();
            failedProcessedPolicy.setDate(LocalDateTime.now());
            failedProcessedPolicy.setMessage(message);

            updateFailedPolicy(failedProcessedPolicy);
            return;
        }

        final FailedProcessedPolicyEntity failed = new FailedProcessedPolicyEntity();
        failed.setOrderId(orderId);
        failed.setName(policyName);
        failed.setMessage(message);
        failed.setRetryCount(1);
        failed.setDate(LocalDateTime.now());
        repository.save(failed);
    }

    @Transactional
    private void updateFailedPolicy(final FailedProcessedPolicyEntity failedProcessedPolicyEntity) {
        repository.updateFailedPolicy(failedProcessedPolicyEntity.getOrderId(),
                                      failedProcessedPolicyEntity.getMessage(),
                                      failedProcessedPolicyEntity.getDate(),
                                      failedProcessedPolicyEntity.getRetryCount());
    }

    @Transactional
    public void updateRetryCount(final String orderId) {
        repository.updateRetryCount(orderId);
    }

    @Transactional
    public void updateReset(final String orderId, final boolean status) {
        repository.updateReset(orderId, status);
    }
}
