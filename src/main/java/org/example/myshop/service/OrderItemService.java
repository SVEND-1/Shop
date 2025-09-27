package org.example.myshop.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.myshop.entity.OrderItem;
import org.example.myshop.entity.User;
import org.example.myshop.repository.OrderItemRepository;
import org.example.myshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;

    public OrderItemService(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

}
