package org.example.myshop.service;

import org.example.myshop.entity.Cart;
import org.example.myshop.entity.CartItem;
import org.example.myshop.entity.Product;
import org.example.myshop.entity.User;
import org.example.myshop.repository.CartItemRepository;
import org.example.myshop.repository.CartRepository;
import org.example.myshop.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartItemServiceTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartItemService cartItemService;

    private User testUser;
    private Cart testCart;
    private Product testProduct1;
    private Product testProduct2;
    private CartItem cartItem1;
    private CartItem cartItem2;

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

        testCart = new Cart();
        testCart.setId(1L);
        testCart.setUser(testUser);

        cartItem1 = new CartItem();
        cartItem1.setId(1L);
        cartItem1.setCart(testCart);
        cartItem1.setProduct(testProduct1);
        cartItem1.setQuantity(2);
        cartItem1.setPrice(BigDecimal.valueOf(1999.98));

        cartItem2 = new CartItem();
        cartItem2.setId(2L);
        cartItem2.setCart(testCart);
        cartItem2.setProduct(testProduct2);
        cartItem2.setQuantity(1);
        cartItem2.setPrice(BigDecimal.valueOf(1999.99));
    }

    @Test
    void findById() {
        Long cartItemId = 1L;
        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.of(cartItem1));

        CartItem result = cartItemService.findById(cartItemId);

        assertNotNull(result);
        assertEquals(cartItem1.getId(), result.getId());
        assertEquals(cartItem1.getProduct(), result.getProduct());
        assertEquals(cartItem1.getQuantity(), result.getQuantity());
        verify(cartItemRepository, times(1)).findById(cartItemId);
    }

    @Test
    void findAllByCartId() {
        Long cartId = 1L;
        List<CartItem> cartItems = Arrays.asList(cartItem1, cartItem2);
        when(cartItemRepository.findAllByCartId(cartId)).thenReturn(cartItems);

        List<CartItem> result = cartItemService.findAllByCartId(cartId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(cartItem1));
        assertTrue(result.contains(cartItem2));
        verify(cartItemRepository, times(1)).findAllByCartId(cartId);
    }

    @Test
    void addItemToCart() {
        Long cartId = 1L;
        Long productId = 1L;
        Integer quantity = 2;

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(testCart));
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct1));
        when(cartItemRepository.findByCartIdAndProductId(cartId, productId)).thenReturn(Optional.empty());
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(cartItem1);

        CartItem result = cartItemService.addItemToCart(cartId, productId, quantity);

        assertNotNull(result);
        verify(cartRepository, times(1)).findById(cartId);
        verify(productRepository, times(1)).findById(productId);
        verify(cartItemRepository, times(1)).findByCartIdAndProductId(cartId, productId);
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    void updateQuantity() {
        Long cartItemId = 1L;
        Integer newQuantity = 5;

        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.of(cartItem1));
        when(cartItemRepository.save(cartItem1)).thenReturn(cartItem1);

        CartItem result = cartItemService.updateQuantity(cartItemId, newQuantity);

        assertNotNull(result);
        assertEquals(newQuantity, result.getQuantity());
        verify(cartItemRepository, times(1)).findById(cartItemId);
        verify(cartItemRepository, times(1)).save(cartItem1);
    }

    @Test
    void removeItemFromCart() {
        Long cartItemId = 1L;
        when(cartItemRepository.existsById(cartItemId)).thenReturn(true);
        doNothing().when(cartItemRepository).deleteById(cartItemId);

        cartItemService.removeItemFromCart(cartItemId);

        verify(cartItemRepository, times(1)).existsById(cartItemId);
        verify(cartItemRepository, times(1)).deleteById(cartItemId);
    }

    @Test
    void clearCart() {
        Long cartId = 1L;
        List<CartItem> cartItems = Arrays.asList(cartItem1, cartItem2);

        when(cartRepository.existsById(cartId)).thenReturn(true);
        when(cartItemRepository.findAllByCartId(cartId)).thenReturn(cartItems);
        doNothing().when(cartItemRepository).deleteAll(cartItems);

        cartItemService.clearCart(cartId);

        verify(cartRepository, times(1)).existsById(cartId);
        verify(cartItemRepository, times(1)).findAllByCartId(cartId);
        verify(cartItemRepository, times(1)).deleteAll(cartItems);
    }
}