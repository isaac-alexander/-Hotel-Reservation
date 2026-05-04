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

    private boolean isAdminOrReceptionist(User user) {
        return user.getRole().equals("admin") ||
                user.getRole().equals("receptionist");
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
        newUser.setRole(dto.getRole());

        try {
            userService.register(newUser);
            model.addAttribute("success", "User created successfully");
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
        }

        return "create-user";
    }

    @GetMapping
    public String viewUsers(Model model, Authentication authentication) {

        if (authentication == null) return "redirect:/login";

        User currentUser = userService.findByEmail(authentication.getName());

        if (!isAdminOrReceptionist(currentUser)) {
            return "redirect:/dashboard";
        }

        model.addAttribute("users", userService.getAllUsers());

        return "users"; // create users.html
    }

    @GetMapping("/edit/{id}")
    public String showEditUser(@PathVariable Long id,
                               Model model,
                               Authentication authentication) {

        if (authentication == null) return "redirect:/login";

        User currentUser = userService.findByEmail(authentication.getName());

        if (!isAdminOrReceptionist(currentUser)) {
            return "redirect:/dashboard";
        }

        User user = userService.getUserById(id);

        if (user == null) return "redirect:/users";

        CreateUserDto dto = new CreateUserDto();
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());

        model.addAttribute("user", dto);
        model.addAttribute("userId", id);

        return "edit-user";
    }

    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable Long id,
                             @ModelAttribute("user") CreateUserDto dto,
                             Authentication authentication) {

        if (authentication == null) return "redirect:/login";

        User currentUser = userService.findByEmail(authentication.getName());

        if (!isAdminOrReceptionist(currentUser)) {
            return "redirect:/dashboard";
        }

        userService.updateUser(id, dto);

        return "redirect:/users";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id,
                             Authentication authentication) {

        if (authentication == null) return "redirect:/login";

        User currentUser = userService.findByEmail(authentication.getName());

        if (!isAdminOrReceptionist(currentUser)) {
            return "redirect:/dashboard";
        }

        userService.deleteUser(id);

        return "redirect:/users";
    }
}