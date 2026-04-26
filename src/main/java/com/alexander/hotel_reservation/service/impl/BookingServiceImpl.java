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

    // create booking
    @Override
    public boolean createBooking(BookingDto bookingDto, User user) {

        List<Booking> existingBookings =
                bookingRepository.findByRoomId(bookingDto.getRoomId());

        for (Booking existing : existingBookings) {

            boolean isConflict =
                    !(bookingDto.getCheckOut().isBefore(existing.getCheckIn()) ||
                            bookingDto.getCheckIn().isAfter(existing.getCheckOut()));

            if (isConflict) {
                return false;
            }
        }

        Booking newBooking = new Booking();

        newBooking.setUser(user);
        newBooking.setRoomId(bookingDto.getRoomId());
        newBooking.setCheckIn(bookingDto.getCheckIn());
        newBooking.setCheckOut(bookingDto.getCheckOut());

        // default status
        newBooking.setStatus("PENDING");

        bookingRepository.save(newBooking);

        return true;
    }

    // get bookings for a user
    @Override
    public List<Booking> getBookingsByUser(Long userId) {
        return bookingRepository.findByUser_Id(userId);
    }

    // admin gets all bookings
    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    // confirm / update status
    @Override
    public void updateStatus(Long bookingId, String status) {

        Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);

        if (bookingOptional.isPresent()) {

            Booking booking = bookingOptional.get();

            booking.setStatus(status);

            bookingRepository.save(booking);
        }
    }

    // check-in
    @Override
    public void checkIn(Long bookingId) {

        Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);

        if (bookingOptional.isPresent()) {

            Booking booking = bookingOptional.get();

            booking.setStatus("CHECKED_IN");

            // store time
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

            booking.setStatus("CHECKED_OUT");

            // store time
            booking.setCheckOutTime(LocalDateTime.now().toString());

            bookingRepository.save(booking);
        }
    }
}