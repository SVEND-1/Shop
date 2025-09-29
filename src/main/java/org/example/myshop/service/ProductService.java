package org.example.myshop.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.myshop.entity.Product;
import org.example.myshop.entity.User;
import org.example.myshop.repository.ProductRepository;
import org.example.myshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product getById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("не найден"));
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product create(Product productToCreate) {
        //TODO: Дописать проверку на админа
        return productRepository.save(productToCreate);
    }

    public Product update(Long id, Product productToUpdate) {
        Product product = productRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("не найден"));
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
            throw new NoSuchElementException("не найден");
        }
        productRepository.deleteById(id);
    }
}
