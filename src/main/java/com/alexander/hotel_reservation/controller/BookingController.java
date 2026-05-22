package com.alexander.hotel_reservation.controller;

import com.alexander.hotel_reservation.dto.BookingDto;
import com.alexander.hotel_reservation.entity.Booking;
import com.alexander.hotel_reservation.entity.User;
import com.alexander.hotel_reservation.service.BookingService;
import com.alexander.hotel_reservation.service.PaymentService;
import com.alexander.hotel_reservation.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    BookingService bookingService;

    @Autowired
    UserService userService;

    @Autowired
    PaymentService paymentService;

    // SHOW BOOKING FORM
    @GetMapping("/new/{roomId}")
    public String bookingForm(@PathVariable Long roomId,
                              Model model,
                              Authentication authentication) {

        if (authentication == null) {
            return "redirect:/login";
        }

        String email = authentication.getName();

        Optional<User> optionalUser =
                userService.findByEmail(email);

        // check if user exists
        if (optionalUser.isEmpty()) {
            return "redirect:/login";
        }

        // get actual user object
        User user = optionalUser.get();

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

        // get logged in user
        String email = authentication.getName();

        Optional<User> optionalUser =
                userService.findByEmail(email);

        // check if user exists
        if (optionalUser.isEmpty()) {
            return "redirect:/login";
        }

        // get actual user object
        User user = optionalUser.get();

        // send user back to view
        model.addAttribute("booking", dto);
        model.addAttribute("user", user);

        // validate dates
        if (dto.getCheckIn().isBefore(java.time.LocalDate.now())) {
            model.addAttribute("error", "You cannot book past dates");
            return "booking-form";
        }

        if (dto.getCheckOut().isBefore(dto.getCheckIn())) {
            model.addAttribute("error", "Check-out must be after check-in");
            return "booking-form";
        }

        // create booking
        boolean success = bookingService.createBooking(dto, user);

        if (!success) {
            model.addAttribute("error", "Room not available for selected dates");
            return "booking-form";
        }

        // get created booking (simple approach: latest user booking)
        List<Booking> bookings = bookingService.getBookingsByUser(user.getId());
        Booking latestBooking = bookings.stream()

                // get last booking
                // keeps replacing first with second until last item remains.
                .reduce((first, second) -> second)

                // return null if empty
                .orElse(null);

        // initialize payment using paystack
        String paymentUrl = paymentService.initializePayment(
                user.getEmail(),
                latestBooking.getTotalPrice(),
                latestBooking.getPaymentReference()
        );

        // send data to html
        model.addAttribute("success", "booking successful. proceed to payment");
        model.addAttribute("paymentUrl", paymentUrl);
        model.addAttribute("bookingCode", latestBooking.getBookingCode());

        return "booking-form";
    }

    @PostMapping("/confirm/{id}")
    public String confirmBooking(@PathVariable Long id) {

        bookingService.updateStatus(id, "CONFIRMED");

        return "redirect:/bookings/history";
    }

    @PostMapping("/reject/{id}")
    public String rejectBooking(@PathVariable Long id) {

        bookingService.updateStatus(id, "REJECTED");

        return "redirect:/bookings/history";
    }

    @PostMapping("/cancel/{id}")
    public String cancelBooking(@PathVariable Long id) {

        bookingService.updateStatus(id, "CANCELLED");

        return "redirect:/bookings/history";
    }

    @PostMapping("/checkin/{id}")
    public String checkIn(@PathVariable Long id) {

        bookingService.checkIn(id);

        return "redirect:/bookings/history";
    }

    @PostMapping("/checkout/{id}")
    public String checkOut(@PathVariable Long id) {

        bookingService.checkOut(id);

        return "redirect:/bookings/history";
    }


    // BOOKING HISTORY
    @GetMapping("/history")
    public String bookingHistory(Model model,
                                 Authentication authentication) {

        if (authentication == null) {
            return "redirect:/login";
        }

        String email = authentication.getName();

        Optional<User> optionalUser =
                userService.findByEmail(email);

        // check if user exists
        if (optionalUser.isEmpty()) {
            return "redirect:/login";
        }

        // get actual user object
        User user = optionalUser.get();

        List<Booking> bookings = bookingService.getBookingsForUser(user);

        model.addAttribute("bookings", bookings);
        model.addAttribute("user", user);

        return "booking-history";
    }

    @GetMapping("/history/search")
    public String searchBookings(@RequestParam String name, Model model) {

        List<Booking> bookings =
                bookingService.searchBookingsByCustomerName(name);

        model.addAttribute("bookings", bookings);

        return "booking-history";
    }

}