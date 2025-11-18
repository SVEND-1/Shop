package org.example.myshop.service;

import org.example.myshop.entity.CartItem;
import org.example.myshop.entity.Order;
import org.example.myshop.entity.OrderItem;
import org.example.myshop.entity.Product;
import org.example.myshop.repository.OrderItemRepository;
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
class OrderItemServiceTest {

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private OrderItemService orderItemService;

    private OrderItem orderItem1;
    private OrderItem orderItem2;
    private Order testOrder;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setPrice(99.99);
        testProduct.setCount(10);

        testOrder = new Order();
        testOrder.setId(1L);

        orderItem1 = new OrderItem();
        orderItem1.setId(1L);
        orderItem1.setOrder(testOrder);
        orderItem1.setProduct(testProduct);
        orderItem1.setQuantity(2);
        orderItem1.setPrice(BigDecimal.valueOf(199.98));

        orderItem2 = new OrderItem();
        orderItem2.setId(2L);
        orderItem2.setOrder(testOrder);
        orderItem2.setProduct(testProduct);
        orderItem2.setQuantity(1);
        orderItem2.setPrice(BigDecimal.valueOf(99.99));
    }

    @Test
    void saveAll() {
        List<OrderItem> orderItems = Arrays.asList(orderItem1, orderItem2);
        when(orderItemRepository.saveAll(orderItems)).thenReturn(orderItems);

        List<OrderItem> result = orderItemService.saveAll(orderItems);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(orderItem1, result.get(0));
        assertEquals(orderItem2, result.get(1));
        verify(orderItemRepository, times(1)).saveAll(orderItems);
    }

    @Test
    void findById() {
        Long orderItemId = 1L;
        when(orderItemRepository.findById(orderItemId)).thenReturn(Optional.of(orderItem1));

        OrderItem result = orderItemService.findById(orderItemId);

        assertNotNull(result);
        assertEquals(orderItem1.getId(), result.getId());
        assertEquals(orderItem1.getOrder(), result.getOrder());
        assertEquals(orderItem1.getProduct(), result.getProduct());
        assertEquals(orderItem1.getQuantity(), result.getQuantity());
        verify(orderItemRepository, times(1)).findById(orderItemId);
    }

}