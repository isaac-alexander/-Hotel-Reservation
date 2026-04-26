package com.alexander.hotel_reservation.service;

import com.alexander.hotel_reservation.dto.BookingDto;
import com.alexander.hotel_reservation.entity.Booking;
import com.alexander.hotel_reservation.entity.User;

import java.util.List;

public interface BookingService {

    // create booking
    boolean createBooking(BookingDto bookingDto, User user);

    // get bookings for logged in user
    List<Booking> getBookingsByUser(Long userId);

    // get all bookings admin/receptionist
    List<Booking> getAllBookings();

    // confirm booking receptionist/admin
    void updateStatus(Long bookingId, String status);

    // check in guest receptionist
    void checkIn(Long bookingId);

    // check out guest receptionist
    void checkOut(Long bookingId);
}