package com.team8.fooddelivery.interfaces;

import com.team8.fooddelivery.model.Cart;
import com.team8.fooddelivery.model.CartItem;
import java.util.List;

public interface CartService {
    Cart getCartForClient(Long clientId);
    Cart addItem(Long clientId, CartItem item);
    Cart removeItem(Long clientId, Long productId);
    Cart clear(Long clientId);
    List<CartItem> listItems(Long clientId);
    Cart updateItem(Long clientId, Long productId, int newQuantity);
}


