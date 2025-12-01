package org.example.myshop.controller;

import org.aspectj.weaver.ast.Or;
import org.example.myshop.entity.Order;
import org.example.myshop.entity.User;
import org.example.myshop.service.OrderService;
import org.example.myshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/courier")
public class CourierController {
    private OrderService orderService;
    private UserService userService;

    @Autowired
    public CourierController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @GetMapping()
    public String courierDashboard(Model model) {
        try {
            User courier = userService.getCurrentUser();

            model.addAttribute("courier", courier);

            List<Order> allOrders = orderService.getAll();
            model.addAttribute("allOrders", allOrders);

            List<Order> assignedOrders = allOrders.stream()
                    .filter(order -> order.getCourier() != null && order.getCourier().getId().equals(courier.getId()))
                    .collect(Collectors.toList());
            model.addAttribute("assignedOrders", assignedOrders);

            List<Order> availableOrders = allOrders.stream()
                    .filter(order -> order.getCourier() == null)
                    .collect(Collectors.toList());
            model.addAttribute("availableOrders", availableOrders);

        } catch (Exception e) {
            model.addAttribute("allOrders", new ArrayList<Order>());
            model.addAttribute("assignedOrders", new ArrayList<Order>());
            model.addAttribute("availableOrders", new ArrayList<Order>());
        }

        return "courier";
    }

    @PostMapping("/orders/{id}/assign")
    public String assignOrder(@PathVariable Long id) {//Проверить что у курьера нету активыных заказов
        User courier = userService.getCurrentUser();
        Order order = orderService.getById(id);
        order.setCourier(courier);
        orderService.update(order.getId(),order);
        return "redirect:/courier";
    }

    @PostMapping("/orders/{id}/status")
    public String setStatus(@PathVariable Long id, @RequestParam("status") Order.OrderStatus status) {
        Order order = orderService.getById(id);
        orderService.setStatus(order, status);
        return "redirect:/courier";
    }
}