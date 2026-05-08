package com.alexander.hotel_reservation.service;

public interface PaymentService {

    // initialize payment and return paystack payment link
    String initializePayment(String email, double amount, String reference);

    // verify payment using reference from paystack
    boolean verifyPayment(String reference);
}