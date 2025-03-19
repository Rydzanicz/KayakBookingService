package com.example.KayakBooking.policy;

import com.example.KayakBooking.model.FailedProcessedPolicyEntity;
import com.example.KayakBooking.model.KayakBooking;
import com.example.KayakBooking.model.UsersEntity;
import com.example.KayakBooking.repository.UserRepository;
import com.example.KayakBooking.service.BookingService;
import com.example.KayakBooking.service.EmailService;
import com.example.KayakBooking.service.FailedProcessedPolicyService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class EmailPolicy {
    private final static String POLICY_NAME = "EmailPolicy";
    private final BookingService bookingService;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final FailedProcessedPolicyService failedProcessedPolicyService;
    final List<KayakBooking> processedFailed = new ArrayList<>();
    final List<UsersEntity> processedUsersEntityFailed = new ArrayList<>();

    public EmailPolicy(final BookingService bookingService,
                       final UserRepository userRepository,
                       final EmailService emailService,
                       final FailedProcessedPolicyService failedProcessedPolicyService) {
        this.bookingService = bookingService;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.failedProcessedPolicyService = failedProcessedPolicyService;
    }

    @Scheduled(cron = "${email.policy.cron}")
    public void executeEmailPolicy() {
        final List<FailedProcessedPolicyEntity> failed = failedProcessedPolicyService.findAllFailedPolicies();
        processedFailed.removeIf(kayakBooking -> failed.stream()
                                                       .anyMatch(failedBooking -> failedBooking.getOrderId()
                                                                                               .equals(kayakBooking.getOrderId())));

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
                emailService.sendEmails(kayakBooking);
                bookingService.updateEmailSendStatus(kayakBooking.getOrderId(), true);
                failedProcessedPolicyService.updateReset(kayakBooking.getOrderId(), false);
            } catch (Exception e) {
                final String errorMessage = e.getCause() != null ? e.getCause()
                                                                    .getLocalizedMessage() : e.getMessage();
                failedProcessedPolicyService.logError(POLICY_NAME, errorMessage, kayakBooking.getOrderId(), failedProcessed);
            }
        }
    }

    @Scheduled(cron = "${email.policy.cron}")
    public void executeEmailPasswordPolicy() {
        final List<UsersEntity> unsentUsers = userRepository.findNoSend();
        final List<FailedProcessedPolicyEntity> failed = failedProcessedPolicyService.findAllFailedPolicies();
        processedFailed.removeIf(kayakBooking -> failed.stream()
                                                       .anyMatch(failedBooking -> failedBooking.getOrderId()
                                                                                               .equals(kayakBooking.getOrderId())));
        if (unsentUsers.isEmpty()) {
            return;
        }

        for (UsersEntity usersEntity : unsentUsers) {
            final Optional<FailedProcessedPolicyEntity> processedUsersFailed = failedProcessedPolicyService.findOrdersByOrderId(usersEntity.getUsername());
            if (processedUsersFailed.isPresent() && processedUsersFailed.get()
                                                                        .getRetryCount() > 10) {
                this.processedUsersEntityFailed.add(usersEntity);
                return;
            }
            try {
                final String generatedPassword = new BCryptPasswordEncoder().encode(UUID.randomUUID()
                                                                                        .toString());
                emailService.sendEmailPassword(usersEntity.getUsername(), generatedPassword);

                userRepository.updatePasswordByUsername(usersEntity.getUsername(), generatedPassword);
                userRepository.updateEmailSendStatusByEmail(usersEntity.getUsername(), false);
                failedProcessedPolicyService.updateReset(usersEntity.getUsername(), false);

            } catch (Exception e) {
                final String errorMessage = e.getCause() != null ? e.getCause()
                                                                    .getLocalizedMessage() : e.getMessage();

                failedProcessedPolicyService.logError(POLICY_NAME, errorMessage, usersEntity.getUsername(), processedUsersFailed);
            }
        }
    }
}
