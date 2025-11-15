package org.example.myshop.controller;

import org.example.myshop.entity.Product;
import org.example.myshop.entity.User;
import org.example.myshop.service.ProductService;
import org.example.myshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/seller")
public class SellerController {
    private final ProductService productService;
    private final UserService userService;

    @Autowired
    public SellerController(ProductService productService, UserService userService) {
        this.productService = productService;
        this.userService = userService;
    }

    @GetMapping
    public String sellerPanel(Model model) {
        User seller = userService.getCurrentUser();
        List<Product> myProducts = productService.getProductsBySeller(seller.getId());

        model.addAttribute("myProducts", myProducts);
        model.addAttribute("categories", Product.Category.values());
        model.addAttribute("product", new Product());
        return "seller";
    }

    @PostMapping("/add")
    public String addProduct(@ModelAttribute Product product,
                             @RequestParam("imageFile") MultipartFile imageFile) {
        try {
            User seller = userService.getCurrentUser();

            if (!imageFile.isEmpty()) {
                String imageName = saveImage(imageFile);
                product.setImage(imageName);
            }

            product.setSeller(seller);
            productService.create(product);

            return "redirect:/seller";
        } catch (Exception e) {
            return "redirect:/seller";
        }
    }

    @PostMapping("/update")
    public String updateProduct(@RequestParam Long id,
                                @RequestParam String name,
                                @RequestParam Double price,
                                @RequestParam Integer count,
                                @RequestParam(required = false) String description,
                                @RequestParam Product.Category category,
                                @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        try {
            Product existingProduct = productService.getById(id);

            if (existingProduct == null) {
                return "redirect:/seller";
            }

            Product productToUpdate = new Product();
            productToUpdate.setName(name);
            productToUpdate.setPrice(price);
            productToUpdate.setCount(count);
            productToUpdate.setDescription(description);
            productToUpdate.setCategory(category);

            if (imageFile != null && !imageFile.isEmpty()) {
                String imageName = saveImage(imageFile);
                productToUpdate.setImage(imageName);
            } else {
                productToUpdate.setImage(existingProduct.getImage());
            }

            productService.update(id, productToUpdate);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/seller";
    }

    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        try {
            productService.deleted(id);
            return "redirect:/seller";
        } catch (Exception e) {
            return "redirect:/seller";
        }
    }

    private String saveImage(MultipartFile imageFile) throws IOException {
        String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
        Path uploadPath = Paths.get("uploads/images");

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(fileName);
        Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }
}