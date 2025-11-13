package org.example.myshop.service;

import javax.persistence.EntityNotFoundException;
import org.example.myshop.entity.Cart;
import org.example.myshop.entity.CartItem;
import org.example.myshop.entity.Product;
import org.example.myshop.repository.CartRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final ProductService productService;
    private final CartItemService cartItemService;

    @Autowired
    public CartService(CartRepository cartRepository, ProductService productService, CartItemService cartItemService) {
        this.cartRepository = cartRepository;
        this.productService = productService;
        this.cartItemService = cartItemService;
    }


    public Cart getCartByUserId(Long userId) {
        Cart cart = cartRepository.findByUserId(userId);
        if (cart != null) {
            Hibernate.initialize(cart.getCartItems());
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
        Cart cart = cartRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Корзина не найдена"));
        Cart updatedCart = new Cart(
                cart.getId(),
                cartToUpdate.getUser(),
                cartToUpdate.getCartItems(),
                cartToUpdate.getTotalPrice());
        return cartRepository.save(updatedCart);
    }

    public void deleted(Long id) {
        if(!cartRepository.existsById(id)){
            throw new NoSuchElementException("Корзина не найдена");
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
    public Cart cartAddProduct(Long cartId, Long productId) {
        Cart cart = getById(cartId);
        cartItemService.addItemToCart(cartId,productId,1);
        return cartRepository.save(cart);
    }



    public void cartRemoveProduct(Long userId, Long productId) {
        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null) {
            throw new RuntimeException("Корзина не найдена для пользователя: " + userId);
        }

        if (cart.getCartItems() == null) {
            return;
        }

        boolean removed = cart.getCartItems().removeIf(item ->
                item.getProduct() != null &&
                        item.getProduct().getId().equals(productId)
        );

        if (removed) {
            cartRepository.save(cart);
            System.out.println("Товар с ID " + productId + " удален из корзины");
        } else {
            System.out.println("Товар с ID " + productId + " не найден в корзине");
        }
    }
}
