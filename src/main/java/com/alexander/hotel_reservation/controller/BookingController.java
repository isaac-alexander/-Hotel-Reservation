package com.alexander.hotel_reservation.controller;

import com.alexander.hotel_reservation.dto.BookingDto;
import com.alexander.hotel_reservation.entity.Booking;
import com.alexander.hotel_reservation.entity.User;
import com.alexander.hotel_reservation.service.BookingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // SHOW BOOKING FORM
    @GetMapping("/new/{roomId}")
    public String bookingForm(@PathVariable Long roomId, Model model, HttpSession session) {

        Optional<User> userOptional =
                Optional.ofNullable((User) session.getAttribute("loggedInUser"));

        // if user not logged in
        if (userOptional.isEmpty()) {
            return "redirect:/login";
        }

        BookingDto dto = new BookingDto();
        dto.setRoomId(roomId);

        model.addAttribute("booking", dto);
        return "booking-form";
    }

    // CREATE BOOKING
    @PostMapping("/new")
    public String createBooking(@ModelAttribute BookingDto dto,
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
            model.addAttribute("error", "Room not available for selected dates");
            return "booking-form";
        }

        model.addAttribute("success", "Booking successful (PENDING approval)");
        return "booking-form";
    }

    // BOOKING HISTORY (ROLE BASED)
    @GetMapping("/history")
    public String bookingHistory(Model model, HttpSession session) {

        Optional<User> userOptional =
                Optional.ofNullable((User) session.getAttribute("loggedInUser"));

        if (userOptional.isEmpty()) {
            return "redirect:/login";
        }

        User user = userOptional.get();

        List<Booking> bookings;

        // ADMIN + RECEPTIONIST see ALL bookings
        if (user.getRole().equals("admin") || user.getRole().equals("receptionist")) {
            bookings = bookingService.getAllBookings();
        } else {
            bookings = bookingService.getBookingsByUser(user.getId());
        }

        model.addAttribute("bookings", bookings);
        model.addAttribute("user", user);

        return "booking-history";
    }

    // RECEPTIONIST confirm booking
    @GetMapping("/confirm/{id}")
    public String confirmBooking(@PathVariable Long id, HttpSession session) {

        Optional<User> userOptional =
                Optional.ofNullable((User) session.getAttribute("loggedInUser"));

        if (userOptional.isEmpty()) {
            return "redirect:/login";
        }

        User user = userOptional.get();

        if (!user.getRole().equals("receptionist") && !user.getRole().equals("admin")) {
            return "redirect:/dashboard";
        }

        bookingService.updateStatus(id, "CONFIRMED");

        return "redirect:/bookings/history";
    }

    // CHECK IN
    @GetMapping("/checkin/{id}")
    public String checkIn(@PathVariable Long id, HttpSession session) {

        Optional<User> userOptional =
                Optional.ofNullable((User) session.getAttribute("loggedInUser"));

        if (userOptional.isEmpty()) {
            return "redirect:/login";
        }

        User user = userOptional.get();

        if (!user.getRole().equals("receptionist")) {
            return "redirect:/dashboard";
        }

        bookingService.checkIn(id);

        return "redirect:/bookings/history";
    }

    // CHECK OUT
    @GetMapping("/checkout/{id}")
    public String checkOut(@PathVariable Long id, HttpSession session) {

        Optional<User> userOptional =
                Optional.ofNullable((User) session.getAttribute("loggedInUser"));

        if (userOptional.isEmpty()) {
            return "redirect:/login";
        }

        User user = userOptional.get();

        if (!user.getRole().equals("receptionist")) {
            return "redirect:/dashboard";
        }

        bookingService.checkOut(id);

        return "redirect:/bookings/history";
    }
}