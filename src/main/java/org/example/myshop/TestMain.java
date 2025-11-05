package org.example.myshop;

import org.example.myshop.entity.Cart;
import org.example.myshop.entity.Order;
import org.example.myshop.entity.Product;
import org.example.myshop.entity.User;
import org.example.myshop.service.CartService;
import org.example.myshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import org.example.myshop.entity.*;
import org.example.myshop.service.*;
import org.springframework.boot.CommandLineRunner;

import java.util.List;
import java.util.Scanner;

@Component
public class TestMain {
    private User user;
    private Cart cart;
    private Product product;
    private Order order;
    private final UserService userService;
    private final CartService cartService;
    private final ProductService productService;
    private final OrderService orderService;


    @Autowired
    public TestMain(UserService userService, CartService cartService,
                    ProductService productService, OrderService orderService) {
        this.userService = userService;
        this.cartService = cartService;
        this.productService = productService;
        this.orderService = orderService;

        // ПЕРЕНЕСИТЕ инициализацию продукта в метод run или создайте правильно
        initializeProduct();
    }

    private void initializeProduct() {
        try {
            // Сначала проверяем, есть ли уже товары в базе
            List<Product> existingProducts = productService.findAll();
            enter();
            if (existingProducts.isEmpty()) {
                product = new Product();
                product.setName("Тестовый продукт");
                product.setDescription("Описание тестового продукта");
                product.setPrice(100.0);
                product.setCount(10); // или setStockQuantity() в зависимости от вашего класса
                product.setCategory(Product.Category.OTHER);

                // Сохраняем продукт
                product = productService.create(product,user.getId()); // Нужен пользователь с правами админа
            } else {
                product = existingProducts.get(0);
            }
        } catch (Exception e) {
            System.err.println("Ошибка инициализации продукта: " + e.getMessage());
        }
    }


    public void run(String... args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("=== ТЕСТОВАЯ СИСТЕМА МАГАЗИНА ===");

        while (running) {
            printMenu();
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> createUserAndCart();
                case "2" -> addProductToCart();
                case "3" -> viewCart();
                case "4" -> deleteProductFromCart();
                case "5" -> createOrder();
                case "6" -> viewOrders();
                case "7" -> viewUserInfo();
                case "8" -> addProduct();
                case "9" -> enter();
                case "0" -> {
                    running = false;
                    System.out.println("Завершение работы...");
                }
                default -> System.out.println("Неверный выбор!");
            }
        }
        scanner.close();
    }

    private void printMenu() {
        System.out.println("\n=== ТЕСТОВОЕ МЕНЮ ===");
        System.out.println("1. Создать пользователя и корзину");
        System.out.println("2. Добавить товар в корзину");
        System.out.println("3. Просмотреть корзину");
        System.out.println("4. Удалить товар из корзины");
        System.out.println("5. Создать заказ");
        System.out.println("6. Просмотреть заказы");
        System.out.println("7. Информация о пользователе");
        System.out.println("8. Добавить продукт");
        System.out.println("9. Вход в аккаунт");
        System.out.println("0. Выход");
        System.out.print("Выберите действие: ");
    }

    private void addProduct(){
        Scanner scanner = new Scanner(System.in);

        Product newProduct = new Product();
        newProduct.setName(scanner.nextLine());
        newProduct.setCount(100);
        newProduct.setPrice(100.0);

        productService.create(newProduct,user.getId());
    }

    private void createUserAndCart() {
        try {
            user = new User();
            user.setEmail("test10@example.com");
            user.setName("Тестовый Пользователь");
            user.setPassword("password123");
            user.setRole(User.Role.ADMIN);
            user.setAddress("ул. Тестовая, д. 123");

            User savedUser = userService.create(user);
            this.user = savedUser;

            // СОЗДАЕМ КОРЗИНУ И СОХРАНЯЕМ ЕЕ
            cart = new Cart();
            cart.setUser(savedUser);

            // Используем правильный метод для сохранения корзины
            Cart savedCart = cartService.create(cart); // или cartService.create(cart)
            this.cart = savedCart;

            System.out.println("✅ Пользователь и корзина созданы успешно!");
            System.out.println("ID пользователя: " + savedUser.getId());
            System.out.println("ID корзины: " + savedCart.getId());

        } catch (Exception e) {
            System.out.println("❌ Ошибка при создании пользователя и корзины: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void enter(){
        user = userService.getById(2L);
        cart = cartService.getById(2L);
    }

    private void addProductToCart() {
        try {
            if (user == null) {
                System.out.println("❌ Сначала создайте пользователя (пункт 1)");
                return;
            }

            // ПОЛУЧАЕМ АКТУАЛЬНУЮ КОРЗИНУ ИЗ БАЗЫ ДАННЫХ
            Cart currentCart = cartService.getCartByUserId(user.getId());
            if (currentCart == null) {
                System.out.println("❌ Корзина не найдена. Сначала создайте пользователя и корзину (пункт 1)");
                return;
            }

            List<Product> products = productService.findAll();

            if (products.isEmpty()) {
                System.out.println("❌ В магазине нет товаров.");
                return;
            }

            // Показываем список товаров для выбора
            System.out.println("\n=== ВЫБЕРИТЕ ТОВАР ===");
            for (int i = 0; i < products.size(); i++) {
                Product p = products.get(i);
                System.out.printf("%d. %s - $%.2f (в наличии: %d)%n",
                        i + 1, p.getName(), p.getPrice(), p.getCount());
            }

            Scanner scanner = new Scanner(System.in);
            System.out.print("Выберите номер товара: ");
            int choice = Integer.parseInt(scanner.nextLine()) - 1;

            if (choice < 0 || choice >= products.size()) {
                System.out.println("❌ Неверный выбор товара");
                return;
            }

            System.out.print("Введите количество: ");
            int quantity = Integer.parseInt(scanner.nextLine());

            Product selectedProduct = products.get(choice);

            cartService.cartAddProduct(cart, selectedProduct,quantity);

            System.out.println("✅ Товар добавлен в корзину: " + selectedProduct.getName());
            System.out.println("Количество: " + quantity);

            // ОБНОВЛЯЕМ ЛОКАЛЬНУЮ ПЕРЕМЕННУЮ CART
            this.cart = cartService.getCartByUserId(user.getId());

        } catch (NumberFormatException e) {
            System.out.println("❌ Ошибка: введите корректное число");
        } catch (Exception e) {
            System.out.println("❌ Ошибка при добавлении товара в корзину: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void viewCart() {
        try {
            if (user == null) {
                System.out.println("❌ Сначала создайте пользователя (пункт 1)");
                return;
            }

            Cart currentCart = cartService.getCartByUserId(user.getId());
            if (currentCart == null || currentCart.getCartItems() == null || currentCart.getCartItems().isEmpty()) {
                System.out.println("🛒 Корзина пуста");
                return;
            }

            System.out.println("\n=== СОДЕРЖИМОЕ КОРЗИНЫ ===");
            System.out.printf("%-20s %-10s %-10s %-10s%n",
                    "Товар", "Цена", "Кол-во", "Сумма");
            System.out.println("------------------------------------------------");

            double total = 0;
            for (CartItem item : currentCart.getCartItems()) {
                double itemTotal = item.getProduct().getPrice() * item.getQuantity();
                total += itemTotal;

                System.out.printf("%-20s $%-9.2f %-10d $%-9.2f%n",
                        item.getProduct().getName(),
                        item.getProduct().getPrice(),
                        item.getQuantity(),
                        itemTotal);
            }

            System.out.println("------------------------------------------------");
            System.out.printf("ИТОГО: $%.2f%n", total);

        } catch (Exception e) {
            System.out.println("❌ Ошибка при просмотре корзины: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteProductFromCart() {
        try {
            if (user == null) {
                System.out.println("❌ Сначала создайте пользователя (пункт 1)");
                return;
            }

            // Получаем актуальную корзину из базы
            Cart currentCart = cartService.getCartByUserId(user.getId());
            if (currentCart == null || currentCart.getCartItems().isEmpty()) {
                System.out.println("❌ Корзина уже пуста");
                return;
            }

            // Показываем список товаров для удаления
            System.out.println("\n=== ВЫБЕРИТЕ ТОВАР ДЛЯ УДАЛЕНИЯ ===");
            List<CartItem> cartItems = currentCart.getCartItems();

            for (int i = 0; i < cartItems.size(); i++) {
                CartItem item = cartItems.get(i);
                Product product = item.getProduct();
                double totalPrice = product.getPrice() * item.getQuantity();

                System.out.printf("%d. %s - %d шт. - $%.2f (всего: $%.2f)%n",
                        i + 1,
                        product.getName(),
                        item.getQuantity(),
                        product.getPrice(),
                        totalPrice);
            }

            System.out.print("Выберите номер товара для удаления: ");
            Scanner scanner = new Scanner(System.in);
            int choice = Integer.parseInt(scanner.nextLine()) - 1;

            if (choice < 0 || choice >= cartItems.size()) {
                System.out.println("❌ Неверный выбор товара");
                return;
            }

            CartItem itemToRemove = cartItems.get(choice);
            Long productId = itemToRemove.getProduct().getId();
            String productName = itemToRemove.getProduct().getName();

            // Удаляем товар из корзины
            cartService.cartRemoveProduct(user.getId(), productId);

            System.out.println("✅ Товар удален из корзины: " + productName);

            // Обновляем локальную ссылку
            this.cart = cartService.getCartByUserId(user.getId());

        } catch (NumberFormatException e) {
            System.out.println("❌ Ошибка: введите корректное число");
        } catch (Exception e) {
            System.out.println("❌ Ошибка при удалении товара из корзины: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createOrder() {
        try {
            if (user == null) {
                System.out.println("❌ Сначала создайте пользователя (пункт 1)");
                return;
            }

            Cart currentCart = cartService.getCartByUserId(user.getId());
            if (currentCart.getCartItems().isEmpty()) {
                System.out.println("❌ Корзина пуста, нечего заказывать");
                return;
            }

            // Создаем заказ через сервис
            Order newOrder = orderService.createOrderFromCart(user.getId());

            this.order = newOrder;

            System.out.println("✅ Заказ успешно создан!");
            System.out.println("Номер заказа: " + newOrder.getId());
            System.out.println("Статус: " + newOrder.getStatus());
            System.out.println("Общая сумма: $" + newOrder.getTotalAmount());

        } catch (Exception e) {
            System.out.println("❌ Ошибка при создании заказа: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void viewOrders() {
        try {
            if (user == null) {
                System.out.println("❌ Сначала создайте пользователя (пункт 1)");
                return;
            }

            var orders = orderService.getOrdersByUserId(user.getId());
            if (orders.isEmpty()) {
                System.out.println("📦 У пользователя нет заказов");
                return;
            }

            System.out.println("\n=== ЗАКАЗЫ ПОЛЬЗОВАТЕЛЯ ===");
            for (Order order : orders) {
                System.out.println("------------------------------------------------");
                System.out.println("Заказ #" + order.getId());
                System.out.println("Статус: " + order.getStatus());
                System.out.println("Общая сумма: $" + order.getTotalAmount());
                System.out.println("Товаров в заказе: " +
                        (order.getOrderItems() != null ? order.getOrderItems().size() : 0));
            }

        } catch (Exception e) {
            System.out.println("❌ Ошибка при просмотре заказов: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void viewUserInfo() {
        try {
            if (user == null) {
                System.out.println("❌ Сначала создайте пользователя (пункт 1)");
                return;
            }

            User currentUser = userService.getById(user.getId());
            System.out.println("\n=== ИНФОРМАЦИЯ О ПОЛЬЗОВАТЕЛЕ ===");
            System.out.println("ID: " + currentUser.getId());
            System.out.println("Имя: " + currentUser.getName());
            System.out.println("Email: " + currentUser.getEmail());
            System.out.println("Адрес: " + currentUser.getAddress());
            System.out.println("Роль: " + currentUser.getRole());

            Cart userCart = cartService.getCartByUserId(user.getId());
            System.out.println("Товаров в корзине: " +
                    (userCart != null && userCart.getCartItems() != null ? userCart.getCartItems().size() : 0));

            var orders = orderService.getOrdersByUserId(user.getId());
            System.out.println("Всего заказов: " + orders.size());

        } catch (Exception e) {
            System.out.println("❌ Ошибка при получении информации о пользователе: " + e.getMessage());
            e.printStackTrace();
        }
    }
}