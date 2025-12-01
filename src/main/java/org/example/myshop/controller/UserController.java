package org.example.myshop.controller;

import org.example.myshop.entity.Order;
import org.example.myshop.entity.User;
import org.example.myshop.entity.dto.UserDTO;
import org.example.myshop.service.OrderService;
import org.example.myshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final OrderService orderService;

    @Autowired
    public UserController(UserService userService, OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    @GetMapping("/profile")
    public String profilePage(Model model) {
        User user = userService.getCurrentUser();
        UserDTO userDTO = user.UserToUserDTO(user);
        model.addAttribute("user", userDTO);

        List<Order> userOrders = orderService.getOrdersByUserId(user.getId());
        model.addAttribute("userOrders", userOrders);
        return "profile";
    }


    @PostMapping("/profile/update-address")
    public String updateAddress(@RequestParam String newAddress) {
        User user = userService.getCurrentUser();
        user.setAddress(newAddress);
        userService.update(user.getId(), user);
        return "redirect:/user/profile";
    }
}