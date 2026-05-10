package com.alexander.hotel_reservation.service.impl;

import com.alexander.hotel_reservation.dto.RoomDto;
import com.alexander.hotel_reservation.entity.Room;
import com.alexander.hotel_reservation.repository.RoomRepository;
import com.alexander.hotel_reservation.service.RoomService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    public RoomServiceImpl(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    // create new room
    @Override
    public void createRoom(RoomDto roomDto) {

        Room room = new Room();

        room.setRoomType(roomDto.getRoomType());
        room.setPrice(roomDto.getPrice());
        room.setDescription(roomDto.getDescription());

        // default availability true if not set
        room.setAvailable(roomDto.isAvailable());

        // save room using SQL query
        roomRepository.createRoom(
                room.getRoomType(),
                room.getPrice(),
                room.getDescription(),
                room.isAvailable()
        );
    }

    // update room
    @Override
    public void updateRoom(Long roomId, RoomDto roomDto) {

        // update room using SQL query
        roomRepository.updateRoom(
                roomId,
                roomDto.getRoomType(),
                roomDto.getPrice(),
                roomDto.getDescription(),
                roomDto.isAvailable()
        );
    }

    // delete room
    @Override
    public void deleteRoom(Long roomId) {
        // delete room using SQL query
        roomRepository.deleteRoom(roomId);
    }

    // get all rooms
    @Override
    public List<Room> getAllRooms() {
        // get all rooms using SQL query
        return roomRepository.getAllRooms();
    }

    // get available rooms
    @Override
    public List<Room> getAvailableRooms() {
        return roomRepository.findAvailableRooms();
    }

    // get one room
    @Override
    public Room getRoomById(Long roomId) {

        // get room using SQL query
        Optional<Room> roomOptional = roomRepository.getRoomById(roomId);

        if (roomOptional.isPresent()) {
            return roomOptional.get();
        }

        return null;
    }

    // book room
    @Override
    public void bookRoom(Long roomId) {

        Optional<Room> roomOptional = roomRepository.findById(roomId);

        if (roomOptional.isPresent()) {

            Room room = roomOptional.get();

            if (room.isAvailable()) {
                room.setAvailable(false);
                roomRepository.save(room);
            }
        }
    }
}