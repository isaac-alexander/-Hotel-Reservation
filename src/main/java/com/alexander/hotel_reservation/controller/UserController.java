package com.alexander.hotel_reservation.controller;

import com.alexander.hotel_reservation.dto.CreateUserDto;
import com.alexander.hotel_reservation.entity.User;
import com.alexander.hotel_reservation.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // show register page
    @GetMapping("/register")
    public String showRegister(Model model) {

        model.addAttribute("user", new CreateUserDto());

        return "register";
    }

    // handle register
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") CreateUserDto dto,
                           BindingResult result,
                           Model model) {

        // handle validation errors
        if (result.hasErrors()) {
            return "register";
        }

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setRole("customer");

        try {
            userService.register(user);
            model.addAttribute("success", "Registration Successful");
            return "register";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }
}