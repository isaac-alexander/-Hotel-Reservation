package com.alexander.hotel_reservation.controller;

import com.alexander.hotel_reservation.dto.LoginDto;
import com.alexander.hotel_reservation.entity.User;
import com.alexander.hotel_reservation.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String defaultRoute() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginDto dto,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {

        User user = userService.login(dto.getEmail(), dto.getPassword());

        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "invalid login");
            return "redirect:/login";
        }

        session.setAttribute("loggedInUser", user);

        return "redirect:/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}