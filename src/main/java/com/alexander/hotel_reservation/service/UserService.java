package com.alexander.hotel_reservation.service;

import com.alexander.hotel_reservation.dto.UserRequest;
import com.alexander.hotel_reservation.entity.User;

public interface UserService {

    void register(UserRequest request);

    User login(String email, String password);

}