package com.alexander.hotel_reservation.controller;

import com.alexander.hotel_reservation.entity.Booking;
import com.alexander.hotel_reservation.repository.BookingRepository;
import com.alexander.hotel_reservation.service.PaymentService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    private final BookingRepository bookingRepository;

    public PaymentController(PaymentService paymentService,
                             BookingRepository bookingRepository) {

        this.paymentService = paymentService;
        this.bookingRepository = bookingRepository;
    }

    // paystack redirects here after payment
    @GetMapping("/verify")
    public String verifyPayment(@RequestParam("reference") String reference,
                                RedirectAttributes redirectAttributes) {

        // verify payment from paystack
        boolean verified = paymentService.verifyPayment(reference);

        // if payment successful
        if (verified) {

            // get booking using payment reference
            Booking booking =
                    bookingRepository.findBookingByPaymentReference(reference);

            // if booking exists
            if (booking != null) {

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