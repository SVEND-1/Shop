package org.example.myshop.controller;

import org.example.myshop.entity.Cart;
import org.example.myshop.entity.CartItem;
import org.example.myshop.entity.User;
import org.example.myshop.service.CartItemService;
import org.example.myshop.service.CartService;
import org.example.myshop.service.OrderService;
import org.example.myshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;
    private final CartService cartService;
    private final CartItemService cartItemService;

    @Autowired
    public OrderController(OrderService orderService, UserService userService, CartService cartService, CartItemService cartItemService) {
        this.orderService = orderService;
        this.userService = userService;
        this.cartService = cartService;
        this.cartItemService = cartItemService;
    }

    @GetMapping
    public String orderPage(Model model) {
        User user = userService.getCurrentUser();
        Cart cart = cartService.getCartByUserId(userService.getCurrentUser().getId());
        List<CartItem> cartItems = cartItemService.findAllByCartId(cart.getId());
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", cart.totalPrice());
        model.addAttribute("totalItems", cart.getQuantity());
        model.addAttribute("user", user);
        return "checkout";
    }

    @PostMapping("/create")
    public String createOrder() {
        User user = userService.getCurrentUser();
        orderService.createOrderFromCart(user.getId());
        return "redirect:/cart";
    }
}
