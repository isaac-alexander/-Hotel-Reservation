package com.alexander.hotel_reservation;

import com.alexander.hotel_reservation.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class HotelReservationApplication {

    public static void main(String[] args) {
        SpringApplication.run(HotelReservationApplication.class, args);
    }

    // create default admin when app starts
    @Bean
    CommandLineRunner run(UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {

        return args -> {

            // check if admin already exists
            if (userRepository.findByEmail("houseoface81@gmail.com").isEmpty()) {

                // encode password
                String encodedPassword = passwordEncoder.encode("12345");

                // insert admin
                userRepository.insertUser(
                        "Admin",
                        "houseoface81@gmail.com",
                        encodedPassword,
                        "admin"
                );

                System.out.println("admin created");
            }
        };
    }
}