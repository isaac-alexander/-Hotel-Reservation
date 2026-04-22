package com.alexander.hotel_reservation.controller;

import com.alexander.hotel_reservation.dto.CreateUserDto;
import com.alexander.hotel_reservation.entity.User;
import com.alexander.hotel_reservation.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String register(@ModelAttribute("user") CreateUserDto dto,
                           RedirectAttributes redirectAttributes) {

        // convert dto -  entity
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setRole("customer");

        userService.register(user);

        redirectAttributes.addFlashAttribute("success", "registration successful");

        return "redirect:/login";
    }
}