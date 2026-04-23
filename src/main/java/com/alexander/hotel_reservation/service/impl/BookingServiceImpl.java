package com.alexander.hotel_reservation.service.impl;

import com.alexander.hotel_reservation.dto.BookingDto;
import com.alexander.hotel_reservation.entity.Booking;
import com.alexander.hotel_reservation.entity.User;
import com.alexander.hotel_reservation.repository.BookingRepository;
import com.alexander.hotel_reservation.service.BookingService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    public BookingServiceImpl(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    public boolean createBooking(BookingDto bookingDto, User user) {

        // get all bookings for this room
        List<Booking> existingBookings =
                bookingRepository.findByRoomId(bookingDto.getRoomId());

        // check for date conflicts
        for (Booking existing : existingBookings) {

            boolean isConflict =
                    !(bookingDto.getCheckOut().isBefore(existing.getCheckIn()) ||
                            bookingDto.getCheckIn().isAfter(existing.getCheckOut()));

            if (isConflict) {
                return false;
            }
        }

        // create booking
        Booking newBooking = new Booking();

        newBooking.setUserId(user.getId());
        newBooking.setRoomId(bookingDto.getRoomId());
        newBooking.setCheckIn(bookingDto.getCheckIn());
        newBooking.setCheckOut(bookingDto.getCheckOut());

        bookingRepository.save(newBooking);

        return true;
    }

    // get bookings for a user
    @Override
    public List<Booking> getBookingsByUser(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    // admin gets all bookings
    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
}