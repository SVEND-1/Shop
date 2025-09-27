package org.example.myshop.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.myshop.entity.User;
import org.example.myshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("не найден"));
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User createUser(User userToCreate) {
        return userRepository.save(userToCreate);
    }

    public User updateUser(Long id, User userToUpdate) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("не найден"));
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

    public void deletedUser(Long id) {
        if(!userRepository.existsById(id)){
            throw new NoSuchElementException("не найден");
        }
        userRepository.deleteById(id);
    }
}
