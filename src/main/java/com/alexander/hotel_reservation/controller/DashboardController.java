package com.alexander.hotel_reservation.controller;

import com.alexander.hotel_reservation.entity.Room;
import com.alexander.hotel_reservation.entity.User;
import com.alexander.hotel_reservation.service.RoomService;
import com.alexander.hotel_reservation.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {

    private final RoomService roomService;
    private final UserService userService;

    public DashboardController(RoomService roomService, UserService userService) {
        this.roomService = roomService;
        this.userService = userService;
    }

    // dashboard page
    @GetMapping("/dashboard")
    public String dashboard(Model model,
                            Authentication authentication) {

        // get user from authentication
        if (authentication == null) {
            return "redirect:/login";
        }

        String email = authentication.getName();
        User user = userService.findByEmail(email);

        // get rooms
        List<Room> rooms = roomService.getAllRooms();

        // send user
        model.addAttribute("user", user);
        model.addAttribute("rooms", rooms);

        return "dashboard";
    }
}