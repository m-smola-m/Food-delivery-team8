package com.team8.fooddelivery.domain;

import java.math.BigDecimal;
import java.time.Instant;

public class CartItem {
    private Long cartItemId;
    private Long cartId;
    private Long productId;
    private Integer quantity;
    private BigDecimal price;

    public CartItem(Long cartId, Long productId, Integer quantity, BigDecimal price) {
        this.cartId = cartId;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    public Long getCartItemId() { return cartItemId; }
    public Long getCartId() { return cartId; }
    public Long getProductId() { return productId; }
    public Integer getQuantity() { return quantity; }
    public BigDecimal getPrice() { return price; }

    public void setCartItemId(Long cartItemId) { this.cartItemId = cartItemId; }
    public void setCartId(Long cartId) { this.cartId = cartId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public void setPrice(BigDecimal price) { this.price = price; }
}
