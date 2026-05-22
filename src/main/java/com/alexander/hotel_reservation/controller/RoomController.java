package com.alexander.hotel_reservation.controller;

import com.alexander.hotel_reservation.dto.RoomDto;
import com.alexander.hotel_reservation.entity.Room;
import com.alexander.hotel_reservation.entity.User;
import com.alexander.hotel_reservation.repository.RoomRepository;
import com.alexander.hotel_reservation.service.RoomService;
import com.alexander.hotel_reservation.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/rooms")
public class RoomController {

    @Autowired
    RoomService roomService;

    @Autowired
    UserService userService;

    @Autowired
    private RoomRepository roomRepository;

    // view all rooms
    @GetMapping
    public String allRooms(Model model,
                           Authentication authentication) {

        if (authentication == null) {
            return "redirect:/login";
        }

        String email = authentication.getName();
        Optional<User> user = userService.findByEmail(email);

        List<Room> rooms = roomService.getAllRooms();

        // send data to frontend
        model.addAttribute("rooms", rooms);
        model.addAttribute("user", user);

        return "rooms";
    }

    // search rooms
    @GetMapping("/search")
    public String searchRooms(@RequestParam("keyword") String keyword,
                              Model model,
                              Authentication authentication) {

        if (authentication == null) {
            return "redirect:/login";
        }

        String email = authentication.getName();
        Optional<User> user = userService.findByEmail(email);

        List<Room> rooms =
                roomRepository.searchByRoomType(keyword);

        model.addAttribute("rooms", rooms);
        model.addAttribute("user", user);

        return "rooms";
    }

    // view single room
    @GetMapping("/{id}")
    public String singleRoom(@PathVariable Long id,
                             Model model,
                             Authentication authentication) {

        if (authentication == null) {
            return "redirect:/login";
        }

        String email = authentication.getName();
        Optional<User> user = userService.findByEmail(email);

        Room room = roomService.getRoomById(id);

        model.addAttribute("room", room);
        model.addAttribute("user", user);

        return "room-details";
    }

    // show create form (admin only)
    @GetMapping("/new")
    public String createRoomPage(Model model,
                                 Authentication authentication) {

        if (authentication == null) {
            return "redirect:/login";
        }

        String email = authentication.getName();
        Optional<User> user = userService.findByEmail(email);

        // only admin allowed
//        if (!user.getRole().equals("admin")) {
//            return "redirect:/rooms";
//        }

        model.addAttribute("room", new RoomDto());

        return "room-form";
    }

    // save new room
    @PostMapping("/new")
    public String saveRoom(@ModelAttribute RoomDto roomDto,
                           Authentication authentication) {

        if (authentication == null) {
            return "redirect:/login";
        }
//
//        String email = authentication.getName();
//        User user = userService.findByEmail(email);
//
//        if (!user.getRole().equals("admin")) {
//            return "redirect:/rooms";
//        }

        roomService.createRoom(roomDto);

        return "redirect:/rooms";
    }


    // edit page
    @GetMapping("/edit/{id}")
    public String editRoom(@PathVariable Long id, Model model, Authentication authentication) {


        String email = authentication.getName();
        Optional<User> user = userService.findByEmail(email);

        Room room = roomService.getRoomById(id);

        // convert entity to dto
        RoomDto roomDto = new RoomDto();
        roomDto.setRoomType(room.getRoomType());
        roomDto.setPrice(room.getPrice());
        roomDto.setDescription(room.getDescription());
        roomDto.setAvailable(room.isAvailable());

        model.addAttribute("room", roomDto);
        model.addAttribute("roomId", id);

        return "edit-room";
    }

    @PostMapping("/edit/{id}")
    public String updateRoom(@PathVariable Long id,
                             @ModelAttribute("room") RoomDto roomDto) {

        roomService.updateRoom(id, roomDto);

        return "redirect:/rooms";
    }

}