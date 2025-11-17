package com.team8.fooddelivery.service.impl;

import com.team8.fooddelivery.model.Cart;
import com.team8.fooddelivery.model.CartItem;
import com.team8.fooddelivery.service.CartService;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class CartServiceImpl implements CartService {

    // -----------------------------
    //   Псевдо-БД (пока In-Memory)
    // -----------------------------
    private final Map<Long, Cart> carts = new HashMap<>();
    private final AtomicLong cartIdGen = new AtomicLong(1);
    private final AtomicLong itemIdGen = new AtomicLong(1);

    // Используется ClientService при регистрации
    public Cart createCartForClient(Long clientId) {
        Cart cart = Cart.builder()
                .id(cartIdGen.getAndIncrement())
                .clientId(clientId)
                .items(new ArrayList<>())
                .build();

        carts.put(clientId, cart);
        return cart;
    }

    @Override
    public Cart getCartForClient(Long clientId) {
        return carts.get(clientId);
    }

    @Override
    public List<CartItem> listItems(Long clientId) {
        Cart cart = getCartForClient(clientId);
        if (cart == null) return Collections.emptyList();
        return cart.getItems();
    }

    @Override
    public Cart addItem(Long clientId, CartItem item) {
        Cart cart = getOrCreate(clientId);

        // Если товар уже есть — увеличиваем количество
        Optional<CartItem> existing = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(item.getProductId()))
                .findFirst();

        if (existing.isPresent()) {
            CartItem ci = existing.get();
            ci.setQuantity(ci.getQuantity() + item.getQuantity());
            return cart;
        }

        // Новый товар
        item.setId(itemIdGen.getAndIncrement());
        item.setCartId(cart.getId());
        cart.getItems().add(item);
        return cart;
    }

    @Override
    public Cart updateItem(Long clientId, Long productId, int newQuantity) {
        Cart cart = getOrCreate(clientId);

        cart.getItems().stream()
                .filter(i -> i.getProductId().equals(productId))
                .findFirst()
                .ifPresent(i -> i.setQuantity(newQuantity));

        return cart;
    }

    @Override
    public Cart removeItem(Long clientId, Long productId) {
        Cart cart = getOrCreate(clientId);
        cart.getItems().removeIf(i -> i.getProductId().equals(productId));
        return cart;
    }

    @Override
    public Cart clear(Long clientId) {
        Cart cart = getOrCreate(clientId);
        cart.getItems().clear();
        return cart;
    }

    @Override
    public Long calculateTotal(Long clientId) {
        Cart cart = getCartForClient(clientId);
        if (cart == null) return 0L;
        return cart.getTotalPrice(); // totalPrice = Long
    }

    // ---------------------------------
    //      PRIVATE HELPERS
    // ---------------------------------

    private Cart getOrCreate(Long clientId) {
        return carts.computeIfAbsent(clientId, id ->
                Cart.builder()
                        .id(cartIdGen.getAndIncrement())
                        .clientId(clientId)
                        .items(new ArrayList<>())
                        .build()
        );
    }
}

