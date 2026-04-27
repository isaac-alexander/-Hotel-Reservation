package com.alexander.hotel_reservation.controller;

import com.alexander.hotel_reservation.dto.CreateUserDto;
import com.alexander.hotel_reservation.entity.User;
import com.alexander.hotel_reservation.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // show create user page for admin + receptionist
    @GetMapping("/create")
    public String showCreateUser(Model model, HttpSession session) {

        // get logged in user safely
        Optional<User> userOptional =
                Optional.ofNullable((User) session.getAttribute("loggedInUser"));

        if (userOptional.isEmpty()) {
            return "redirect:/login";
        }

        model.addAttribute("user", new CreateUserDto());

        return "create-user";
    }

    //create user
    @PostMapping("/create")
    public String createUser(@Valid @ModelAttribute("user") CreateUserDto dto,
                             BindingResult result,
                             HttpSession session,
                             Model model) {

        Optional<User> userOptional =
                Optional.ofNullable((User) session.getAttribute("loggedInUser"));

        if (userOptional.isEmpty()) {
            return "redirect:/login";
        }

        if (result.hasErrors()) {
            return "create-user";
        }

        User currentUser = userOptional.get();

        // convert dto - entity
        User newUser = new User();
        newUser.setName(dto.getName());
        newUser.setEmail(dto.getEmail());
        newUser.setPassword(dto.getPassword());

        // admin
        if (currentUser.getRole().equals("admin")) {

            // admin can create receptionist or customer
            if (dto.getRole().equals("receptionist")) {
                newUser.setRole("receptionist");
            } else {
                newUser.setRole("customer");
            }

            userService.register(newUser);
        }

        // receptionist
        else if (currentUser.getRole().equals("receptionist")) {

            // receptionist always creates customer
            newUser.setRole("customer");

            userService.register(newUser);
        }

        // customers can't access
        else {
            return "redirect:/dashboard";
        }

        model.addAttribute("success", "User created successfully");

        return "create-user";
    }
}