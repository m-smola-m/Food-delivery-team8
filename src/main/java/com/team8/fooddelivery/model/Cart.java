package com.team8.fooddelivery.model;

import java.time.Instant;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    private Long cartId;
    private Client client;  // Long clientId
    private Instant createdAt;
    private Instant updatedAt;
    private List<CartItem> products;
    private Double totalPrice;
    private Integer totalQuantity;
    }


