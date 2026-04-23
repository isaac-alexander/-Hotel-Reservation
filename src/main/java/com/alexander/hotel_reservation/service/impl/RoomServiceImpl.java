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

        roomRepository.save(room);
    }

    // update room
    @Override
    public void updateRoom(Long roomId, RoomDto roomDto) {

        Optional<Room> roomOptional = roomRepository.findById(roomId);

        if (roomOptional.isPresent()) {

            Room room = roomOptional.get();

            room.setRoomType(roomDto.getRoomType());
            room.setPrice(roomDto.getPrice());
            room.setDescription(roomDto.getDescription());
            room.setAvailable(roomDto.isAvailable());

            roomRepository.save(room);
        }
    }

    // delete room
    @Override
    public void deleteRoom(Long roomId) {
        roomRepository.deleteById(roomId);
    }

    // get all rooms
    @Override
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    // get available rooms
    @Override
    public List<Room> getAvailableRooms() {
        return roomRepository.findAvailableRooms();
    }

    // get one room
    @Override
    public Room getRoomById(Long roomId) {

        Optional<Room> roomOptional = roomRepository.findById(roomId);

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