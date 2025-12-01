package org.example.myshop.controller;

import org.example.myshop.entity.Cart;
import org.example.myshop.entity.Product;
import org.example.myshop.entity.User;
import org.example.myshop.service.CartService;
import org.example.myshop.service.ProductService;
import org.example.myshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

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
    public String CatalogPage(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Model model) {

        Page<Product> productsPage;
        List<Product> availableProducts;

        if (category != null && !category.isEmpty()) {
            try {
                Product.Category productCategory = Product.Category.valueOf(category.toUpperCase());
                productsPage = productService.getProductsByCategoryWithPagination(productCategory, page, size);
                model.addAttribute("selectedCategory", productCategory);
            } catch (IllegalArgumentException e) {
                productsPage = productService.getAvailableProductsWithPagination(page, size);
                model.addAttribute("errorMessage", "Категория не найдена");
            }
        } else {
            productsPage = productService.getAvailableProductsWithPagination(page, size);
        }

        availableProducts = productsPage.getContent().stream()
                .filter(product -> product.getCount() > 0)
                .collect(Collectors.toList());

        model.addAttribute("products", availableProducts);
        model.addAttribute("categories", Product.Category.values());

        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", productsPage.getTotalPages());
        model.addAttribute("totalItems", productsPage.getTotalElements());
        model.addAttribute("hasNext", productsPage.hasNext());
        model.addAttribute("hasPrevious", productsPage.hasPrevious());

        return "catalog";
    }

    @PostMapping("/catalog/add")
    public String addProductToCart(@RequestParam Long productId) {
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