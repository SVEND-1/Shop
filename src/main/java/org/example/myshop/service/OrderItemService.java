package org.example.myshop.service;

import javax.persistence.EntityNotFoundException;
import org.example.myshop.entity.*;
import org.example.myshop.repository.OrderItemRepository;
import org.example.myshop.repository.OrderRepository;
import org.example.myshop.repository.ProductRepository;
import org.example.myshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
@Transactional
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Autowired
    public OrderItemService(OrderItemRepository orderItemRepository, OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }
    public List<OrderItem> saveAll(List<OrderItem> orderItems) {
        if (orderItems == null || orderItems.isEmpty()) {
            System.out.println("⚠️  Передан пустой список OrderItem для сохранения");
            return new ArrayList<>();
        }

        System.out.println("💾 Сохранение " + orderItems.size() + " элементов заказа...");

        try {
            // Сохраняем все элементы
            List<OrderItem> savedItems = orderItemRepository.saveAll(orderItems);

            System.out.println("✅ Успешно сохранено " + savedItems.size() + " элементов заказа");

            return savedItems;

        } catch (Exception e) {
            System.err.println("❌ Ошибка при сохранении элементов заказа: " + e.getMessage());
            throw new RuntimeException("Не удалось сохранить элементы заказа", e);
        }
    }

    public OrderItem findById(Long id) {
        return orderItemRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("OrderItem не найден"));
    }

    public List<CartItem> findAllByOrderId(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    public OrderItem addItemToCart(Long orderId, Long productId, Integer quantity) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Заказ не найдена"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Продукт не найден"));

        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Количество должно быть больше 0");
        }

        Optional<OrderItem> existingOrderItem = orderItemRepository.findByOrderIdAndProductId(orderId, productId);

        if (!existingOrderItem.isPresent()) {
            OrderItem orderItem = existingOrderItem.get();
            orderItem.setQuantity(orderItem.getQuantity() + quantity);
            return orderItemRepository.save(orderItem);
        } else {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(quantity);

            return orderItemRepository.save(orderItem);
        }
    }

    public void removeItemFromOrder(Long orderItemId) {
        if (!orderItemRepository.existsById(orderItemId)) {
            throw new EntityNotFoundException("OrderItem не найден");
        }

        orderItemRepository.deleteById(orderItemId);
    }

    public void removeItemFromCart(Long cartId, Long productId) {
        if (!orderRepository.existsById(cartId)) {
            throw new EntityNotFoundException("Заказ не найден");
        }

        if (!productRepository.existsById(productId)) {
            throw new EntityNotFoundException("продукт не найден");
        }

        orderItemRepository.deleteByOrderIdAndProductId(cartId, productId);
    }

}
