package org.example.myshop.controller;

import org.example.myshop.entity.Product;
import org.example.myshop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Controller
@RequestMapping("/seller")
public class SellerController {
    private final ProductService productService;

    @Autowired
    public SellerController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/add")
    public String AddProductPage(Model model) {//TODO Переписать под другой фронтент
        model.addAttribute("product", new Product());
        model.addAttribute("categories", Product.Category.values());
        return "add-product";
    }

    @PostMapping("/add")
    public String addProduct(@ModelAttribute Product product,
                             @RequestParam("imageFile") MultipartFile imageFile) {
        try {
            if (!imageFile.isEmpty()) {
                String imageName = saveImage(imageFile);
                product.setImage(imageName);
            }
            productService.create(product);
            return "redirect:/";
        } catch (Exception e) {
            return "redirect:/seller/add";
        }
    }

    //В базе сохраняется только путь,а на сервере будет сохраняться image
    private String saveImage(MultipartFile imageFile) throws IOException {
        String originalFileName = imageFile.getOriginalFilename();
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + fileExtension;

        Path uploadPath = Paths.get("uploads/images");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(fileName);
        Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }
}
