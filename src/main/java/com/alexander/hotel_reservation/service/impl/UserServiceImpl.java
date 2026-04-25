package com.alexander.hotel_reservation.service.impl;

import com.alexander.hotel_reservation.entity.User;
import com.alexander.hotel_reservation.repository.UserRepository;
import com.alexander.hotel_reservation.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // register user
    @Override
    public void register(User user) {

        // check if email already exists
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());

        if (existingUser.isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        // set default role
        user.setRole("customer");

        userRepository.save(user);
    }

    // login logic using optional
    @Override
    public User login(String email, String password) {

        // find user by email
        Optional<User> foundUser = userRepository.findByEmail(email);

        // check if user exists
        if (foundUser.isPresent()) {

            User user = foundUser.get();

            // compare password
            if (user.getPassword().equals(password)) {
                return user;
            }
        }

        return null;
    }
}