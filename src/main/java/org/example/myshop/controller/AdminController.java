package org.example.myshop.controller;

import org.example.myshop.entity.User;
import org.example.myshop.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String adminPage() {
        return "admin";
    }

    @PostMapping("/appoint-courier")
    public String appointCourier(@RequestParam Long userId) {
        User user = userService.appoint(userId, User.Role.COURIER);
        if(user.getRole().name().equals(User.Role.COURIER.name())) {
            return "redirect:/admin?success=true";
        }
        else {
            return "redirect:/admin?success=false";
        }
    }

    @PostMapping("/appoint-seller")
    public String appointSeller(@RequestParam Long userId) {
        User user = userService.appoint(userId, User.Role.SELLER);
        if(user.getRole().name().equals(User.Role.COURIER.name())) {
            return "redirect:/admin?success=true";
        }
        else {
            return "redirect:/admin?success=false";
        }
    }
}
