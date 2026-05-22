package com.alexander.hotel_reservation.controller;

import com.alexander.hotel_reservation.entity.Booking;
import com.alexander.hotel_reservation.repository.BookingRepository;
import com.alexander.hotel_reservation.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    PaymentService paymentService;

    @Autowired
    BookingRepository bookingRepository;

    // paystack redirects here after payment
    @GetMapping("/verify")
    public String verifyPayment(@RequestParam("reference") String reference,
                                RedirectAttributes redirectAttributes) {

        // verify payment from paystack
        boolean verified = paymentService.verifyPayment(reference);

        // if payment successful
        if (verified) {

            // get booking using payment reference
            Optional<Booking> booking =
                    bookingRepository.findBookingByPaymentReference(reference);

            // if booking exists
            if (booking.isPresent()) {

                // update booking payment using SQL query
                bookingRepository.updateBookingPayment(
                        "PAID",
                        "CONFIRMED",
                        reference
                );
            }

            redirectAttributes.addFlashAttribute(
                    "success",
                    "payment successful"
            );

        } else {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "payment verification failed"
            );
        }

        // return to booking history
        return "redirect:/bookings/history";
    }
}