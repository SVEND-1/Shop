package org.example.myshop.controller;

import org.example.myshop.entity.User;
import org.example.myshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String adminPage(Model model) {
        List<User> users = userService.getAll();
        model.addAttribute("users", users);
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

    @PostMapping("/downgrade-courier")
    public String downgradeCourier(@RequestParam Long userId) {
        User user = userService.downgrade(userId, User.Role.COURIER);
        if(user.getRole().name().equals(User.Role.USER.name())) {
            return "redirect:/admin?success=true";
        }
        else {
            return "redirect:/admin?success=false";
        }
    }

    @PostMapping("/downgrade-seller")
    public String downgradeSeller(@RequestParam Long userId) {
        User user = userService.downgrade(userId, User.Role.SELLER);
        if(user.getRole().name().equals(User.Role.USER.name())) {
            return "redirect:/admin?success=true";
        }
        else {
            return "redirect:/admin?success=false";
        }
    }


}
