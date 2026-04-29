package com.alexander.hotel_reservation.repository;

import com.alexander.hotel_reservation.entity.Room;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    //  get all available rooms
    @Query(value = "select * from room where available = true", nativeQuery = true)
    List<Room> findAvailableRooms();

    //  get room by type search
    @Query(value = "select * from room where lower(room_type) like lower(concat('%', :type, '%'))", nativeQuery = true)
    List<Room> searchByRoomType(@Param("type") String type);
//    List<Room> findByRoomTypeContainingIgnoreCase(String type);

}