package com.example.KayakBooking.service;

import com.example.KayakBooking.model.UsersEntity;
import com.example.KayakBooking.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService {
    private final UserRepository userRepository;

    public LoginService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<UsersEntity> findByUsername(final String username) {
        return userRepository.findByUsername(username);
    }

    public void updateEmailSendStatusByEmail(final String username, final boolean status) {
        userRepository.updateEmailSendStatusByEmail(username, status);
    }
}