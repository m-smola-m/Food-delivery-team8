package com.team8.fooddelivery.domain;

import java.time.Instant;
import java.util.List;

public class Cart {
    private Long cartId;
    private Client client;  // Long clientId
    private Instant createdAt;
    private Instant updatedAt;
    private List<CartItem> products;

}
