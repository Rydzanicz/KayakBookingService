package com.example.InvoiceMailer.policy;

import com.example.InvoiceMailer.model.FailedProcessedPolicyEntity;
import com.example.InvoiceMailer.model.Invoice;
import com.example.InvoiceMailer.service.EmailService;
import com.example.InvoiceMailer.service.FailedProcessedPolicyService;
import com.example.InvoiceMailer.service.InvoiceService;
import com.example.InvoiceMailer.service.PdfGeneratorService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class EmailPolicy {
    private final static String POLICY_NAME = "EmailPolicy";
    private final InvoiceService invoiceService;
    private final EmailService emailService;
    private final FailedProcessedPolicyService failedProcessedPolicyService;

    public EmailPolicy(final InvoiceService invoiceService,
                       final EmailService emailService,
                       final FailedProcessedPolicyService failedProcessedPolicyService) {
        this.invoiceService = invoiceService;
        this.emailService = emailService;
        this.failedProcessedPolicyService = failedProcessedPolicyService;
    }

    @Scheduled(cron = "${email.policy.cron}")
    public void executeEmailPolicy() {
        final List<Invoice> unsentInvoices = invoiceService.getNoSendInvoices();
        if (unsentInvoices.isEmpty()) {
            return;
        }

        for (Invoice invoice : unsentInvoices) {
            final Optional<FailedProcessedPolicyEntity> failedProcessed = failedProcessedPolicyService.findInvoicesByInvoiceId(invoice.getInvoiceId());
            if (failedProcessed.isPresent() && failedProcessed.get()
                                                              .getRetryCount() > 10) {
                return;
            }

            try {
                byte[] pdfAttachment = generateInvoicePdf(invoice);
                emailService.sendEmails(invoice.getBuyerAddressEmail(), pdfAttachment, "Faktura-" + invoice.getInvoiceId() + ".pdf");
                invoiceService.updateEmailSendStatus(invoice.getInvoiceId(), true);
            } catch (Exception e) {
                failedProcessedPolicyService.logError(POLICY_NAME,
                                                      e.getCause()
                                                       .getLocalizedMessage(),
                                                      invoice.getInvoiceId(),
                                                      failedProcessed);

                throw new IllegalStateException("Failed to send email.", e);
            }
        }
    }

    private byte[] generateInvoicePdf(Invoice invoice) throws IOException {
        PdfGeneratorService pdfGeneratorService = new PdfGeneratorService();
        return pdfGeneratorService.generateInvoicePdf(invoice)
                                  .toByteArray();
    }

}
