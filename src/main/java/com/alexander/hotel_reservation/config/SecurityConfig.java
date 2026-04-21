package com.alexander.hotel_reservation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable()) // disable csrf for now (simplifies forms)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // allow all pages without login
                );

        return http.build();
    }
}