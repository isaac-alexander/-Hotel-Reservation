package com.alexander.hotel_reservation.controller;

import com.alexander.hotel_reservation.dto.BookingDto;
import com.alexander.hotel_reservation.entity.User;
import com.alexander.hotel_reservation.service.BookingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // show booking form
    @GetMapping("/new/{roomId}")
    public String bookingForm(@PathVariable Long roomId,
                              Model model,
                              HttpSession session) {

        // get user
        Optional<User> userOptional =
                Optional.ofNullable((User) session.getAttribute("loggedInUser"));

        // redirect if not logged in
        if (userOptional.isEmpty()) {
            return "redirect:/login";
        }

        BookingDto dto = new BookingDto();
        dto.setRoomId(roomId);

        model.addAttribute("booking", dto);

        return "booking-form";
    }

    // create booking
    @PostMapping("/new")
    public String createBooking(@ModelAttribute("booking") BookingDto dto,
                                HttpSession session,
                                Model model) {

        Optional<User> userOptional =
                Optional.ofNullable((User) session.getAttribute("loggedInUser"));

        if (userOptional.isEmpty()) {
            return "redirect:/login";
        }

        User user = userOptional.get();

        boolean success = bookingService.createBooking(dto, user);

        if (!success) {
            model.addAttribute("error", "room not available for selected dates");
            return "booking-form";
        }

        return "redirect:/dashboard";
    }
}