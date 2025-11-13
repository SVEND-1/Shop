package org.example.myshop.controller;

import org.example.myshop.entity.Cart;
import org.example.myshop.entity.Product;
import org.example.myshop.entity.User;
import org.example.myshop.service.CartService;
import org.example.myshop.service.ProductService;
import org.example.myshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;
    private final UserService userService;
    private final CartService cartService;

    @Autowired
    public ProductController(ProductService productService, UserService userService, CartService cartService) {
        this.productService = productService;
        this.userService = userService;
        this.cartService = cartService;
    }

    @GetMapping("/catalog")
    public String CatalogPage(Model model) {
        List<Product> products = productService.findAll();
        model.addAttribute("products", products);
        return "catalog";
    }

    @PostMapping("/catalog/add")
    public String addProductToCart(@RequestParam Long productId) {//TODO многопоточность + проверки + если есть уже товар такой то просто добавлять 1
        User user = userService.getCurrentUser();
        Cart cart = cartService.getCartByUserId(user.getId());
        cartService.cartAddProduct(cart.getId(),productId);
        return "redirect:/product/catalog";
    }

    @GetMapping("/{id}")
    public String productDetailPage(@PathVariable String id, Model model)  {
        Product product = productService.getById(Long.parseLong(id));
        model.addAttribute("product", product);
        return "product-detail";
    }
}
