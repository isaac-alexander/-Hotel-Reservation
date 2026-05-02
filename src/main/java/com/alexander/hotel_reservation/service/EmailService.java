package com.alexander.hotel_reservation.service;

public interface EmailService {

    void sendBookingStatusEmail(String toEmail, String status, String bookingCode);
}