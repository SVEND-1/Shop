package org.example.myshop.service;

import javax.persistence.EntityNotFoundException;
import org.example.myshop.entity.Product;
import org.example.myshop.entity.User;
import org.example.myshop.repository.ProductRepository;
import org.example.myshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Page<Product> findProductsWithPagination(int offset, int limit) {
        Page<Product> products = productRepository.findAll(PageRequest.of(offset,limit));//Можно добавить сортировку
        return products;
    }

    public List<Product> getProductsBySeller(Long sellerId) {
        return productRepository.findBySellerId(sellerId);
    }

    public Product getById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Продукт не найден"));
    }

    public List<Product> getProductsByCategory(Product.Category category) {
        return productRepository.findProductByCategory(category);
    }

    public List<Product> getAvailableProducts(){
        return productRepository.findByCountGreaterThan(0);
    }

    public List<Product> searchProducts(String query){
        return getAvailableProducts().stream().filter(product -> product.getName().contains(query)).collect(Collectors.toList());
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product productSubtractQuantity(Long productId, int quantity) {
        Product product = getById(productId);
        product.setCount(product.getCount() - quantity);
        return productRepository.save(product);
    }

    public Product create(Product productToCreate) {
        return productRepository.save(productToCreate);
    }

    public Product update(Long id, Product productToUpdate) {
        Product product = productRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Продукт не найден"));
        Product updatedProduct = new Product(
                product.getId(),
                productToUpdate.getSeller(),
                productToUpdate.getName(),
                productToUpdate.getPrice(),
                productToUpdate.getCount(),
                productToUpdate.getDescription(),
                productToUpdate.getImage(),
                productToUpdate.getCategory(),
                productToUpdate.getOrderItems(),
                productToUpdate.getCartItems());
        return productRepository.save(updatedProduct);
    }

    public void deleted(Long id) {
        if(!productRepository.existsById(id)){
            throw new NoSuchElementException("Продукт не найден");
        }
        productRepository.deleteById(id);
    }
}
