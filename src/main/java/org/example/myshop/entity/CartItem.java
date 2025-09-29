package org.example.myshop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "cart_items")
@Getter
@Setter
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity")
    private Integer quantity = 1;

    @Column(name = "price")
    private BigDecimal price;

    public CartItem(Long id, Cart cart, Product product, Integer quantity, BigDecimal price) {
        this.id = id;
        this.cart = cart;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
    }

    public CartItem() {
    }

    public void calculatePrice() {
        if (product != null && product.getPrice() != null) {
            this.price = BigDecimal.valueOf(product.getPrice()).multiply(BigDecimal.valueOf(quantity));
        }
    }
}