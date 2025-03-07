package com.example.KayakBooking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KayakBookingApplication {
    public static void main(String[] args) {
        SpringApplication.run(KayakBookingApplication.class, args);
    }
}
