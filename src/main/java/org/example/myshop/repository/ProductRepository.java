package org.example.myshop.repository;

import org.example.myshop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findProductByCategory(Product.Category category);

    List<Product> findByCountGreaterThan(int countIsGreaterThan);

    List<Product> findBySellerId(Long sellerId);
}
