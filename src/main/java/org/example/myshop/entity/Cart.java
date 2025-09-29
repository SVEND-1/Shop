package org.example.myshop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "cards")
@Getter
@Setter
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CartItem> cartItems = new ArrayList<>();

    @Column(name = "total_price")
    private BigDecimal totalPrice = BigDecimal.ZERO;

    public Cart() {
    }

    public Cart(Long id, User user, List<CartItem> cartItems, BigDecimal totalPrice) {
        this.id = id;
        this.user = user;
        this.cartItems = cartItems;
        this.totalPrice = totalPrice;
    }

    public void calculateTotalPrice() {
        this.totalPrice = cartItems.stream()
                .map(item -> {
                    item.calculatePrice(); // Обновляем цену каждого элемента
                    return item.getPrice();
                })
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void addCartItem(Product product) {
        CartItem cartItem = new CartItem();
        cartItem.setCart(this);
        cartItem.setProduct(product);
        cartItem.calculatePrice();
        this.cartItems.add(cartItem);
        calculateTotalPrice();
    }

    public void removeCartItemByProduct(Product product) {
        boolean removed = cartItems.removeIf(item ->
                item.getProduct() != null && item.getProduct().getId().equals(product.getId()));
        if (removed) {
            calculateTotalPrice();
        }
    }

    public void clearCart() {
        this.cartItems.clear();
        this.totalPrice = BigDecimal.ZERO;
    }
}