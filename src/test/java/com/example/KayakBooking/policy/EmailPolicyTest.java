package com.example.KayakBooking.policy;

import com.example.KayakBooking.model.FailedProcessedPolicyEntity;
import com.example.KayakBooking.model.KayakBooking;
import com.example.KayakBooking.model.TypeTrip;
import com.example.KayakBooking.model.UsersEntity;
import com.example.KayakBooking.repository.UserRepository;
import com.example.KayakBooking.service.BookingService;
import com.example.KayakBooking.service.EmailService;
import com.example.KayakBooking.service.FailedProcessedPolicyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.*;

class EmailPolicyTest {

    private BookingService bookingService;
    private EmailService emailService;
    private FailedProcessedPolicyService failedProcessedPolicyService;
    private EmailPolicy emailPolicy;
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        bookingService = mock(BookingService.class);
        userRepository = mock(UserRepository.class);
        emailService = mock(EmailService.class);
        failedProcessedPolicyService = mock(FailedProcessedPolicyService.class);

        emailPolicy = new EmailPolicy(bookingService, userRepository, emailService, failedProcessedPolicyService);
    }

    @Test
    void shouldSendEmailForUnsentOrders() {
        // Given
        final String buyerName = "Jan Kowalski";
        final String buyerEmail = "jan.kowalski@example.com";
        final String buyerPhone = "123123123";
        final LocalDateTime ordersDate = LocalDateTime.parse("2025-01-01 14:30:00", formatter);
        final int kayakOne = 1;
        final int kayakTwo = 1;
        final int kayakOne_Two = 1;
        final TypeTrip traceID = TypeTrip.Prawiedniki_Zemborzycki;

        final KayakBooking kayakBooking = new KayakBooking(1,
                                                           buyerName,
                                                           buyerEmail,
                                                           buyerPhone,
                                                           ordersDate,
                                                           kayakOne,
                                                           kayakTwo,
                                                           kayakOne_Two,
                                                           false,
                                                           traceID);
        final List<KayakBooking> unsentKayakBookings = List.of(kayakBooking);

        when(bookingService.getNoSendOrdersWithExcluding(anyList())).thenReturn(unsentKayakBookings);
        when(failedProcessedPolicyService.findOrdersByOrderId(kayakBooking.getOrderId())).thenReturn(Optional.empty());
        doNothing().when(emailService)
                   .sendEmails(any());
        doNothing().when(bookingService)
                   .updateEmailSendStatus(anyString(), eq(true));

        // When
        emailPolicy.executeEmailPolicy();

        // Then
        verify(emailService, times(1)).sendEmails(eq(kayakBooking));
        verify(bookingService, times(1)).updateEmailSendStatus(eq(kayakBooking.getOrderId()), eq(true));
    }

    @Test
    void shouldLogErrorWhenEmailSendingFails() {
        // Given
        final String buyerName = "Jan Kowalski";
        final String buyerEmail = "jan.kowalski@example.com";
        final String buyerPhone = "123123123";
        final LocalDateTime ordersDate = LocalDateTime.parse("2025-01-01 14:30:00", formatter);
        final int kayakOne = 1;
        final int kayakTwo = 1;
        final int kayakOne_Two = 1;
        final TypeTrip traceID = TypeTrip.Prawiedniki_Zemborzycki;

        final KayakBooking kayakBooking = new KayakBooking(1,
                                                           buyerName,
                                                           buyerEmail,
                                                           buyerPhone,
                                                           ordersDate,
                                                           kayakOne,
                                                           kayakTwo,
                                                           kayakOne_Two,
                                                           false,
                                                           traceID);
        final List<KayakBooking> unsentKayakBookings = List.of(kayakBooking);

        when(bookingService.getNoSendOrdersWithExcluding(anyList())).thenReturn(unsentKayakBookings);
        when(failedProcessedPolicyService.findOrdersByOrderId(anyString())).thenReturn(Optional.empty());
        doThrow(new RuntimeException("Email service failed")).when(emailService)
                                                             .sendEmails(any());

        // when
        emailPolicy.executeEmailPolicy();

        // then
        verify(failedProcessedPolicyService, times(1)).logError(eq("EmailPolicy"),
                                                                eq("Email service failed"),
                                                                eq(kayakBooking.getOrderId()),
                                                                any());
        verify(bookingService, never()).updateEmailSendStatus(anyString(), eq(true));
    }

    @Test
    void shouldSkipOrdersWithMaxRetryCount() {
        // Given
        final String buyerName = "Jan Kowalski";
        final String buyerEmail = "jan.kowalski@example.com";
        final String buyerPhone = "123123123";
        final LocalDateTime ordersDate = LocalDateTime.parse("2025-01-01 14:30:00", formatter);
        final int kayakOne = 1;
        final int kayakTwo = 1;
        final int kayakOne_Two = 1;
        final TypeTrip traceID = TypeTrip.Prawiedniki_Zemborzycki;

        final KayakBooking kayakBooking = new KayakBooking(1,
                                                           buyerName,
                                                           buyerEmail,
                                                           buyerPhone,
                                                           ordersDate,
                                                           kayakOne,
                                                           kayakTwo,
                                                           kayakOne_Two,
                                                           false,
                                                           traceID);
        final List<KayakBooking> unsentKayakBookings = List.of(kayakBooking);

        final FailedProcessedPolicyEntity failedPolicy = new FailedProcessedPolicyEntity();
        failedPolicy.setRetryCount(11);

        when(bookingService.getNoSendOrdersWithExcluding(anyList())).thenReturn(unsentKayakBookings);
        when(failedProcessedPolicyService.findOrdersByOrderId(kayakBooking.getOrderId())).thenReturn(Optional.of(failedPolicy));

        // When
        emailPolicy.executeEmailPolicy();

        // Then
        verify(emailService, never()).sendEmails(any());
        verify(failedProcessedPolicyService, never()).logError(anyString(), anyString(), anyString(), any());
    }

    @Test
    void shouldNotProcessWhenNoUnsentOrders() {
        // Given
        when(bookingService.getNoSendOrdersWithExcluding(anyList())).thenReturn(new ArrayList<>());

        // When
        emailPolicy.executeEmailPolicy();

        // Then
        verify(emailService, never()).sendEmails(any());
        verify(failedProcessedPolicyService, never()).logError(anyString(), anyString(), anyString(), any());
    }

    @Test
    void shouldSendEmailForUnsentPasswordReset() {
        // Given
        final Long id = 0L;
        final String username = "username";
        final String password = "password";
        final Set<String> roles = Set.of("roles");
        final boolean reset = false;
        final String generatedPassword = new BCryptPasswordEncoder().encode(UUID.randomUUID()
                                                                                .toString());
        final UsersEntity users = new UsersEntity(id, username, password, roles, reset);
        final List<UsersEntity> unsentUsers = List.of(users);

        when(userRepository.findNoSend()).thenReturn(unsentUsers);
        when(failedProcessedPolicyService.findOrdersByOrderId(users.getUsername())).thenReturn(Optional.empty());
        doNothing().when(emailService)
                   .sendEmailPassword(anyString(), anyString());
        doNothing().when(userRepository)
                   .updateEmailSendStatusByEmail(anyString(), eq(false));

        // When
        emailPolicy.executeEmailPasswordPolicy();

        // Then
        verify(emailService, times(1)).sendEmailPassword(eq(users.getUsername()), anyString());
        verify(userRepository, times(1)).updateEmailSendStatusByEmail(eq(users.getUsername()), eq(false));
    }

    @Test
    void shouldLogErrorWhenEmailSendingPasswordResetFails() {
        // Given
        final Long id = 0L;
        final String username = "username";
        final String password = "password";
        final Set<String> roles = Set.of("roles");
        final boolean reset = false;

        final UsersEntity users = new UsersEntity(id, username, password, roles, reset);
        final List<UsersEntity> unsentUsers = List.of(users);
        final String generatedPassword = new BCryptPasswordEncoder().encode(UUID.randomUUID()
                                                                                .toString());
        when(userRepository.findNoSend()).thenReturn(unsentUsers);
        when(failedProcessedPolicyService.findOrdersByOrderId(anyString())).thenReturn(Optional.empty());
        doThrow(new RuntimeException("Email service failed")).when(emailService)
                                                             .sendEmailPassword(anyString(), anyString());

        // when
        emailPolicy.executeEmailPasswordPolicy();

        // then
        verify(failedProcessedPolicyService, times(1)).logError(eq("EmailPolicy"),
                                                                eq("Email service failed"),
                                                                eq(users.getUsername()),
                                                                any());
        verify(bookingService, never()).updateEmailSendStatus(anyString(), eq(false));
    }

    @Test
    void shouldSkipPasswordResetWithMaxRetryCount() {
        // Given
        final Long id = 0L;
        final String username = "username";
        final String password = "password";
        final Set<String> roles = Set.of("roles");
        final boolean reset = true;

        final UsersEntity users = new UsersEntity(id, username, password, roles, reset);
        final List<UsersEntity> unsentUsers = List.of(users);

        final FailedProcessedPolicyEntity failedPolicy = new FailedProcessedPolicyEntity();
        failedPolicy.setRetryCount(11);

        when(userRepository.findNoSend()).thenReturn(unsentUsers);
        when(failedProcessedPolicyService.findOrdersByOrderId(users.getUsername())).thenReturn(Optional.of(failedPolicy));

        // When
        emailPolicy.executeEmailPasswordPolicy();

        // Then
        verify(emailService, never()).sendEmails(any());
        verify(failedProcessedPolicyService, never()).logError(anyString(), anyString(), anyString(), any());
    }

    @Test
    void shouldNotProcessWhenNoPasswordReset() {
        // Given
        when(bookingService.getNoSendOrdersWithExcluding(anyList())).thenReturn(new ArrayList<>());

        // When
        emailPolicy.executeEmailPasswordPolicy();

        // Then
        verify(emailService, never()).sendEmails(any());
        verify(failedProcessedPolicyService, never()).logError(anyString(), anyString(), anyString(), any());
    }
}
