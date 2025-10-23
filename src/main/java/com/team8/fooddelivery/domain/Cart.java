package com.team8.fooddelivery.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "carts")
public class Cart {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
    
    @Column(nullable = false)
    private Instant createdAt;
    
    @Column(nullable = false)
    private Instant updatedAt;
    
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CartItem> products;

    public Cart() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public Cart(Client client) {
        this.client = client;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public Long getCartId() { return cartId; }
    public Client getClient() { return client; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public List<CartItem> getProducts() { return products; }

    public void setCartId(Long cartId) { this.cartId = cartId; }
    public void setClient(Client client) { this.client = client; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public void setProducts(List<CartItem> products) { this.products = products; }
}
