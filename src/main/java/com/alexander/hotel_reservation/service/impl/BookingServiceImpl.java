package com.alexander.hotel_reservation.service.impl;

import com.alexander.hotel_reservation.dto.BookingDto;
import com.alexander.hotel_reservation.entity.Booking;
import com.alexander.hotel_reservation.entity.User;
import com.alexander.hotel_reservation.repository.BookingRepository;
import com.alexander.hotel_reservation.repository.RoomRepository;
import com.alexander.hotel_reservation.service.BookingService;
import com.alexander.hotel_reservation.service.EmailService;
import org.springframework.stereotype.Service;
import com.alexander.hotel_reservation.entity.Room;

import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

        // check if any booking overlaps
        boolean overlapExists = existingBookings.stream()

                // ignore cancelled and rejected bookings
                .filter(existing ->
                        !existing.getStatus().equals("CANCELLED") &&
                                !existing.getStatus().equals("REJECTED")
                )

                // check if any booking overlaps
                .anyMatch(existing ->

                        bookingDto.getCheckIn().isBefore(existing.getCheckOut()) &&
                                bookingDto.getCheckOut().isAfter(existing.getCheckIn())
                );

        // if overlap exists
        if (overlapExists) {
            return false;
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

        // generate payment reference
        String reference = "PAY-" +
                UUID.randomUUID()
                        .toString()
                        .substring(0, 10)
                        .toUpperCase();

        // create new booking object
        Booking newBooking = new Booking();

        newBooking.setUser(user); // set user
        newBooking.setRoomId(bookingDto.getRoomId()); // set room
        newBooking.setCheckIn(bookingDto.getCheckIn()); // set check-in date
        newBooking.setCheckOut(bookingDto.getCheckOut()); // set check-out date

        newBooking.setTotalPrice(totalPrice);
        newBooking.setBookingCode(code);
        // payment details
        newBooking.setPaymentStatus("PENDING");
        newBooking.setPaymentReference(reference);

        // booking status
        newBooking.setStatus("PENDING");

        // save booking using SQL query
        bookingRepository.createBooking(
                user.getId(),
                bookingDto.getRoomId(),
                bookingDto.getCheckIn(),
                bookingDto.getCheckOut(),
                totalPrice,
                code,
                "PENDING",
                reference,
                "PENDING"
        );

        // make room unavailable immediately after booking
        roomRepository.makeRoomUnavailable(bookingDto.getRoomId());

        return true; // success
    }

    // GET BOOKINGS FOR USER
    @Override
    public List<Booking> getBookingsByUser(Long userId) {

        // get bookings for one user
        return bookingRepository.findByUser_Id(userId);
    }


    // GET BOOKINGS BASED ON USER ROLE
    @Override
    public List<Booking> getBookingsForUser(User user) {

        // admin and receptionist can see all bookings
        if (user.getRole().equals("admin") ||
                user.getRole().equals("receptionist")) {

            return bookingRepository.getAllBookings();
        }

        // customer can only see their own bookings
        return bookingRepository.findByUser_Id(user.getId());
    }

    // GET ALL BOOKINGS ADMIN / RECEPTIONIST
    @Override
    public List<Booking> getAllBookings() {

        // return all bookings
        return bookingRepository.getAllBookings();
    }

    // UPDATE STATUS
    @Override
    public void updateStatus(Long bookingId, String status) {

        // get booking from database
        Optional<Booking> bookingOptional = bookingRepository.getBookingById(bookingId);

        // check if booking exists
        if (bookingOptional.isPresent()) {

            Booking booking = bookingOptional.get();

            // update booking status using SQL query
            bookingRepository.updateBookingStatus(bookingId, status);

            // get customer email
            String email = booking.getUser().getEmail();

            // get booking code
            String bookingCode = booking.getBookingCode();

            // send email
            emailService.sendBookingStatusEmail(email, status, bookingCode);
        }
    }


    // CHECK IN
    @Override
    public void checkIn(Long bookingId) {

        // get booking from database
        Optional<Booking> bookingOptional = bookingRepository.getBookingById(bookingId);

        // check if booking exists
        if (bookingOptional.isPresent()) {

            // format current date and time
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy hh:mm a");

            // current formatted time
            String formattedTime = LocalDateTime.now().format(formatter);

            // update check in using SQL query
            bookingRepository.updateCheckIn(bookingId, formattedTime);
        }
    }

    // CHECK OUT
    @Override
    public void checkOut(Long bookingId) {

        // get booking from database
        Optional<Booking> bookingOptional = bookingRepository.getBookingById(bookingId);

        // check if booking exists
        if (bookingOptional.isPresent()) {

            // format current date and time
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy hh:mm a");

            // current formatted time
            String formattedTime = LocalDateTime.now().format(formatter);

            // update check out using SQL query
            bookingRepository.updateCheckOut(bookingId, formattedTime);
        }
    }

    // GET SINGLE BOOKING
    @Override
    public Booking getBookingById(Long id) {

        // get booking using SQL query
        Optional<Booking> bookingOptional = bookingRepository.getBookingById(id);

        // return booking if found
        return bookingOptional.orElse(null);
    }


    // SEARCH BOOKINGS
    @Override
    public List<Booking> searchBookingsByCustomerName(String name) {

        // search booking using customer name
        return bookingRepository.searchByCustomerName(name);
    }

}