package com.alexander.hotel_reservation.service.impl;

import com.alexander.hotel_reservation.dto.CreateUserDto;
import com.alexander.hotel_reservation.entity.User;
import com.alexander.hotel_reservation.repository.UserRepository;
import com.alexander.hotel_reservation.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // register user
    @Override
    public void register(User user) {

        // check if email already exists
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());

        if (existingUser.isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        // hash password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (user.getRole() == null) {
            user.setRole("customer");
        }

        // save user using SQL query
        userRepository.insertUser(
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                user.getRole()
        );
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public List<User> getAllUsers() {
        // get all users using SQL query
        return userRepository.getAllUsers();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.getUserById(id).orElse(null);
    }

    @Override
    public void updateUser(Long id, CreateUserDto dto) {

        // get user from database
        Optional<User> optionalUser =
                userRepository.getUserById(id);

        if (optionalUser.isPresent()) {

            User user = optionalUser.get();

            // keep old password
            String password = user.getPassword();

            // encode new password if user entered one
            if (dto.getPassword() != null &&
                    !dto.getPassword().isEmpty()) {

                password =
                        passwordEncoder.encode(dto.getPassword());
            }

            // update user using SQL query
            userRepository.updateUser(
                    id,
                    dto.getName(),
                    dto.getEmail(),
                    password,
                    dto.getRole()
            );
        }
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteUser(userId);
    }

}