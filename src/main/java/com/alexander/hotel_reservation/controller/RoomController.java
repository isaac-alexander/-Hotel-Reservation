package com.alexander.hotel_reservation.controller;

import com.alexander.hotel_reservation.dto.RoomDto;
import com.alexander.hotel_reservation.entity.Room;
import com.alexander.hotel_reservation.entity.User;
import com.alexander.hotel_reservation.service.RoomService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    // view all rooms
    @GetMapping
    public String allRooms(Model model, HttpSession session) {

        // get logged in user safely using Optional
        Optional<User> userOptional =
                Optional.ofNullable((User) session.getAttribute("loggedInUser"));

        // if user not logged in redirect to login page
        if (userOptional.isEmpty()) {
            return "redirect:/login";
        }

        // get actual user
        User user = userOptional.get();

        // get all rooms from database
        List<Room> rooms = roomService.getAllRooms();

        // send data to frontend
        model.addAttribute("rooms", rooms);
        model.addAttribute("user", user);

        return "rooms";
    }

    // view single room
    @GetMapping("/{id}")
    public String singleRoom(@PathVariable Long id, Model model, HttpSession session) {

        Optional<User> userOptional =
                Optional.ofNullable((User) session.getAttribute("loggedInUser"));

        if (userOptional.isEmpty()) {
            return "redirect:/login";
        }

        User user = userOptional.get();

        Room room = roomService.getRoomById(id);

        model.addAttribute("room", room);
        model.addAttribute("user", user);

        return "room-details";
    }

    // show create form (admin only)
    @GetMapping("/new")
    public String createRoomPage(Model model, HttpSession session) {

        Optional<User> userOptional =
                Optional.ofNullable((User) session.getAttribute("loggedInUser"));

        if (userOptional.isEmpty()) {
            return "redirect:/login";
        }

        User user = userOptional.get();

        // only admin allowed
        if (!user.getRole().equals("admin")) {
            return "redirect:/rooms";
        }

        model.addAttribute("room", new RoomDto());

        return "room-form";
    }

    // save new room
    @PostMapping("/new")
    public String saveRoom(@ModelAttribute RoomDto roomDto, HttpSession session) {

        Optional<User> userOptional =
                Optional.ofNullable((User) session.getAttribute("loggedInUser"));

        if (userOptional.isEmpty()) {
            return "redirect:/login";
        }

        User user = userOptional.get();

        if (!user.getRole().equals("admin")) {
            return "redirect:/rooms";
        }

        roomService.createRoom(roomDto);

        return "redirect:/rooms";
    }

    // edit page
    @GetMapping("/edit/{id}")
    public String editRoom(@PathVariable Long id, Model model, HttpSession session) {

        Optional<User> userOptional =
                Optional.ofNullable((User) session.getAttribute("loggedInUser"));

        if (userOptional.isEmpty()) {
            return "redirect:/login";
        }

        User user = userOptional.get();

        if (!user.getRole().equals("admin")) {
            return "redirect:/rooms";
        }

        Room room = roomService.getRoomById(id);

        RoomDto dto = new RoomDto();
        dto.setRoomType(room.getRoomType());
        dto.setPrice(room.getPrice());
        dto.setDescription(room.getDescription());
        dto.setAvailable(room.isAvailable());

        model.addAttribute("room", dto);
        model.addAttribute("roomId", id);

        return "edit-room";
    }

    // update room
    @PostMapping("/edit/{id}")
    public String updateRoom(@PathVariable Long id,
                             @ModelAttribute RoomDto roomDto,
                             HttpSession session) {

        Optional<User> userOptional =
                Optional.ofNullable((User) session.getAttribute("loggedInUser"));

        if (userOptional.isEmpty()) {
            return "redirect:/login";
        }

        User user = userOptional.get();

        if (!user.getRole().equals("admin")) {
            return "redirect:/rooms";
        }

        roomService.updateRoom(id, roomDto);

        return "redirect:/rooms";
    }

    // delete room
    @GetMapping("/delete/{id}")
    public String deleteRoom(@PathVariable Long id, HttpSession session) {

        Optional<User> userOptional =
                Optional.ofNullable((User) session.getAttribute("loggedInUser"));

        if (userOptional.isEmpty()) {
            return "redirect:/login";
        }

        User user = userOptional.get();

        if (!user.getRole().equals("admin")) {
            return "redirect:/rooms";
        }

        roomService.deleteRoom(id);

        return "redirect:/rooms";
    }
}