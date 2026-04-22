package com.alexander.hotel_reservation.service;

import com.alexander.hotel_reservation.entity.User;

public interface UserService {

    void register(User user);

    User login(String email, String password);
}