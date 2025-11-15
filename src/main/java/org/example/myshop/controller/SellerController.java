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
                             @RequestParam("imageFile") MultipartFile imageFile,
                             RedirectAttributes redirectAttributes) {
        try {
            User seller = userService.getCurrentUser();

            if (!imageFile.isEmpty()) {
                String imageName = saveImage(imageFile);
                product.setImage(imageName);
            }

            product.setSeller(seller);
            productService.create(product);

            redirectAttributes.addFlashAttribute("success", "Товар успешно добавлен!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка: " + e.getMessage());
        }
        return "redirect:/seller";
    }

    @PostMapping("/update")
    public String updateProduct(@RequestParam Long id,
                                @RequestParam String name,
                                @RequestParam Double price,
                                @RequestParam Integer count,
                                @RequestParam(required = false) String description,
                                @RequestParam Product.Category category,
                                @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                                RedirectAttributes redirectAttributes) {
        try {
            Product existingProduct = productService.getById(id);

            if (existingProduct == null) {
                redirectAttributes.addFlashAttribute("error", "Товар не найден");
                return "redirect:/seller";
            }

            existingProduct.setName(name);
            existingProduct.setPrice(price);
            existingProduct.setCount(count);
            existingProduct.setDescription(description);
            existingProduct.setCategory(category);

            if (imageFile != null && !imageFile.isEmpty()) {
                String imageName = saveImage(imageFile);
                existingProduct.setImage(imageName);
            }

            productService.update(existingProduct.getId(),existingProduct);
            redirectAttributes.addFlashAttribute("success", "Товар обновлен!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка обновления: " + e.getMessage());
        }
        return "redirect:/seller";
    }

    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleted(id);
            redirectAttributes.addFlashAttribute("success", "Товар удален!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка удаления: " + e.getMessage());
        }
        return "redirect:/seller";
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