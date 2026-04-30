package com.alexander.hotel_reservation.controller;

import com.alexander.hotel_reservation.dto.BookingDto;
import com.alexander.hotel_reservation.entity.Booking;
import com.alexander.hotel_reservation.entity.User;
import com.alexander.hotel_reservation.service.BookingService;
import com.alexander.hotel_reservation.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final UserService userService;

    public BookingController(BookingService bookingService, UserService userService) {
        this.bookingService = bookingService;
        this.userService = userService;
    }

    // SHOW BOOKING FORM
    @GetMapping("/new/{roomId}")
    public String bookingForm(@PathVariable Long roomId,
                              Model model,
                              Authentication authentication) {

        if (authentication == null) {
            return "redirect:/login";
        }

        String email = authentication.getName();
        User user = userService.findByEmail(email);

        BookingDto dto = new BookingDto();
        dto.setRoomId(roomId);

        model.addAttribute("booking", dto);
        model.addAttribute("user", user);

        return "booking-form";
    }

    // CREATE BOOKING
    @PostMapping("/new")
    public String createBooking(@ModelAttribute BookingDto dto,
                                Authentication authentication,
                                Model model) {

        if (authentication == null) {
            return "redirect:/login";
        }

        String email = authentication.getName();
        User user = userService.findByEmail(email);

        model.addAttribute("booking", dto);
        model.addAttribute("user", user);

        if (dto.getCheckIn().isBefore(java.time.LocalDate.now())) {
            model.addAttribute("error", "You cannot book past dates");
            return "booking-form";
        }

        if (dto.getCheckOut().isBefore(dto.getCheckIn())) {
            model.addAttribute("error", "Check-out must be after check-in");
            return "booking-form";
        }

        boolean success = bookingService.createBooking(dto, user);

        if (!success) {
            model.addAttribute("error", "Room not available for selected dates");
            return "booking-form";
        }

        model.addAttribute("success", "Booking successful (PENDING approval)");
        return "booking-form";
    }

    // BOOKING HISTORY
    @GetMapping("/history")
    public String bookingHistory(Model model,
                                 Authentication authentication) {

        if (authentication == null) {
            return "redirect:/login";
        }

        String email = authentication.getName();
        User user = userService.findByEmail(email);

        List<Booking> bookings;

        if (user.getRole().equals("admin") || user.getRole().equals("receptionist")) {
            bookings = bookingService.getAllBookings();
        } else {
            bookings = bookingService.getBookingsByUser(user.getId());
        }

        model.addAttribute("bookings", bookings);
        model.addAttribute("user", user);

        return "booking-history";
    }
}