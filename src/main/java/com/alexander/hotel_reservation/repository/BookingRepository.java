package com.alexander.hotel_reservation.repository;

import com.alexander.hotel_reservation.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // get bookings for a specific room
    List<Booking> findByRoomId(Long roomId);

    // get bookings for a specific user
    List<Booking> findByUser_Id(Long userId);
}