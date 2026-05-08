package com.alexander.hotel_reservation.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaystackConfig {

    @Value("${paystack.secret-key}")
    private String secretKey; // inject secret key from application.yml

    public String getSecretKey() {
        return secretKey; // allows other classes to use the key
    }
}