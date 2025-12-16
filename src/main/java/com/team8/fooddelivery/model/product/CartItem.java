package com.team8.fooddelivery.model.product;

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
    private String productName;
    private int quantity;
    private double price;

    private Long shopId;
    private String shopName;

    public static CartItemBuilder builder() {
        return new CartItemBuilder();
    }

    public static class CartItemBuilder {
        private Long id;
        private Long cartId;
        private Long productId;
        private String productName;
        private int quantity;
        private double price;
        private Long shopId;
        private String shopName;

        public CartItemBuilder id(Long id) { this.id = id; return this; }
        public CartItemBuilder cartId(Long cartId) { this.cartId = cartId; return this; }
        public CartItemBuilder productId(Long productId) { this.productId = productId; return this; }
        public CartItemBuilder productName(String productName) { this.productName = productName; return this; }
        public CartItemBuilder quantity(int quantity) { this.quantity = quantity; return this; }
        public CartItemBuilder price(double price) { this.price = price; return this; }

        // Поддержка как Long, так и примитива long для совместимости
        public CartItemBuilder shopId(Long shopId) { this.shopId = shopId; return this; }
        public CartItemBuilder shopId(long shopId) { this.shopId = shopId; return this; }

        public CartItemBuilder shopName(String shopName) { this.shopName = shopName; return this; }

        public CartItem build() {
            return new CartItem(id, cartId, productId, productName, quantity, price, shopId, shopName);
        }
    }
}
