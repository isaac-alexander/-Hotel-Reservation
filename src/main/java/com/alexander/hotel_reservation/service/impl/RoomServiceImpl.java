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

    // constructor injection for repository
    public RoomServiceImpl(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    // create new room
    @Override
    public void createRoom(RoomDto roomDto) {

        // create new entity object
        Room room = new Room();

        // map values from dto to entity
        room.setRoomType(roomDto.getRoomType());
        room.setPrice(roomDto.getPrice());
        room.setDescription(roomDto.getDescription());

        // set default availability to true if not provided
        room.setAvailable(roomDto.isAvailable());

        // save to database
        roomRepository.save(room);
    }

    // update room
    @Override
    public void updateRoom(Long roomId, RoomDto roomDto) {

        // find room using optional
        Optional<Room> optionalRoom = roomRepository.findById(roomId);

        // check if room exists
        if (optionalRoom.isPresent()) {

            // get actual room object
            Room room = optionalRoom.get();

            // update fields
            room.setRoomType(roomDto.getRoomType());
            room.setPrice(roomDto.getPrice());
            room.setDescription(roomDto.getDescription());
            room.setAvailable(roomDto.isAvailable());

            // save updated room
            roomRepository.save(room);
        }
    }

    // delete room
    @Override
    public void deleteRoom(Long roomId) {

        // delete directly by id
        roomRepository.deleteById(roomId);
    }

    // get all rooms
    @Override
    public List<Room> getAllRooms() {

        // return all rooms
        return roomRepository.findAll();
    }

    // get only available rooms
    @Override
    public List<Room> getAvailableRooms() {

        // custom query method in repository
        return roomRepository.findAvailableRooms();
    }

    // get single room
    @Override
    public Room getRoomById(Long roomId) {

        // use optional to avoid null pointer
        Optional<Room> optionalRoom = roomRepository.findById(roomId);

        // return room if found
        if (optionalRoom.isPresent()) {
            return optionalRoom.get();
        }

        // return null if not found (simple beginner approach)
        return null;
    }

    // booking logic
    @Override
    public void bookRoom(Long roomId) {

        // find room safely
        Optional<Room> optionalRoom = roomRepository.findById(roomId);

        // check if room exists
        if (optionalRoom.isPresent()) {

            Room room = optionalRoom.get();

            // only book if available
            if (room.isAvailable()) {

                // mark as unavailable after booking
                room.setAvailable(false);

                // save updated room
                roomRepository.save(room);
            }
        }
    }
}