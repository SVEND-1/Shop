package org.example.myshop.service;

import org.example.myshop.entity.Product;
import org.example.myshop.entity.User;
import org.example.myshop.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct1;
    private Product testProduct2;
    private Product testProduct3;
    private User testSeller;

    @BeforeEach
    void setUp() {
        testSeller = new User();
        testSeller.setId(1L);
        testSeller.setEmail("seller@mail.com");
        testSeller.setName("Test Seller");

        testProduct1 = new Product();
        testProduct1.setId(1L);
        testProduct1.setName("iPhone 15");
        testProduct1.setPrice(999.99);
        testProduct1.setCount(10);
        testProduct1.setDescription("Latest iPhone");
        testProduct1.setCategory(Product.Category.ELECTRONICS);
        testProduct1.setSeller(testSeller);

        testProduct2 = new Product();
        testProduct2.setId(2L);
        testProduct2.setName("Samsung Galaxy");
        testProduct2.setPrice(899.99);
        testProduct2.setCount(5);
        testProduct2.setDescription("Android phone");
        testProduct2.setCategory(Product.Category.ELECTRONICS);
        testProduct2.setSeller(testSeller);

        testProduct3 = new Product();
        testProduct3.setId(3L);
        testProduct3.setName("T-shirt");
        testProduct3.setPrice(29.99);
        testProduct3.setCount(0); // Out of stock
        testProduct3.setDescription("Cotton t-shirt");
        testProduct3.setCategory(Product.Category.CLOTHING);
        testProduct3.setSeller(testSeller);
    }
    @Test
    void getAvailableProductsWithPagination() {
        int page = 0;
        int size = 10;
        List<Product> products = Arrays.asList(testProduct1, testProduct2);
        Page<Product> productPage = new PageImpl<>(products, PageRequest.of(page, size), products.size());

        when(productRepository.findByCountGreaterThan(0, PageRequest.of(page, size)))
                .thenReturn(productPage);

        Page<Product> result = productService.getAvailableProductsWithPagination(page, size);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertTrue(result.getContent().contains(testProduct1));
        assertTrue(result.getContent().contains(testProduct2));
        verify(productRepository, times(1)).findByCountGreaterThan(0, PageRequest.of(page, size));
    }

    @Test
    void getProductsByCategoryWithPagination() {
        int page = 0;
        int size = 10;
        Product.Category category = Product.Category.ELECTRONICS;
        List<Product> products = Arrays.asList(testProduct1, testProduct2);
        Page<Product> productPage = new PageImpl<>(products, PageRequest.of(page, size), products.size());

        when(productRepository.findByCategoryAndCountGreaterThan(category, 0, PageRequest.of(page, size)))
                .thenReturn(productPage);

        Page<Product> result = productService.getProductsByCategoryWithPagination(category, page, size);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertTrue(result.getContent().stream()
                .allMatch(product -> product.getCategory() == category));
        verify(productRepository, times(1))
                .findByCategoryAndCountGreaterThan(category, 0, PageRequest.of(page, size));
    }

    @Test
    void find48Product() {
        List<Product> products = Arrays.asList(testProduct1, testProduct2);
        when(productRepository.findTop48ByCountGreaterThan(0)).thenReturn(products);

        List<Product> result = productService.find48Product();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(product -> product.getCount() > 0));
        verify(productRepository, times(1)).findTop48ByCountGreaterThan(0);
    }

    @Test
    void getProductsBySeller() {
        Long sellerId = 1L;
        List<Product> products = Arrays.asList(testProduct1, testProduct2, testProduct3);
        when(productRepository.findBySellerId(sellerId)).thenReturn(products);

        List<Product> result = productService.getProductsBySeller(sellerId);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.stream().allMatch(product ->
                product.getSeller().getId().equals(sellerId)));
        verify(productRepository, times(1)).findBySellerId(sellerId);
    }

    @Test
    void getById() {
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct1));

        Product result = productService.getById(productId);

        assertNotNull(result);
        assertEquals(testProduct1.getId(), result.getId());
        assertEquals(testProduct1.getName(), result.getName());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void getAvailableProducts() {
        List<Product> allProducts = Arrays.asList(testProduct1, testProduct2, testProduct3);
        List<Product> availableProducts = Arrays.asList(testProduct1, testProduct2);
        when(productRepository.findByCountGreaterThan(0)).thenReturn(availableProducts);

        List<Product> result = productService.getAvailableProducts();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(product -> product.getCount() > 0));
        assertFalse(result.contains(testProduct3)); // Out of stock product
        verify(productRepository, times(1)).findByCountGreaterThan(0);
    }

    @Test
    void searchProducts() {
        String query = "iPhone";
        List<Product> availableProducts = Arrays.asList(testProduct1, testProduct2);
        when(productRepository.findByCountGreaterThan(0)).thenReturn(availableProducts);

        List<Product> result = productService.searchProducts(query);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testProduct1, result.get(0));
        assertTrue(result.get(0).getName().contains(query));
        verify(productRepository, times(1)).findByCountGreaterThan(0);
    }

    @Test
    void productSubtractQuantity() {
        Long productId = 1L;
        int quantity = 3;
        int initialCount = testProduct1.getCount();

        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct1));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct1);

        Product result = productService.productSubtractQuantity(productId, quantity);

        assertNotNull(result);
        assertEquals(initialCount - quantity, result.getCount());
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).save(testProduct1);
    }

    @Test
    void productAddQuantity() {
        Long productId = 1L;
        int quantity = 5;
        int initialCount = testProduct1.getCount();

        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct1));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct1);

        Product result = productService.productAddQuantity(productId, quantity);

        assertNotNull(result);
        assertEquals(initialCount + quantity, result.getCount());
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).save(testProduct1);
    }

    @Test
    void create() {
        when(productRepository.save(testProduct1)).thenReturn(testProduct1);

        Product result = productService.create(testProduct1);

        assertNotNull(result);
        assertEquals(testProduct1.getId(), result.getId());
        assertEquals(testProduct1.getName(), result.getName());
        verify(productRepository, times(1)).save(testProduct1);
    }

    @Test
    void update() {
        Long productId = 1L;
        Product productToUpdate = new Product();
        productToUpdate.setName("Updated Name");
        productToUpdate.setPrice(1099.99);
        productToUpdate.setCount(15);
        productToUpdate.setDescription("Updated description");
        productToUpdate.setCategory(Product.Category.ELECTRONICS);
        productToUpdate.setImage("new-image.jpg");

        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct1));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct1);

        Product result = productService.update(productId, productToUpdate);

        assertNotNull(result);
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).save(testProduct1);
    }

    @Test
    void deleted() {
        Long productId = 1L;
        when(productRepository.existsById(productId)).thenReturn(true);
        doNothing().when(productRepository).deleteById(productId);

        productService.deleted(productId);

        verify(productRepository, times(1)).existsById(productId);
        verify(productRepository, times(1)).deleteById(productId);
    }
}