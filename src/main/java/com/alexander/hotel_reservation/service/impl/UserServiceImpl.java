package com.alexander.hotel_reservation.service.impl;

import com.alexander.hotel_reservation.dto.UserRequest;
import com.alexander.hotel_reservation.entity.User;
import com.alexander.hotel_reservation.repository.UserRepository;
import com.alexander.hotel_reservation.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional // required for insert
    public void register(UserRequest request) {

        // check if email already exists
        User existingUser = userRepository.findByEmail(request.getEmail());

        if (existingUser != null) {
            throw new RuntimeException("email already exists");
        }

        // insert into database
        userRepository.insertUser(
                request.getName(),
                request.getEmail(),
                request.getPassword(),
                "CUSTOMER"
        );
    }

    @Override
    public User login(String email, String password) {

        return userRepository.login(email, password);
    }

}