package com.alexander.hotel_reservation.controller;

import com.alexander.hotel_reservation.dto.UserRequest;
import com.alexander.hotel_reservation.entity.User;
import com.alexander.hotel_reservation.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {

        model.addAttribute("user", new UserRequest());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("user") UserRequest request) {

        userService.register(request);

        return "redirect:/users/register?success";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // loads login.html
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        Model model) {

        User user = userService.login(email, password);

        if (user == null) {
            model.addAttribute("error", true);
            return "login"; // reload login page
        }

        // successful login
        model.addAttribute("user", user);

        return "home"; // we will create this next
    }

}