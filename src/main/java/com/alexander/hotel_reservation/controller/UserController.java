package com.alexander.hotel_reservation.controller;

import com.alexander.hotel_reservation.dto.CreateUserDto;
import com.alexander.hotel_reservation.entity.User;
import com.alexander.hotel_reservation.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // show create user page
    @GetMapping("/create")
    public String showCreateUser(Model model,
                                 Authentication authentication) {

        if (authentication == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", new CreateUserDto());

        return "create-user";
    }

    // create user
    @PostMapping("/create")
    public String createUser(@Valid @ModelAttribute("user") CreateUserDto dto,
                             BindingResult result,
                             Authentication authentication,
                             Model model) {

        if (authentication == null) {
            return "redirect:/login";
        }

        if (result.hasErrors()) {
            return "create-user";
        }

        // get current logged in user
        String email = authentication.getName();
        User currentUser = userService.findByEmail(email);

        User newUser = new User();
        newUser.setName(dto.getName());
        newUser.setEmail(dto.getEmail());
        newUser.setPassword(dto.getPassword());

        try {

            // ADMIN
            if (currentUser.getRole().equals("admin")) {

                if ("receptionist".equals(dto.getRole())) {
                    newUser.setRole("receptionist");
                } else {
                    newUser.setRole("customer");
                }

                userService.register(newUser);
            }

            // RECEPTIONIST
            else if (currentUser.getRole().equals("receptionist")) {

                newUser.setRole("customer");

                userService.register(newUser);
            }

            // CUSTOMER BLOCKED
            else {
                return "redirect:/dashboard";
            }

            model.addAttribute("success", "User created successfully");

        } catch (RuntimeException e) {

            model.addAttribute("error", e.getMessage());
        }

        return "create-user";
    }
}