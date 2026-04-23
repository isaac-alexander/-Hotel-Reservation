package com.alexander.hotel_reservation.controller;

import com.alexander.hotel_reservation.entity.Booking;
import com.alexander.hotel_reservation.entity.User;
import com.alexander.hotel_reservation.service.BookingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;

@Controller
public class BookingHistoryController {

    private final BookingService bookingService;

    public BookingHistoryController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // booking history page
    @GetMapping("/bookings")
    public String bookingHistory(Model model, HttpSession session) {

        // get user
        Optional<User> userOptional =
                Optional.ofNullable((User) session.getAttribute("loggedInUser"));

        if (userOptional.isEmpty()) {
            return "redirect:/login";
        }

        User user = userOptional.get();

        List<Booking> bookingList;

        // admin sees all bookings
        if (user.getRole().equals("admin")) {
            bookingList = bookingService.getAllBookings();
        } else {
            // customer sees only their bookings
            bookingList = bookingService.getBookingsByUser(user.getId());
        }

        model.addAttribute("bookings", bookingList);
        model.addAttribute("user", user);

        return "booking-history";
    }
}