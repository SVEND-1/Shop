package org.example.myshop.service;

import org.example.myshop.entity.Order;
import org.example.myshop.entity.User;
import org.example.myshop.repository.OrderRepository;
import org.example.myshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final OrderService orderService;

    @Autowired
    public UserService(UserRepository userRepository, OrderService orderService) {
        this.userRepository = userRepository;
        this.orderService = orderService;
    }

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();//Вернет логин
        User user =  userRepository.findByEmailEqualsIgnoreCase(email);
        if(user == null) {
            throw new IllegalArgumentException("Не найден пользователь");
        }
        return user;
    }

    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
    }

    public User appoint(Long userId, User.Role role) {
        User user = getById(userId);
        user.setRole(role);
        return userRepository.save(user);
    }

    public User getByEmail(String email) {
        return userRepository.findByEmailEqualsIgnoreCase(email);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User create(User userToCreate) {
        try {
            return userRepository.save(userToCreate);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Пользователь с email " + userToCreate.getEmail() + " уже существует");
        }
    }

    public User update(Long id, User userToUpdate) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
        User updatedUser = new User(
                user.getId(),
                userToUpdate.getEmail(),
                userToUpdate.getName(),
                userToUpdate.getPassword(),
                userToUpdate.getRole(),
                userToUpdate.getAddress(),
                userToUpdate.getOrders(),
                userToUpdate.getCart());
        return userRepository.save(updatedUser);
    }

    public void deleted(Long id) {
        if(!userRepository.existsById(id)){
            throw new NoSuchElementException("Пользователь не найден");
        }
        userRepository.deleteById(id);
    }

    public User userAddOrder(User user, Order order) {
        user.addOrderItem(order);
        return userRepository.save(user);
    }

    public User userAddOrder(Long userId, Long orderId) {
        User user = getById(userId);
        Order order = orderService.getById(orderId);
        user.addOrderItem(order);
        return userRepository.save(user);
    }

    public User userRemoveOrder(Long userId, Long orderId) {
        User user = getById(userId);
        Order order = orderService.getById(orderId);
        user.removeOrderItem(order);
        return userRepository.save(user);
    }
}
