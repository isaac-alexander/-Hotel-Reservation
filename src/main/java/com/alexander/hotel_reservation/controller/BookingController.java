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
    public String bookingForm(@PathVariable Long roomId,
                              Model model,
                              HttpSession session) {

        // get logged in user safely
        Optional<User> userOptional =
                Optional.ofNullable((User) session.getAttribute("loggedInUser"));

        // if not logged in redirect
        if (userOptional.isEmpty()) {
            return "redirect:/login";
        }

        User user = userOptional.get();

        // create dto object
        BookingDto dto = new BookingDto();

        // set room id from URL
        dto.setRoomId(roomId);

        // send booking to frontend
        model.addAttribute("booking", dto);

        // also send user (since you refused global advice)
        model.addAttribute("user", user);

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

        // send booking to frontend
        model.addAttribute("booking", dto);

        // send user to frontend
        model.addAttribute("user", user);

        // VALIDATION for CHECK-IN NO PAST DATE
        if (dto.getCheckIn().isBefore(java.time.LocalDate.now())) {

            model.addAttribute("error", "You cannot book past dates");

            return "booking-form";
        }

        // VALIDATION for CHECKOUT AFTER CHECK-IN
        if (dto.getCheckOut().isBefore(dto.getCheckIn())) {

            model.addAttribute("error", "Check-out must be after check-in");

            return "booking-form";
        }

        boolean success = bookingService.createBooking(dto, user);

        if (!success) {

            model.addAttribute("error", "Room not available for selected dates");

            return "booking-form";
        }

        // success message
        model.addAttribute("success", "Booking successful (PENDING approval)");

        return "booking-form";
    }

    // BOOKING HISTORY
    @GetMapping("/history")
    public String bookingHistory(Model model, HttpSession session) {

        Optional<User> userOptional =
                Optional.ofNullable((User) session.getAttribute("loggedInUser"));

        if (userOptional.isEmpty()) {
            return "redirect:/login";
        }

        User user = userOptional.get();

        List<Booking> bookings;

        // ADMIN and RECEPTIONIST sees all bookings
        if (user.getRole().equals("admin") || user.getRole().equals("receptionist")) {

            bookings = bookingService.getAllBookings();

        } else {
            // CUSTOMER - only their bookings
            bookings = bookingService.getBookingsByUser(user.getId());
        }

        model.addAttribute("bookings", bookings);
        model.addAttribute("user", user);

        return "booking-history";
    }

    // CONFIRM BOOKING
    @GetMapping("/confirm/{id}")
    public String confirmBooking(@PathVariable Long id,
                                 HttpSession session) {

        Optional<User> userOptional =
                Optional.ofNullable((User) session.getAttribute("loggedInUser"));

        if (userOptional.isEmpty()) {
            return "redirect:/login";
        }

        User user = userOptional.get();

        // only admin or receptionist
        if (!user.getRole().equals("admin") &&
                !user.getRole().equals("receptionist")) {
            return "redirect:/dashboard";
        }

        // only allow if still pending
        Booking booking = bookingService.getBookingById(id);

        if (booking != null && booking.getStatus().equals("PENDING")) {
            bookingService.updateStatus(id, "CONFIRMED");
        }

        return "redirect:/bookings/history";
    }

    // CUSTOMER CANCEL BOOKING
    @GetMapping("/cancel/{id}")
    public String cancelBooking(@PathVariable Long id,
                                HttpSession session) {

        // get logged in user
        Optional<User> userOptional =
                Optional.ofNullable((User) session.getAttribute("loggedInUser"));

        if (userOptional.isEmpty()) {
            return "redirect:/login";
        }

        User user = userOptional.get();

        // only customer allowed
        if (!user.getRole().equals("customer")) {
            return "redirect:/dashboard";
        }

        // update status
        bookingService.updateStatus(id, "CANCELLED");

        return "redirect:/bookings/history";
    }

    // REJECT BOOKING
    @GetMapping("/reject/{id}")
    public String rejectBooking(@PathVariable Long id,
                                HttpSession session) {

        Optional<User> userOptional =
                Optional.ofNullable((User) session.getAttribute("loggedInUser"));

        if (userOptional.isEmpty()) {
            return "redirect:/login";
        }

        User user = userOptional.get();

        if (!user.getRole().equals("admin") &&
                !user.getRole().equals("receptionist")) {
            return "redirect:/dashboard";
        }

        Booking booking = bookingService.getBookingById(id);

        if (booking != null && booking.getStatus().equals("PENDING")) {
            bookingService.updateStatus(id, "REJECTED");
        }

        return "redirect:/bookings/history";
    }

    // CHECK IN
    @GetMapping("/checkin/{id}")
    public String checkIn(@PathVariable Long id,
                          HttpSession session) {

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
    public String checkOut(@PathVariable Long id,
                           HttpSession session) {

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