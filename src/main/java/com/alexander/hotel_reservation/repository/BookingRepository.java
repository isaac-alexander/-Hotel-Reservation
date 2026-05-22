package com.alexander.hotel_reservation.repository;

import com.alexander.hotel_reservation.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // get bookings for a specific room
    @Query(value = "SELECT * FROM bookings WHERE room_id = :roomId",
            nativeQuery = true)
    List<Booking> findByRoomId(@Param("roomId") Long roomId);



    // get bookings for a specific user
    @Query(value = "SELECT * FROM bookings WHERE user_id = :userId",
            nativeQuery = true)
    List<Booking> findByUser_Id(@Param("userId") Long userId);



    // get all bookings
    @Query(value = "SELECT * FROM bookings",
            nativeQuery = true)
    List<Booking> getAllBookings();



    // get booking by id
    @Query(value = "SELECT * FROM bookings WHERE id = :id",
            nativeQuery = true)
    Optional<Booking> getBookingById(@Param("id") Long id);



    // create booking using SQL
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO bookings " +
            "(user_id, room_id, check_in, check_out, total_price, booking_code, payment_status, payment_reference, status) " +
            "VALUES " +
            "(:userId, :roomId, :checkIn, :checkOut, :totalPrice, :bookingCode, :paymentStatus, :paymentReference, :status)",
            nativeQuery = true)
    void createBooking(
            @Param("userId") Long userId,
            @Param("roomId") Long roomId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut,
            @Param("totalPrice") double totalPrice,
            @Param("bookingCode") String bookingCode,
            @Param("paymentStatus") String paymentStatus,
            @Param("paymentReference") String paymentReference,
            @Param("status") String status
    );



    // update booking status
    @Modifying
    @Transactional
    @Query(value = "UPDATE bookings " +
            "SET status = :status " +
            "WHERE id = :bookingId",
            nativeQuery = true)
    void updateBookingStatus(
            @Param("bookingId") Long bookingId,
            @Param("status") String status
    );



    // update check in
    @Modifying
    @Transactional
    @Query(value = "UPDATE bookings " +
            "SET status = 'CHECKED_IN', " +
            "check_in_time = :checkInTime " +
            "WHERE id = :bookingId",
            nativeQuery = true)
    void updateCheckIn(
            @Param("bookingId") Long bookingId,
            @Param("checkInTime") String checkInTime
    );



    // update check out
    @Modifying
    @Transactional
    @Query(value = "UPDATE bookings " +
            "SET status = 'CHECKED_OUT', " +
            "check_out_time = :checkOutTime " +
            "WHERE id = :bookingId",
            nativeQuery = true)
    void updateCheckOut(
            @Param("bookingId") Long bookingId,
            @Param("checkOutTime") String checkOutTime
    );



    // find booking using payment reference
    @Query(value = "SELECT * FROM bookings WHERE payment_reference = :reference",
            nativeQuery = true)
    Optional<Booking> findBookingByPaymentReference(
            @Param("reference") String reference
    );



    // update payment and booking status
    @Modifying
    @Transactional
    @Query(value = "UPDATE bookings " +
            "SET payment_status = :paymentStatus, " +
            "status = :bookingStatus " +
            "WHERE payment_reference = :reference",
            nativeQuery = true)
    void updateBookingPayment(
            @Param("paymentStatus") String paymentStatus,
            @Param("bookingStatus") String bookingStatus,
            @Param("reference") String reference
    );



    // search bookings by customer name
    @Query(value = "SELECT b.* FROM bookings b " +
            "JOIN users u ON b.user_id = u.id " +
            "WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))",
            nativeQuery = true)
    List<Booking> searchByCustomerName(@Param("name") String name);

}