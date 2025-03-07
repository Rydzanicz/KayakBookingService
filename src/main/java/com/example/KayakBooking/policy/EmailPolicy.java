package com.example.KayakBooking.policy;

import com.example.KayakBooking.model.FailedProcessedPolicyEntity;
import com.example.KayakBooking.model.KayakBooking;
import com.example.KayakBooking.service.BookingService;
import com.example.KayakBooking.service.EmailService;
import com.example.KayakBooking.service.FailedProcessedPolicyService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class EmailPolicy {
    private final static String POLICY_NAME = "EmailPolicy";
    private final BookingService bookingService;
    private final EmailService emailService;
    private final FailedProcessedPolicyService failedProcessedPolicyService;
    final List<KayakBooking> processedFailed = new ArrayList<>();

    public EmailPolicy(final BookingService bookingService,
                       final EmailService emailService,
                       final FailedProcessedPolicyService failedProcessedPolicyService) {
        this.bookingService = bookingService;
        this.emailService = emailService;
        this.failedProcessedPolicyService = failedProcessedPolicyService;
    }

    @Scheduled(cron = "${email.policy.cron}")
    public void executeEmailPolicy() {
        final List<KayakBooking> unsentKayakBookings = bookingService.getNoSendOrdersWithExcluding(processedFailed);
        if (unsentKayakBookings.isEmpty()) {
            return;
        }

        for (KayakBooking kayakBooking : unsentKayakBookings) {
            final Optional<FailedProcessedPolicyEntity> failedProcessed = failedProcessedPolicyService.findOrdersByOrderId(kayakBooking.getOrderId());
            if (failedProcessed.isPresent() && failedProcessed.get()
                                                              .getRetryCount() > 10) {
                processedFailed.add(kayakBooking);
                return;
            }

            try {
                emailService.sendEmails(kayakBooking.getBuyerAddressEmail());
                bookingService.updateEmailSendStatus(kayakBooking.getOrderId(), true);
            } catch (Exception e) {
              final   String errorMessage = e.getCause() != null ? e.getCause().getLocalizedMessage() : e.getMessage();
                failedProcessedPolicyService.logError(POLICY_NAME,
                                                      errorMessage,
                                                      kayakBooking.getOrderId(),
                                                      failedProcessed);
            }
        }
    }
}
