package com.alexander.hotel_reservation.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/")
    public String defaultRoute() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage(Authentication authentication) {


        if (authentication != null) {
            return "redirect:/dashboard"; // send to dashboard directly
        }

        return "login"; // show login page
    }
}