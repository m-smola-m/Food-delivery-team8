package com.team8.fooddelivery.service;

import com.team8.fooddelivery.model.product.Cart;
import com.team8.fooddelivery.model.product.CartItem;
import java.math.BigDecimal;
import java.util.List;

public interface CartService {

    Cart createCartForClient(Long clientId);

    Cart getCartForClient(Long clientId);

    Cart addItem(Long clientId, CartItem item);

    Cart removeItem(Long clientId, Long productId);

    Cart updateItem(Long clientId, Long productId, int newQuantity);

    List<CartItem> listItems(Long clientId);

    Cart clear(Long clientId);

    Long calculateTotal(Long clientId);

    /**
     * Возвращает общую стоимость корзины для пользователя.
     * Добавлено, чтобы удовлетворить вызовы в CartServlet.
     */
    default BigDecimal getCartTotal(Long userId) {
        throw new UnsupportedOperationException("getCartTotal not implemented yet");
    }

    /**
     * Добавляет товар в корзину пользователя.
     */
    default void addToCart(Long userId, Long productId, int quantity) {
        throw new UnsupportedOperationException("addToCart not implemented yet");
    }

    /**
     * Удаляет элемент корзины по id (или другой логике проекта).
     */
    default void removeFromCart(Long cartItemId) {
        throw new UnsupportedOperationException("removeFromCart not implemented yet");
    }

    /**
     * Обновляет количество для элемента корзины.
     */
    default void updateQuantity(Long cartItemId, int quantity) {
        throw new UnsupportedOperationException("updateQuantity not implemented yet");
    }
}
