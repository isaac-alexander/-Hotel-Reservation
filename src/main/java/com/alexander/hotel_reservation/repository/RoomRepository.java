package com.alexander.hotel_reservation.repository;

import com.alexander.hotel_reservation.entity.Room;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    // get all rooms
    @Query(value = "SELECT * FROM room",
            nativeQuery = true)
    List<Room> getAllRooms();



    // get room by id
    @Query(value = "SELECT * FROM room WHERE id = :id",
            nativeQuery = true)
    Optional<Room> getRoomById(@Param("id") Long id);



    // get available rooms
    @Query(value = "SELECT * FROM room WHERE available = true",
            nativeQuery = true)
    List<Room> findAvailableRooms();



    // search room by type
    @Query(value = "SELECT * FROM room " +
            "WHERE lower(room_type) " +
            "LIKE lower(concat('%', :type, '%'))",
            nativeQuery = true)
    List<Room> searchByRoomType(@Param("type") String type);



    // create room
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO room " +
            "(room_type, price, description, available) " +
            "VALUES " +
            "(:roomType, :price, :description, :available)",
            nativeQuery = true)
    void createRoom(
            @Param("roomType") String roomType,
            @Param("price") double price,
            @Param("description") String description,
            @Param("available") boolean available
    );



    // update room
    @Modifying
    @Transactional
    @Query(value = "UPDATE room SET " +
            "room_type = :roomType, " +
            "price = :price, " +
            "description = :description, " +
            "available = :available " +
            "WHERE id = :roomId",
            nativeQuery = true)
    void updateRoom(
            @Param("roomId") Long roomId,
            @Param("roomType") String roomType,
            @Param("price") double price,
            @Param("description") String description,
            @Param("available") boolean available
    );



    // delete room
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM room WHERE id = :roomId",
            nativeQuery = true)
    void deleteRoom(@Param("roomId") Long roomId);



    // make room unavailable
    @Modifying
    @Transactional
    @Query(value = "UPDATE room " +
            "SET available = false " +
            "WHERE id = :roomId",
            nativeQuery = true)
    void makeRoomUnavailable(@Param("roomId") Long roomId);

}