package com.alexander.hotel_reservation.repository;

import com.alexander.hotel_reservation.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // get bookings for a specific room
    List<Booking> findByRoomId(Long roomId);

    // get bookings for a specific user
    List<Booking> findByUser_Id(Long userId);

    // find booking using payment reference
    @Query(value = "SELECT * FROM bookings WHERE payment_reference = :reference",
            nativeQuery = true)
    Booking findBookingByPaymentReference(
            @Param("reference") String reference
    );

    // update payment and booking status after successful payment
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

    @Query(value = "SELECT b.* FROM bookings b " +
            "JOIN users u ON b.user_id = u.id " +
            "WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))",
            nativeQuery = true)
    List<Booking> searchByCustomerName(@Param("name") String name);

}