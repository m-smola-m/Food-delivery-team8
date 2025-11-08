package com.team8.fooddelivery.model;

import java.time.Instant;
import java.util.List;

public class Cart {
    private Long cartId;
    private Client client;  // Long clientId
    private Instant createdAt;
    private Instant updatedAt;
    private List<CartItem> products;

    // public Cart() {
    //     this.createdAt = Instant.now();
    //     this.updatedAt = Instant.now();
    // }

    // public Cart(Client client) {
    //     this.client = client;
    //     this.createdAt = Instant.now();
    //     this.updatedAt = Instant.now();
    // }

    // public Long getCartId() { return cartId; }
    // public Client getClient() { return client; }
    // public Instant getCreatedAt() { return createdAt; }
    // public Instant getUpdatedAt() { return updatedAt; }
    // public List<CartItem> getProducts() { return products; }

    // public void setCartId(Long cartId) { this.cartId = cartId; }
    // public void setClient(Client client) { this.client = client; }
    // public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    // public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    // public void setProducts(List<CartItem> products) { this.products = products; }
}
