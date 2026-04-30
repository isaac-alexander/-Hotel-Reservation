package com.alexander.hotel_reservation.config;

import com.alexander.hotel_reservation.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        // public pages
                        .requestMatchers("/login", "/users/create").permitAll()

                        // admin only
                        .requestMatchers("/rooms/new/**").hasRole("ADMIN")
                        .requestMatchers("/rooms/edit/**").hasRole("ADMIN")
                        .requestMatchers("/rooms/delete/**").hasRole("ADMIN")

                        // receptionist + admin
                        .requestMatchers("/bookings/confirm/**").hasAnyRole("ADMIN", "RECEPTIONIST")
                        .requestMatchers("/bookings/reject/**").hasAnyRole("ADMIN", "RECEPTIONIST")
                        .requestMatchers("/bookings/checkin/**").hasRole("RECEPTIONIST")
                        .requestMatchers("/bookings/checkout/**").hasRole("RECEPTIONIST")

                        // everything else
                        .anyRequest().authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}