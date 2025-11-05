package org.example.myshop.service;

import javax.persistence.EntityNotFoundException;
import org.example.myshop.entity.Product;
import org.example.myshop.entity.User;
import org.example.myshop.repository.ProductRepository;
import org.example.myshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final UserService userService;

    @Autowired
    public ProductService(ProductRepository productRepository,@Lazy UserService userService) {
        this.productRepository = productRepository;
        this.userService = userService;
    }

    public Product getById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Продукт не найден"));
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product create(Product productToCreate,Long userId) {
        if(userService.getById(userId) == null) {
            throw new NoSuchElementException("Пользователь с таким id не найден");
        }
        else if(userService.getById(userId).getRole() == User.Role.USER) {
            throw new RuntimeException("У пользователя не достаточно прав");
        }
        return productRepository.save(productToCreate);
    }

    public Product update(Long id, Product productToUpdate) {
        Product product = productRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Продукт не найден"));
        Product updatedProduct = new Product(
                product.getId(),
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
