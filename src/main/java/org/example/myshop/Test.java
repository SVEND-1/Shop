package org.example.myshop;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RestController
public class Test {


    @GetMapping(value = "/forgot-password", produces = MediaType.TEXT_HTML_VALUE)
    public String forgotPasswordPage() throws IOException {
        return readHtmlFile("static/html/forgot-password.html");
    }

    @GetMapping(value = "/reset-password", produces = MediaType.TEXT_HTML_VALUE)
    public String resetPasswordPage() throws IOException {
        return readHtmlFile("static/html/reset-password.html");
    }

    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public String homePage() throws IOException {
        return readHtmlFile("static/html/index.html");
    }

    @GetMapping(value = "/products", produces = MediaType.TEXT_HTML_VALUE)
    public String productsPage() throws IOException {
        return readHtmlFile("static/html/products.html");
    }

    @GetMapping(value = "/product/{id}", produces = MediaType.TEXT_HTML_VALUE)
    public String productDetailPage(@PathVariable String id) throws IOException {
        return readHtmlFile("static/html/product-detail.html");
    }

    @GetMapping(value = "/cart", produces = MediaType.TEXT_HTML_VALUE)
    public String cartPage() throws IOException {
        return readHtmlFile("static/html/cart.html");
    }

    @GetMapping(value = "/profile", produces = MediaType.TEXT_HTML_VALUE)
    public String profilePage() throws IOException {
        return readHtmlFile("static/html/profile.html");
    }

    @GetMapping(value = "/checkout", produces = MediaType.TEXT_HTML_VALUE)
    public String checkoutPage() throws IOException {
        return readHtmlFile("static/html/checkout.html");
    }


    @PostMapping("/api/auth/forgot-password")
    public ResponseEntity<Map<String, Object>> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        Map<String, Object> response = new HashMap<>();

        if (request.email == null || request.email.isEmpty()) {
            response.put("success", false);
            response.put("message", "Email обязателен");
            return ResponseEntity.badRequest().body(response);
        }

        response.put("success", true);
        response.put("message", "Ссылка для восстановления отправлена на " + request.email);
        response.put("resetToken", "reset-token-" + System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/auth/reset-password")
    public ResponseEntity<Map<String, Object>> resetPassword(@RequestBody ResetPasswordRequest request) {
        Map<String, Object> response = new HashMap<>();

        if (request.newPassword.length() < 6) {
            response.put("success", false);
            response.put("message", "Пароль должен содержать минимум 6 символов");
            return ResponseEntity.badRequest().body(response);
        }

        if (!request.newPassword.equals(request.confirmPassword)) {
            response.put("success", false);
            response.put("message", "Пароли не совпадают");
            return ResponseEntity.badRequest().body(response);
        }

        response.put("success", true);
        response.put("message", "Пароль успешно изменен");
        return ResponseEntity.ok(response);
    }

    // API endpoints для магазина
    @GetMapping("/api/products")
    public ResponseEntity<Map<String, Object>> getProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") int page) {

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("products", getSampleProducts());
        response.put("totalPages", 5);
        response.put("currentPage", page);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/products/{id}")
    public ResponseEntity<Map<String, Object>> getProduct(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("product", getSampleProduct(id));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/cart")
    public ResponseEntity<Map<String, Object>> getCart() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("cartItems", getSampleCartItems());
        response.put("total", 29997);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/cart/add")
    public ResponseEntity<Map<String, Object>> addToCart(@RequestBody CartRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Товар добавлен в корзину");
        response.put("cartCount", 3);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/order/create")
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody OrderRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Заказ успешно создан");
        response.put("orderId", "order-" + System.currentTimeMillis());
        response.put("total", 30497);
        return ResponseEntity.ok(response);
    }

    private String readHtmlFile(String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        byte[] fileData = FileCopyUtils.copyToByteArray(resource.getInputStream());
        return new String(fileData, StandardCharsets.UTF_8);
    }

    private Map<String, Object> createUserData() {
        Map<String, Object> user = new HashMap<>();
        user.put("id", "user-123");
        user.put("name", "Иван Иванов");
        user.put("email", "ivan@mail.ru");
        user.put("phone", "+7 (999) 999-99-99");
        return user;
    }

    private Map<String, Object> getSampleProduct(String id) {
        Map<String, Object> product = new HashMap<>();
        product.put("id", id);
        product.put("name", "Смартфон Samsung Galaxy S23");
        product.put("price", 79990);
        product.put("oldPrice", 89990);
        product.put("description", "Мощный смартфон с лучшей камерой на рынке");
        product.put("category", "Электроника");
        product.put("brand", "Samsung");
        product.put("rating", 4.8);
        product.put("reviews", 128);
        product.put("inStock", true);
        product.put("images", new String[]{"product1.jpg", "product2.jpg", "product3.jpg"});
        return product;
    }

    private Map<String, Object>[] getSampleProducts() {
        return new Map[]{
                createProduct("1", "Смартфон Samsung", 79990, 89990, "samsung.jpg"),
                createProduct("2", "Ноутбук ASUS", 59990, 69990, "asus.jpg"),
                createProduct("3", "Наушники Sony", 12990, 15990, "sony.jpg"),
                createProduct("4", "Часы Apple Watch", 29990, 34990, "apple.jpg")
        };
    }

    private Map<String, Object> createProduct(String id, String name, int price, int oldPrice, String image) {
        Map<String, Object> product = new HashMap<>();
        product.put("id", id);
        product.put("name", name);
        product.put("price", price);
        product.put("oldPrice", oldPrice);
        product.put("image", image);
        product.put("rating", 4.5 + Math.random() * 0.5);
        product.put("category", "Электроника");
        return product;
    }

    private Map<String, Object>[] getSampleCartItems() {
        return new Map[]{
                createCartItem("1", "Смартфон Samsung", 79990, 2, "samsung.jpg"),
                createCartItem("2", "Наушники Sony", 12990, 1, "sony.jpg")
        };
    }

    private Map<String, Object> createCartItem(String id, String name, int price, int quantity, String image) {
        Map<String, Object> item = new HashMap<>();
        item.put("id", id);
        item.put("name", name);
        item.put("price", price);
        item.put("quantity", quantity);
        item.put("image", image);
        item.put("total", price * quantity);
        return item;
    }

    public static class LoginRequest {
        public String email;
        public String password;
        public boolean remember;
    }

    public static class RegisterRequest {
        public String fullName;
        public String email;
        public String phone;
        public String password;
        public String confirmPassword;
        public boolean agree;
    }

    public static class ForgotPasswordRequest {
        public String email;
    }

    public static class ResetPasswordRequest {
        public String newPassword;
        public String confirmPassword;
    }

    public static class CartRequest {
        public String productId;
        public int quantity;
    }

    public static class OrderRequest {
        public String fullName;
        public String email;
        public String phone;
        public String address;
        public String city;
        public String paymentMethod;
        public Map<String, Integer> items;
    }
}

