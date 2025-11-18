package org.example.myshop.service;

import org.example.myshop.entity.Cart;
import org.example.myshop.entity.CartItem;
import org.example.myshop.entity.Product;
import org.example.myshop.entity.User;
import org.example.myshop.repository.CartRepository;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemService cartItemService;

    @InjectMocks
    private CartService cartService;

    private User testUser;
    private Cart testCart;
    private CartItem cartItem1;
    private CartItem cartItem2;
    private Product testProduct1;
    private Product testProduct2;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("user@mail.com");
        testUser.setName("Test User");

        testProduct1 = new Product();
        testProduct1.setId(1L);
        testProduct1.setName("iPhone 15");
        testProduct1.setPrice(999.99);
        testProduct1.setCount(10);

        testProduct2 = new Product();
        testProduct2.setId(2L);
        testProduct2.setName("MacBook Pro");
        testProduct2.setPrice(1999.99);
        testProduct2.setCount(5);

        cartItem1 = new CartItem();
        cartItem1.setId(1L);
        cartItem1.setProduct(testProduct1);
        cartItem1.setQuantity(2);

        cartItem2 = new CartItem();
        cartItem2.setId(2L);
        cartItem2.setProduct(testProduct2);
        cartItem2.setQuantity(1);

        testCart = new Cart();
        testCart.setId(1L);
        testCart.setUser(testUser);
        testCart.setCartItems(Arrays.asList(cartItem1, cartItem2));
        testCart.setTotalPrice(BigDecimal.valueOf(2999.97));
    }

    @Test
    void getCartByUserId() {
        Long userId = 1L;

        Cart cartWithItems = new Cart();
        cartWithItems.setId(1L);
        cartWithItems.setUser(testUser);
        cartWithItems.setCartItems(Arrays.asList(cartItem1, cartItem2));

        when(cartRepository.findByUserId(userId)).thenReturn(cartWithItems);

        Cart result = cartService.getCartByUserId(userId);

        assertNotNull(result);
        assertEquals(cartWithItems.getId(), result.getId());
        assertEquals(cartWithItems.getUser(), result.getUser());
        assertNotNull(result.getCartItems());
        assertEquals(2, result.getCartItems().size());
        verify(cartRepository, times(1)).findByUserId(userId);
    }

    @Test
    void getById() {
        Long cartId = 1L;
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(testCart));

        Cart result = cartService.getById(cartId);

        assertNotNull(result);
        assertEquals(testCart.getId(), result.getId());
        verify(cartRepository, times(1)).findById(cartId);
    }

    @Test
    void create() {
        when(cartRepository.save(testCart)).thenReturn(testCart);

        Cart result = cartService.create(testCart);

        assertNotNull(result);
        assertEquals(testCart.getId(), result.getId());
        verify(cartRepository, times(1)).save(testCart);
    }

    @Test
    void update() {
        Long cartId = 1L;
        Cart cartToUpdate = new Cart();
        cartToUpdate.setUser(testUser);
        cartToUpdate.setCartItems(Arrays.asList(cartItem1));
        cartToUpdate.setTotalPrice(BigDecimal.valueOf(999.99));

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(testCart));
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        Cart result = cartService.update(cartId, cartToUpdate);

        assertNotNull(result);
        verify(cartRepository, times(1)).findById(cartId);
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void deleted() {
        Long cartId = 1L;
        when(cartRepository.existsById(cartId)).thenReturn(true);
        doNothing().when(cartRepository).deleteById(cartId);

        cartService.deleted(cartId);

        verify(cartRepository, times(1)).existsById(cartId);
        verify(cartRepository, times(1)).deleteById(cartId);
    }

    @Test
    void clearCartByUserId() {
        Long userId = 1L;

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(testUser);
        cart.setCartItems(new ArrayList<>(Arrays.asList(cartItem1, cartItem2))); // mutable list

        when(cartRepository.findByUserId(userId)).thenReturn(cart);
        when(cartRepository.save(cart)).thenReturn(cart);

        Cart result = cartService.clearCartByUserId(userId);

        assertNotNull(result);
        verify(cartRepository, times(1)).findByUserId(userId);
        verify(cartRepository, times(1)).save(cart);
        assertTrue(result.getCartItems().isEmpty());
    }

    @Test
    void cartAddProduct() {
        Long cartId = 1L;
        Long productId = 1L;

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(testCart));
        when(cartItemService.addItemToCart(cartId, productId, 1)).thenReturn(cartItem1);
        when(cartRepository.save(testCart)).thenReturn(testCart);

        Cart result = cartService.cartAddProduct(cartId, productId);

        assertNotNull(result);
        verify(cartRepository, times(1)).findById(cartId);
        verify(cartItemService, times(1)).addItemToCart(cartId, productId, 1);
        verify(cartRepository, times(1)).save(testCart);
    }
}