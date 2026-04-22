package com.alexander.hotel_reservation;

import com.alexander.hotel_reservation.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class HotelReservationApplication {

    public static void main(String[] args) {
        SpringApplication.run(HotelReservationApplication.class, args);
    }

    // create default admin when app starts
    @Bean
    CommandLineRunner run(UserRepository userRepository) {
        return args -> {

            // check if admin already exists
            if (userRepository.findByEmail("admin@gmail.com").isEmpty()) {

                // insert admin user
                userRepository.insertUser(
                        "Admin",
                        "admin@gmail.com",
                        "1234",
                        "admin"
                );

                System.out.println("admin created");
            }
        };
    }
}