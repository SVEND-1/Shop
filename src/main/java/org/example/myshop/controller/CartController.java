package org.example.myshop.controller;

import org.example.myshop.entity.Cart;
import org.example.myshop.entity.CartItem;
import org.example.myshop.service.CartItemService;
import org.example.myshop.service.CartService;
import org.example.myshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;
    private final UserService userService;
    private final CartItemService cartItemService;

    @Autowired
    public CartController(CartService cartService, UserService userService, CartItemService cartItemService) {
        this.cartService = cartService;
        this.userService = userService;
        this.cartItemService = cartItemService;
    }

    @GetMapping()
    public String cartPage(Model model) {
        Cart cart = cartService.getCartByUserId(userService.getCurrentUser().getId());
        List<CartItem> cartItems = cartItemService.findAllByCartId(cart.getId());
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", cart.totalPrice());
        model.addAttribute("totalItems", cart.getQuantity());
        return "cart";
    }

    @PostMapping("/deleted")
    public String deleteProductFromCart(@RequestParam Long cartItemId) {
        cartItemService.removeItemFromCart(cartItemId);
        return "redirect:/cart";
    }

    @PostMapping("/quantity-reduce")
    public String productQuantityReduce(@RequestParam Long cartItemId, @RequestParam int quantity) {//TODO нормально обработать если 1 товар всего
        cartItemService.updateQuantity(cartItemId, quantity - 1);
        return "redirect:/cart";
    }

    @PostMapping("/quantity-increase")
    private String productQuantityIncrease(@RequestParam Long cartItemId, @RequestParam int quantity) {
        cartItemService.updateQuantity(cartItemId, quantity + 1);
        return "redirect:/cart";
    }
}