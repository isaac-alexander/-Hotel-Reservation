package com.alexander.hotel_reservation.service;

import com.alexander.hotel_reservation.dto.CreateUserDto;
import com.alexander.hotel_reservation.entity.User;

import java.util.List;

public interface UserService {

    void register(User user);

    User findByEmail(String email);

    void deleteUser(Long userId);

    List<User> getAllUsers();

    User getUserById(Long id);

    void updateUser(Long id, CreateUserDto dto);

}