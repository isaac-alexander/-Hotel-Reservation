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

@Controller
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;

    // inject service
    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    // view all rooms
    @GetMapping
    public String allRooms(Model model, HttpSession session) {

        // get user from session
        Object userObject = session.getAttribute("loggedInUser");

        // redirect if not logged in
        if (userObject == null) {
            return "redirect:/login";
        }

        // cast to user
        User loggedInUser = (User) userObject;

        // fetch rooms
        List<Room> rooms = roomService.getAllRooms();

        // send data to frontend
        model.addAttribute("rooms", rooms);
        model.addAttribute("user", loggedInUser);

        return "rooms";
    }

    // view single room
    @GetMapping("/{id}")
    public String singleRoom(@PathVariable Long id, Model model, HttpSession session) {

        Object userObject = session.getAttribute("loggedInUser");

        if (userObject == null) {
            return "redirect:/login";
        }

        User loggedInUser = (User) userObject;

        // get room by id
        Room room = roomService.getRoomById(id);

        model.addAttribute("room", room);
        model.addAttribute("user", loggedInUser);

        return "room-details";
    }

    // show create form (admin only)
    @GetMapping("/new")
    public String createRoomPage(Model model, HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        // block non-admin users
        if (user == null || !user.getRole().equals("admin")) {
            return "redirect:/rooms";
        }

        model.addAttribute("room", new RoomDto());

        return "room-form";
    }

    // save new room
    @PostMapping("/new")
    public String saveRoom(@ModelAttribute RoomDto roomDto, HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null || !user.getRole().equals("admin")) {
            return "redirect:/rooms";
        }

        // call service
        roomService.createRoom(roomDto);

        return "redirect:/rooms";
    }

    // edit page
    @GetMapping("/edit/{id}")
    public String editRoom(@PathVariable Long id, Model model, HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null || !user.getRole().equals("admin")) {
            return "redirect:/rooms";
        }

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

    // update room
    @PostMapping("/edit/{id}")
    public String updateRoom(@PathVariable Long id,
                             @ModelAttribute RoomDto roomDto,
                             HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null || !user.getRole().equals("admin")) {
            return "redirect:/rooms";
        }

        roomService.updateRoom(id, roomDto);

        return "redirect:/rooms";
    }

    // delete room
    @GetMapping("/delete/{id}")
    public String deleteRoom(@PathVariable Long id, HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null || !user.getRole().equals("admin")) {
            return "redirect:/rooms";
        }

        roomService.deleteRoom(id);

        return "redirect:/rooms";
    }

    // book room (customer only)
    @GetMapping("/book/{id}")
    public String bookRoom(@PathVariable Long id, HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        // block admin from booking
        if (user == null || !user.getRole().equals("customer")) {
            return "redirect:/rooms";
        }

        roomService.bookRoom(id);

        return "redirect:/rooms";
    }
}