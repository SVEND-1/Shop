package org.example.myshop.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.myshop.entity.Cart;
import org.example.myshop.entity.Product;
import org.example.myshop.entity.User;
import org.example.myshop.repository.CartRepository;
import org.example.myshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CartService {

    private final CartRepository cartRepository;

    @Autowired
    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public Cart getCartByUserId(Long userId)  {
        return cartRepository.getCartByUserId((userId));
    };

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


    public Cart clearCartByUserId(Cart cart)  {
        cart.clearCart();
        return cartRepository.save(cart);
    }

    public Cart cartAddProduct(Cart cart, Product product) {
        cart.addCartItem(product);
        return cartRepository.save(cart);
    }

    public Cart cartRemoveProduct(Cart cart, Product product) {
        cart.removeCartItemByProduct(product);
        return cartRepository.save(cart);
    }
}
