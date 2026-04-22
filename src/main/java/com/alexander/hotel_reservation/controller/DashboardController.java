package com.alexander.hotel_reservation.controller;

import com.alexander.hotel_reservation.entity.Room;
import com.alexander.hotel_reservation.entity.User;
import com.alexander.hotel_reservation.service.RoomService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;

@Controller
public class DashboardController {

    private final RoomService roomService;

    public DashboardController(RoomService roomService) {
        this.roomService = roomService;
    }

    // dashboard page
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {

        // get user from session and wrap in optional
        Optional<User> userOptional =
                Optional.ofNullable((User) session.getAttribute("loggedInUser"));

        // check if empty
        if (userOptional.isEmpty()) {
            return "redirect:/login";
        }

        // extract actual user object
        User user = userOptional.get();

        // get rooms
        List<Room> rooms = roomService.getAllRooms();

        // send user
        model.addAttribute("user", user);
        model.addAttribute("rooms", rooms);

        return "dashboard";
    }
}