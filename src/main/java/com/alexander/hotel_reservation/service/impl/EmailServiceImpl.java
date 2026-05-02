package com.alexander.hotel_reservation.service.impl;

import com.alexander.hotel_reservation.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendBookingStatusEmail(String toEmail, String status, String bookingCode) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("Hotel Booking <houseoface81@gmail.com>");
        message.setTo(toEmail);
        message.setSubject("Booking Update - " + status);

        String body = "Hello,\n\n" +
                "Your booking (" + bookingCode + ") status is now: " + status + "\n\n" +
                "Thank you for choosing our hotel.";

        message.setText(body);

        mailSender.send(message);
    }
}