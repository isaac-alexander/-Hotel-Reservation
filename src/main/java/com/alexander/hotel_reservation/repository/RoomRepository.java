package com.alexander.hotel_reservation.repository;

import com.alexander.hotel_reservation.entity.Room;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    //  get all available rooms
    @Query(value = "SELECT * FROM room WHERE available = true", nativeQuery = true)
    List<Room> findAvailableRooms();

    //  get room by type search
    @Query(value = "SELECT * FROM room WHERE lower(room_type) LIKE lower(concat('%', :type, '%'))", nativeQuery = true)
    List<Room> searchByRoomType(@Param("type") String type);

    @Modifying
    @Transactional
    @Query(value = "UPDATE room SET available = false WHERE id = :roomId", nativeQuery = true)
    void makeRoomUnavailable(@Param("roomId") Long roomId);

}