package org.example.myshop.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.example.myshop.entity.Cart;
import org.example.myshop.entity.CartItem;
import org.example.myshop.entity.Product;
import org.example.myshop.entity.User;
import org.example.myshop.repository.CartRepository;
import org.example.myshop.repository.UserRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class CartService {

    private final CartRepository cartRepository;

    @Autowired
    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

//    public Cart getCartByUserId(Long userId)  {
//        return cartRepository.getCartByUserId((userId));
//    };

    public Cart getCartByUserId(Long userId) {
        Cart cart = cartRepository.findByUserId(userId);
        if (cart != null) {
            // Инициализируем коллекцию
            Hibernate.initialize(cart.getCartItems());
            // Или для каждой коллекции внутри items
            for (CartItem item : cart.getCartItems()) {
                Hibernate.initialize(item.getProduct());
            }
        }
        return cart;
    }

    public Cart getById(Long id)  {
        return cartRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("не найден"));
    }

    public Cart create(Cart cartToCreate) {
        return cartRepository.save(cartToCreate);
    }

    public Cart update(Long id,Cart cartToUpdate) {
        Cart cart = cartRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("не найден"));
        Cart updatedCart = new Cart(
                cart.getId(),
                cartToUpdate.getUser(),
                cartToUpdate.getCartItems(),
                cartToUpdate.getTotalPrice());
        return cartRepository.save(updatedCart);
    }

    public void deleted(Long id) {
        if(!cartRepository.existsById(id)){
            throw new NoSuchElementException("не найден");
        }
        cartRepository.deleteById(id);
    }


    public Cart clearCartByUserId(Long userID)  {
        Cart cart = cartRepository.findByUserId(userID);
        cart.clearCart();
        return cartRepository.save(cart);
    }

    public Cart cartAddProduct(Cart cart, Product product,int quantity) {
        cart.addCartItem(product,quantity);
        return cartRepository.save(cart);
    }
    public Cart cartAddProduct(Cart cart, Product product) {
        cart.addCartItem(product,1);
        return cartRepository.save(cart);
    }

    public void cartRemoveProduct(Long userId, Long productId) {
        // Получаем корзину с загруженными элементами
        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null) {
            throw new RuntimeException("Корзина не найдена для пользователя: " + userId);
        }

        // Убедитесь, что коллекция инициализирована
        if (cart.getCartItems() == null) {
            return; // Нет товаров для удаления
        }

        // Удаляем товар
        boolean removed = cart.getCartItems().removeIf(item ->
                item.getProduct() != null &&
                        item.getProduct().getId().equals(productId)
        );

        if (removed) {
            cartRepository.save(cart); // Сохраняем изменения
            System.out.println("Товар с ID " + productId + " удален из корзины");
        } else {
            System.out.println("Товар с ID " + productId + " не найден в корзине");
        }
    }
}
