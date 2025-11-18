package org.example.myshop.service;

import org.example.myshop.entity.*;
import org.example.myshop.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Lazy;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    @Lazy
    private UserService userService;

    @Mock
    private CartService cartService;

    @Mock
    private ProductService productService;

    @Mock
    private OrderItemService orderItemService;

    @InjectMocks
    private OrderService orderService;

    private User testUser;
    private User testCourier;
    private Product testProduct1;
    private Product testProduct2;
    private Cart testCart;
    private CartItem cartItem1;
    private CartItem cartItem2;
    private Order testOrder;
    private OrderItem orderItem1;
    private OrderItem orderItem2;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("user@mail.com");
        testUser.setName("Test User");
        testUser.setRole(User.Role.USER);

        testCourier = new User();
        testCourier.setId(2L);
        testCourier.setEmail("courier@mail.com");
        testCourier.setName("Test Courier");
        testCourier.setRole(User.Role.COURIER);

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

        orderItem1 = new OrderItem();
        orderItem1.setId(1L);
        orderItem1.setProduct(testProduct1);
        orderItem1.setQuantity(2);
        orderItem1.setPrice(BigDecimal.valueOf(1999.98));

        orderItem2 = new OrderItem();
        orderItem2.setId(2L);
        orderItem2.setProduct(testProduct2);
        orderItem2.setQuantity(1);
        orderItem2.setPrice(BigDecimal.valueOf(1999.99));

        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setUser(testUser);
        testOrder.setStatus(Order.OrderStatus.PENDING);
        testOrder.setTotalAmount(BigDecimal.valueOf(3999.97));
        testOrder.setOrderItems(Arrays.asList(orderItem1, orderItem2));
    }

    @Test
    void getAll() {
        List<Order> orders = Arrays.asList(testOrder);
        when(orderRepository.findAll()).thenReturn(orders);

        List<Order> result = orderService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testOrder, result.get(0));
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void getById() {
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(testOrder));

        Order result = orderService.getById(orderId);

        assertNotNull(result);
        assertEquals(testOrder.getId(), result.getId());
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void setStatus() {
        testOrder.setCourier(testCourier);
        testOrder.setStatus(Order.OrderStatus.DISPATCHED);
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        Order result = orderService.setStatus(testOrder, Order.OrderStatus.CANCELLED);

        assertNotNull(result);
        assertNull(result.getCourier());
        assertEquals(Order.OrderStatus.PENDING, result.getStatus());
        verify(orderRepository, times(1)).save(testOrder);
    }

    @Test
    void getOrdersByUserId() {
        Long userId = 1L;
        List<Order> userOrders = Arrays.asList(testOrder);
        when(orderRepository.findOrderByUserId(userId)).thenReturn(userOrders);

        List<Order> result = orderService.getOrdersByUserId(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testOrder, result.get(0));
        verify(orderRepository, times(1)).findOrderByUserId(userId);
    }

    @Test
    void createOrderFromCart() {
        Long userId = 1L;

        when(userService.getById(userId)).thenReturn(testUser);
        when(cartService.getCartByUserId(userId)).thenReturn(testCart);
        when(productService.getById(1L)).thenReturn(testProduct1);
        when(productService.getById(2L)).thenReturn(testProduct2);
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(orderItemService.saveAll(anyList())).thenReturn(Arrays.asList(orderItem1, orderItem2));
        when(productService.productSubtractQuantity(anyLong(), anyInt())).thenReturn(testProduct1);

        Order result = orderService.createOrderFromCart(userId);

        assertNotNull(result);
        assertEquals(testUser, result.getUser());
        assertEquals(Order.OrderStatus.PENDING, result.getStatus());

        verify(userService, times(1)).getById(userId);
        verify(cartService, times(1)).getCartByUserId(userId);
        verify(productService, times(2)).getById(anyLong()); // For validation
        verify(orderRepository, times(2)).save(any(Order.class));
        verify(orderItemService, times(1)).saveAll(anyList());
        verify(cartService, times(1)).clearCartByUserId(userId);
        verify(productService, times(1)).productSubtractQuantity(1L, 2); // iPhone
        verify(productService, times(1)).productSubtractQuantity(2L, 1); // MacBook
    }

    @Test
    void save() {
        when(orderRepository.save(testOrder)).thenReturn(testOrder);

        Order result = orderService.save(testOrder);

        assertNotNull(result);
        assertEquals(testOrder.getId(), result.getId());
        verify(orderRepository, times(1)).save(testOrder);
    }

    @Test
    void update() {
        Long orderId = 1L;
        Order orderToUpdate = new Order();
        orderToUpdate.setUser(testUser);
        orderToUpdate.setCourier(testCourier);
        orderToUpdate.setStatus(Order.OrderStatus.DISPATCHED);
        orderToUpdate.setTotalAmount(BigDecimal.valueOf(5000.00));

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        Order result = orderService.update(orderId, orderToUpdate);

        // Assert
        assertNotNull(result);
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void deleted() {
        Long orderId = 1L;
        when(orderRepository.existsById(orderId)).thenReturn(true);
        doNothing().when(orderRepository).deleteById(orderId);

        orderService.deleted(orderId);

        verify(orderRepository, times(1)).existsById(orderId);
        verify(orderRepository, times(1)).deleteById(orderId);
    }
}