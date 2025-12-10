package com.team8.fooddelivery.service.impl;

import com.team8.fooddelivery.model.product.Cart;
import com.team8.fooddelivery.model.product.CartItem;
import com.team8.fooddelivery.model.product.Product;
import com.team8.fooddelivery.repository.CartRepository;
import com.team8.fooddelivery.repository.ClientRepository;
import com.team8.fooddelivery.repository.ProductRepository;
import com.team8.fooddelivery.service.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;

public class CartServiceImpl implements CartService {

    private static final Logger logger = LoggerFactory.getLogger(CartServiceImpl.class);
    private final CartRepository cartRepository = new CartRepository();
    private final ClientRepository clientRepository = new ClientRepository();
    private final ProductRepository productRepository = new ProductRepository();

    // Используется ClientService при регистрации
    public Cart createCartForClient(Long clientId) {
        if (clientId == null) {
            // Временная корзина, будет сохранена позже
            return Cart.builder()
                    .clientId(null)
                    .items(new ArrayList<>())
                    .build();
        }

        validateClient(clientId);

        try {
            // Проверяем, есть ли уже корзина для этого клиента
            Optional<Cart> existingCart = cartRepository.findByClientId(clientId);
            if (existingCart.isPresent()) {
                return existingCart.get();
            }

            // Создаем новую корзину
            Cart cart = Cart.builder()
                    .clientId(clientId)
                    .items(new ArrayList<>())
                    .build();

            Long cartId = cartRepository.save(cart);
            cart.setId(cartId);
            return cart;
        } catch (Exception e) {
            logger.error("Ошибка при создании корзины для клиента {}", clientId, e);
            throw new RuntimeException("Не удалось создать корзину", e);
        }
    }

    @Override
    public Cart getCartForClient(Long clientId) {
        validateClient(clientId);
        try {
            return cartRepository.findByClientId(clientId).orElse(null);
        } catch (Exception e) {
            logger.error("Ошибка при получении корзины для клиента {}", clientId, e);
            return null;
        }
    }

    @Override
    public List<CartItem> listItems(Long clientId) {
        Cart cart = getCartForClient(clientId);
        if (cart == null) return Collections.emptyList();
        return cart.getItems();
    }

    @Override
    public Cart addItem(Long clientId, CartItem item) {
        try {
            Cart cart = getOrCreate(clientId);

            // Проверяем, что все товары в корзине из одного ресторана
            if (!cart.getItems().isEmpty() && item.getProductId() != null) {
                Long newProductShopId = getShopIdByProductId(item.getProductId());
                if (newProductShopId != null) {
                    // Проверяем shopId первого товара в корзине
                    Long firstProductId = cart.getItems().get(0).getProductId();
                    if (firstProductId != null) {
                        Long firstProductShopId = getShopIdByProductId(firstProductId);
                        if (firstProductShopId != null && !firstProductShopId.equals(newProductShopId)) {
                            throw new IllegalStateException("Нельзя добавлять товары из разных ресторанов в одну корзину. Очистите корзину перед добавлением товаров из другого ресторана.");
                        }
                    }
                }
            }

            // Если товар уже есть — увеличиваем количество
            Optional<CartItem> existing = cart.getItems().stream()
                    .filter(i -> i.getProductId().equals(item.getProductId()))
                    .findFirst();

            if (existing.isPresent()) {
                CartItem ci = existing.get();
                ci.setQuantity(ci.getQuantity() + item.getQuantity());
                cartRepository.updateCartItem(ci);
                return cart;
            }

            // Новый товар
            item.setCartId(cart.getId());
            Long itemId = cartRepository.saveCartItem(item);
            item.setId(itemId);
            cart.getItems().add(item);
            return cart;
        } catch (SQLException e) {
            logger.error("Ошибка при добавлении товара в корзину", e);
            throw new RuntimeException("Не удалось добавить товар в корзину", e);
        }
    }

    private Long getShopIdByProductId(Long productId) {
        try {
            String sql = "SELECT shop_id FROM products WHERE product_id = ?";
            try (var conn = com.team8.fooddelivery.service.DatabaseConnectionService.getConnection();
                 var stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, productId);
                var rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getObject("shop_id", Long.class);
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting shopId for product {}", productId, e);
        }
        return null;
    }

    @Override
    public Cart updateItem(Long clientId, Long productId, int newQuantity) {
        try {
            Cart cart = getOrCreate(clientId);

            Optional<CartItem> itemOpt = cart.getItems().stream()
                    .filter(i -> i.getProductId().equals(productId))
                    .findFirst();

            if (itemOpt.isPresent()) {
                CartItem item = itemOpt.get();
                item.setQuantity(newQuantity);
                cartRepository.updateCartItem(item);
            }

            return cart;
        } catch (SQLException e) {
            logger.error("Ошибка при обновлении товара в корзине", e);
            throw new RuntimeException("Не удалось обновить товар в корзине", e);
        }
    }

    @Override
    public Cart removeItem(Long clientId, Long productId) {
        try {
            Cart cart = getOrCreate(clientId);
            Optional<CartItem> itemToRemove = cart.getItems().stream()
                    .filter(i -> i.getProductId().equals(productId))
                    .findFirst();

            if (itemToRemove.isPresent()) {
                cartRepository.deleteCartItemByProductId(cart.getId(), productId);
                cart.getItems().removeIf(i -> i.getProductId().equals(productId));
            }
            return cart;
        } catch (SQLException e) {
            logger.error("Ошибка при удалении товара из корзины", e);
            throw new RuntimeException("Не удалось удалить товар из корзины", e);
        }
    }

    @Override
    public Cart clear(Long clientId) {
        try {
            Optional<Cart> cartOpt = cartRepository.findByClientId(clientId);
            if (cartOpt.isPresent()) {
                Cart cart = cartOpt.get();
                cartRepository.clearCart(cart.getId());
                cart.getItems().clear();
                return cart;
            }
            return getOrCreate(clientId);
        } catch (SQLException e) {
            logger.error("Ошибка при очистке корзины", e);
            throw new RuntimeException("Не удалось очистить корзину", e);
        }
    }

    public void clearCart(Long clientId) {
        clear(clientId);
    }

    public void addItemsFromOrder(Long clientId, List<CartItem> items) {
        try {
            Cart cart = getOrCreate(clientId);
            cartRepository.clearCart(cart.getId());
            for (CartItem item : items) {
                CartItem newItem = CartItem.builder()
                        .cartId(cart.getId())
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .build();
                cartRepository.saveCartItem(newItem);
            }
        } catch (SQLException e) {
            logger.error("Не удалось добавить товары заказа в корзину", e);
            throw new RuntimeException("Не удалось повторить заказ", e);
        }
    }

    @Override
    public Long calculateTotal(Long clientId) {
        Cart cart = getCartForClient(clientId);
        if (cart == null) return 0L;
        return cart.getTotalPrice();
    }

    @Override
    public BigDecimal getCartTotal(Long clientId) {
        return BigDecimal.valueOf(calculateTotal(clientId));
    }

    @Override
    public void addToCart(Long clientId, Long productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Количество должно быть больше нуля");
        }
        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Продукт не найден"));

            CartItem item = CartItem.builder()
                    .productId(productId)
                    .productName(product.getName())
                    .quantity(quantity)
                    .price(product.getPrice())
                    .build();

            addItem(clientId, item);
        } catch (SQLException e) {
            logger.error("Ошибка при добавлении продукта {} в корзину клиента {}", productId, clientId, e);
            throw new RuntimeException("Не удалось добавить товар в корзину", e);
        }
    }

    @Override
    public void removeFromCart(Long clientId, Long cartItemId) {
        try {
            Cart cart = getOrCreate(clientId);
            Optional<CartItem> itemOpt = cartRepository.findCartItemById(cartItemId);
            if (itemOpt.isEmpty() || !itemOpt.get().getCartId().equals(cart.getId())) {
                throw new IllegalArgumentException("Товар не найден в корзине клиента");
            }
            cartRepository.deleteCartItem(cartItemId);
            cart.getItems().removeIf(i -> i.getId().equals(cartItemId));
        } catch (SQLException e) {
            logger.error("Ошибка при удалении товара {} из корзины клиента {}", cartItemId, clientId, e);
            throw new RuntimeException("Не удалось удалить товар из корзины", e);
        }
    }

    @Override
    public void updateQuantity(Long clientId, Long cartItemId, int quantity) {
        if (quantity <= 0) {
            removeFromCart(clientId, cartItemId);
            return;
        }
        try {
            Cart cart = getOrCreate(clientId);
            Optional<CartItem> itemOpt = cartRepository.findCartItemById(cartItemId);
            if (itemOpt.isEmpty() || !itemOpt.get().getCartId().equals(cart.getId())) {
                throw new IllegalArgumentException("Товар не найден в корзине клиента");
            }
            CartItem item = itemOpt.get();
            item.setQuantity(quantity);
            cartRepository.updateCartItem(item);
            cart.getItems().stream()
                    .filter(i -> i.getId().equals(cartItemId))
                    .findFirst()
                    .ifPresent(i -> i.setQuantity(quantity));
        } catch (SQLException e) {
            logger.error("Ошибка при обновлении количества для элемента {} клиента {}", cartItemId, clientId, e);
            throw new RuntimeException("Не удалось обновить количество товара", e);
        }
    }

    @Override
    public List<CartItem> getCartItemsForClient(Long clientId) {
        Cart cart = getCartForClient(clientId);
        if (cart == null) {
            return Collections.emptyList();
        }
        try {
            return cartRepository.findItemsByCartId(cart.getId());
        } catch (SQLException e) {
            logger.error("Ошибка при получении элементов корзины клиента {}", clientId, e);
            return Collections.emptyList();
        }
    }

    // ---------------------------------
    //      PRIVATE HELPERS
    // ---------------------------------

    private Cart getOrCreate(Long clientId) {
        validateClient(clientId);
        try {
            Optional<Cart> existingCart = cartRepository.findByClientId(clientId);
            if (existingCart.isPresent()) {
                return existingCart.get();
            }

            // Создаем новую корзину
            Cart cart = Cart.builder()
                    .clientId(clientId)
                    .items(new ArrayList<>())
                    .build();

            Long cartId = cartRepository.save(cart);
            cart.setId(cartId);
            return cart;
        } catch (SQLException e) {
            logger.error("Ошибка при получении/создании корзины для клиента {}", clientId, e);
            throw new RuntimeException("Не удалось получить или создать корзину", e);
        }
    }

    private void validateClient(Long clientId) {
        try {
            if (clientId == null || clientRepository.findById(clientId).isEmpty()) {
                throw new IllegalArgumentException("Клиент не найден или не задан");
            }
        } catch (SQLException e) {
            logger.error("Ошибка при проверке существования клиента {}", clientId, e);
            throw new RuntimeException("Не удалось проверить клиента", e);
        }
    }

    @Override
    public Cart getCartByClientId(Long clientId) {
        return getCartForClient(clientId);
    }

    @Override
    public List<CartItem> getCartItems(Long cartId) {
        try {
            return cartRepository.findItemsByCartId(cartId);
        } catch (SQLException e) {
            logger.error("Error getting cart items for cart {}", cartId, e);
            return Collections.emptyList();
        }
    }
}
