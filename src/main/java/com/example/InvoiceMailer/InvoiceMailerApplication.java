package com.example.InvoiceMailer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class InvoiceMailerApplication {
    public static void main(String[] args) {
        SpringApplication.run(InvoiceMailerApplication.class, args);
    }
}
