package org.example.myshop.controller;

import org.example.myshop.entity.Cart;
import org.example.myshop.entity.Product;
import org.example.myshop.entity.User;
import org.example.myshop.service.CartService;
import org.example.myshop.service.ProductService;
import org.example.myshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {

    private final ProductService productService;
    private final UserService userService;
    private final CartService cartService;

    @Autowired
    public HomeController(ProductService productService, UserService userService, CartService cartService) {
        this.productService = productService;
        this.userService = userService;
        this.cartService = cartService;
    }

    @GetMapping("/")
    public String homePage(Model model) {
        List<Product> featuredProducts = productService.find48Product();
        model.addAttribute("featuredProducts", featuredProducts);
        return "index";
    }

    @GetMapping("/home/search")
    public String search(@RequestParam String query, Model model) {
        List<Product> searchResults = productService.searchProducts(query.trim().toLowerCase());
        model.addAttribute("products", searchResults);
        model.addAttribute("searchQuery", query.trim().toLowerCase());
        return "catalog";
    }

    @PostMapping("/home/add")
    public String homeAddProductToCart(@RequestParam Long productId){
        User user = userService.getCurrentUser();
        Cart cart = cartService.getCartByUserId(user.getId());
        cartService.cartAddProduct(cart.getId(),productId);
        return "redirect:/";
    }
}