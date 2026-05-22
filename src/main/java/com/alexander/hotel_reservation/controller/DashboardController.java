package com.alexander.hotel_reservation.controller;

import com.alexander.hotel_reservation.entity.Room;
import com.alexander.hotel_reservation.entity.User;
import com.alexander.hotel_reservation.service.RoomService;
import com.alexander.hotel_reservation.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;

@Controller
public class DashboardController {

    @Autowired
    RoomService roomService;

    @Autowired
    UserService userService;

    // dashboard page
    @GetMapping("/dashboard")
    public String dashboard(Model model,
                            Authentication authentication) {

        // get user from authentication
        if (authentication == null) {
            return "redirect:/login";
        }

        String email = authentication.getName();
         Optional<User> user = userService.findByEmail(email);

        // get rooms
        List<Room> rooms = roomService.getAllRooms();

        // send user
        model.addAttribute("user", user);
        model.addAttribute("rooms", rooms);

        return "dashboard";
    }
}