package com.alexander.hotel_reservation.service;

import com.alexander.hotel_reservation.dto.RoomDto;
import com.alexander.hotel_reservation.entity.Room;

import java.util.List;

public interface RoomService {

    // create a new room using dto
    void createRoom(RoomDto roomDto);

    // update an existing room by id
    void updateRoom(Long roomId, RoomDto roomDto);

    // delete a room by id
    void deleteRoom(Long roomId);

    // get all rooms (admin + customer)
    List<Room> getAllRooms();

    // get only available rooms
    List<Room> getAvailableRooms();

    // get a single room by id
    Room getRoomById(Long roomId);

    // book a room (set available to false)
    void bookRoom(Long roomId);
}