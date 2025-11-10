package com.team8.fooddelivery.model;

import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    private Long id;
    private Long cartId;
    private Long productId;
    private Integer quantity;
    private BigDecimal price;
}


