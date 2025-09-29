package org.example.myshop.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.myshop.entity.Cart;
import org.example.myshop.entity.Order;
import org.example.myshop.entity.User;
import org.example.myshop.repository.OrderRepository;
import org.example.myshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> findAllByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("пользователь с таким id не найден");
        }

        List<Order> orders = orderRepository.findAllByUserId((userId));

        if (orders.isEmpty()) {
            return Collections.emptyList();
        }

        return orders;
    }

    public Order findById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("не найден"));
    }

    public Order save(Order order) {
        return orderRepository.save(order);
    }

    public Order update(Long id, Order orderToUpdate) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("не найден"));
        Order updatedOrder = new Order(
                order.getId(),
                orderToUpdate.getUser(),
                orderToUpdate.getStatus(),
                orderToUpdate.getTotalAmount(),
                orderToUpdate.getOrderItems());
        return orderRepository.save(updatedOrder);
    }

    public void deleted(Long id) {
        if(!orderRepository.existsById(id)){
            throw new NoSuchElementException("не найден");
        }
        orderRepository.deleteById(id);
    }

    public Order updateStatus(Order order, Order.OrderStatus status) {
        order.setStatus(status);
        return orderRepository.save(order);
    }
}
