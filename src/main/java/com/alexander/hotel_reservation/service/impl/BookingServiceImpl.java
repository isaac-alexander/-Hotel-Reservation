package com.alexander.hotel_reservation.service.impl;

import com.alexander.hotel_reservation.dto.BookingDto;
import com.alexander.hotel_reservation.entity.Booking;
import com.alexander.hotel_reservation.entity.User;
import com.alexander.hotel_reservation.repository.BookingRepository;
import com.alexander.hotel_reservation.service.BookingService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    public BookingServiceImpl(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    // CREATE BOOKING
    @Override
    public boolean createBooking(BookingDto bookingDto, User user) {

        // get all bookings for this room
        List<Booking> existingBookings =
                bookingRepository.findByRoomId(bookingDto.getRoomId());

        // loop through existing bookings
        for (Booking existing : existingBookings) {

            String status = existing.getStatus();

            // ignore cancelled or rejected bookings
            if (status.equals("CANCELLED") || status.equals("REJECTED")) {
                continue;
            }

            //  overlap
            boolean overlap =
                    bookingDto.getCheckIn().isBefore(existing.getCheckOut()) &&
                            bookingDto.getCheckOut().isAfter(existing.getCheckIn());

            // if overlap exists - booking not allowed
            if (overlap) {
                return false;
            }
        }

        // create new booking object
        Booking newBooking = new Booking();

        newBooking.setUser(user); // set user
        newBooking.setRoomId(bookingDto.getRoomId()); // set room
        newBooking.setCheckIn(bookingDto.getCheckIn()); // set check-in date
        newBooking.setCheckOut(bookingDto.getCheckOut()); // set check-out date

        // default status
        newBooking.setStatus("PENDING");

        // save booking
        bookingRepository.save(newBooking);

        return true; // success
    }

    // GET BOOKINGS FOR USER
    @Override
    public List<Booking> getBookingsByUser(Long userId) {
        return bookingRepository.findByUser_Id(userId);
    }

    // GET ALL BOOKINGS ADMIN / RECEPTIONIST
    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    // UPDATE STATUS - CONFIRM / REJECT / CANCEL
    @Override
    public void updateStatus(Long bookingId, String status) {

        Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);

        if (bookingOptional.isPresent()) {

            Booking booking = bookingOptional.get();

            booking.setStatus(status); // update status

            bookingRepository.save(booking); // save changes
        }
    }

    // CHECK-IN
    @Override
    public void checkIn(Long bookingId) {

        Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);

        if (bookingOptional.isPresent()) {

            Booking booking = bookingOptional.get();

            booking.setStatus("CHECKED_IN"); // update status

            // store current time
            booking.setCheckInTime(LocalDateTime.now().toString());

            bookingRepository.save(booking);
        }
    }

    // check - out
    @Override
    public void checkOut(Long bookingId) {

        Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);

        if (bookingOptional.isPresent()) {

            Booking booking = bookingOptional.get();

            booking.setStatus("CHECKED_OUT"); // update status

            // store current time
            booking.setCheckOutTime(LocalDateTime.now().toString());

            bookingRepository.save(booking);
        }
    }

    // get single booking
    @Override
    public Booking getBookingById(Long id) {

        Optional<Booking> bookingOptional = bookingRepository.findById(id);

        return bookingOptional.orElse(null); // return booking or null
    }
}