package com.alexander.hotel_reservation.service.impl;

import com.alexander.hotel_reservation.dto.BookingDto;
import com.alexander.hotel_reservation.entity.Booking;
import com.alexander.hotel_reservation.entity.User;
import com.alexander.hotel_reservation.repository.BookingRepository;
import com.alexander.hotel_reservation.repository.RoomRepository;
import com.alexander.hotel_reservation.service.BookingService;
import com.alexander.hotel_reservation.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alexander.hotel_reservation.entity.Room;

import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final RoomRepository roomRepository;

    private final EmailService emailService;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              RoomRepository roomRepository,
                              EmailService emailService) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.emailService = emailService;
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

        // get room from database
        Optional<Room> roomOptional = roomRepository.findById(bookingDto.getRoomId());

        // if room does not exist
        if (roomOptional.isEmpty()) {
            return false;
        }

        Room room = roomOptional.get();

        // calculate number of days
        long days = ChronoUnit.DAYS.between(
                bookingDto.getCheckIn(),
                bookingDto.getCheckOut()
        );

        // prevent wrong booking
        if (days <= 0) {
            return false;
        }

        // calculate total price using room price
        double totalPrice = room.getPrice() * days;

        // generate booking code (simple unique code)
        String code = "BK-" + UUID.randomUUID().toString()
                .substring(0, 8)
                .toUpperCase();

        // create new booking object
        Booking newBooking = new Booking();

        newBooking.setUser(user); // set user
        newBooking.setRoomId(bookingDto.getRoomId()); // set room
        newBooking.setCheckIn(bookingDto.getCheckIn()); // set check-in date
        newBooking.setCheckOut(bookingDto.getCheckOut()); // set check-out date

        newBooking.setTotalPrice(totalPrice);
        newBooking.setBookingCode(code);

        // default status
        newBooking.setStatus("PENDING");

        // save booking
        bookingRepository.save(newBooking);

        emailService.sendBookingStatusEmail(
                user.getEmail(),
                "PENDING",
                newBooking.getBookingCode()
        );

        return true; // success
    }

    // GET BOOKINGS FOR USER
    @Override
    public List<Booking> getBookingsByUser(Long userId) {
        return bookingRepository.findByUser_Id(userId);
    }

    @Override
    public List<Booking> getBookingsForUser(User user) {

        // ADMIN & RECEPTIONIST - see all bookings
        if (user.getRole().equals("admin") || user.getRole().equals("receptionist")) {
            return bookingRepository.findAll();
        }

        // CUSTOMER - only their bookings
        return bookingRepository.findByUser_Id(user.getId());
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

            // SEND EMAIL
            String email = booking.getUser().getEmail();
            String bookingCode = booking.getBookingCode();

            emailService.sendBookingStatusEmail(email, status, bookingCode);
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