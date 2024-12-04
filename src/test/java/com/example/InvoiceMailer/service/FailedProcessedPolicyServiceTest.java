package com.example.InvoiceMailer.service;

import com.example.InvoiceMailer.model.FailedProcessedPolicyEntity;
import com.example.InvoiceMailer.repository.FailedProcessedPolicyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FailedProcessedPolicyServiceTest {

    private FailedProcessedPolicyService service;
    private FailedProcessedPolicyRepository repository;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(FailedProcessedPolicyRepository.class);
        service = new FailedProcessedPolicyService(repository);
    }

    @Test
    void testFindInvoicesByInvoiceId_Found() {
        // Given
        final String invoiceId = "INV001";
        final FailedProcessedPolicyEntity entity = new FailedProcessedPolicyEntity();
        entity.setInvoiceId(invoiceId);
        when(repository.findInvoicesByInvoiceId(invoiceId)).thenReturn(Optional.of(entity));

        // When
        final Optional<FailedProcessedPolicyEntity> result = service.findInvoicesByInvoiceId(invoiceId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(invoiceId,
                     result.get()
                           .getInvoiceId());
        verify(repository, times(1)).findInvoicesByInvoiceId(invoiceId);
    }

    @Test
    void testFindInvoicesByInvoiceId_NotFound() {
        // Given
        final String invoiceId = "INV002";
        when(repository.findInvoicesByInvoiceId(invoiceId)).thenReturn(Optional.empty());

        // When
        final Optional<FailedProcessedPolicyEntity> result = service.findInvoicesByInvoiceId(invoiceId);

        // Then
        assertFalse(result.isPresent());
        verify(repository, times(1)).findInvoicesByInvoiceId(invoiceId);
    }

    @Test
    void testLogError_NewPolicyEntry() {
        // Given
        final String policyName = "TestPolicy";
        final String message = "Test error message";
        final String invoiceId = "INV003";

        when(repository.findInvoicesByInvoiceId(invoiceId)).thenReturn(Optional.empty());

        // When
        service.logError(policyName, message, invoiceId, Optional.empty());

        // Then
        final ArgumentCaptor<FailedProcessedPolicyEntity> captor = ArgumentCaptor.forClass(FailedProcessedPolicyEntity.class);
        verify(repository, times(1)).save(captor.capture());

        FailedProcessedPolicyEntity captured = captor.getValue();
        assertEquals(policyName, captured.getName());
        assertEquals(message, captured.getMessage());
        assertEquals(invoiceId, captured.getInvoiceId());
        assertEquals(1, captured.getRetryCount());
        assertNotNull(captured.getDate());
    }

    @Test
    void testLogError_UpdateExistingPolicy() {
        // Given
        final String policyName = "TestPolicy";
        final String message = "Updated error message";
        final String invoiceId = "INV001";

        final FailedProcessedPolicyEntity existingEntity = new FailedProcessedPolicyEntity();
        existingEntity.setId(1L);
        existingEntity.setInvoiceId(invoiceId);
        existingEntity.setRetryCount(1);
        existingEntity.setMessage("Previous error");

        when(repository.findInvoicesByInvoiceId(invoiceId)).thenReturn(Optional.of(existingEntity));
        when(repository.updateFailedPolicy(eq(invoiceId), eq(message), any(String.class), eq(2))).thenReturn(1);

        // When
        service.logError(policyName, message, invoiceId, Optional.of(existingEntity));

        // Then
        verify(repository, times(1)).updateFailedPolicy(eq(invoiceId), eq(message), any(String.class), eq(2));
        assertEquals(2, existingEntity.getRetryCount());
        assertEquals(message, existingEntity.getMessage());
    }
}