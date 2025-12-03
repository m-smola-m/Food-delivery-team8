package com.team8.fooddelivery.userstory;

import com.team8.fooddelivery.model.Address;
import com.team8.fooddelivery.model.client.Client;
import com.team8.fooddelivery.model.client.PaymentMethodForOrder;
import com.team8.fooddelivery.model.client.PaymentStatus;
import com.team8.fooddelivery.model.notification.Notification;
import com.team8.fooddelivery.model.order.Order;
import com.team8.fooddelivery.model.order.OrderStatus;
import com.team8.fooddelivery.model.product.Cart;
import com.team8.fooddelivery.model.product.CartItem;
import com.team8.fooddelivery.model.shop.Shop;
import com.team8.fooddelivery.model.shop.ShopStatus;
import com.team8.fooddelivery.model.product.Product;
import com.team8.fooddelivery.model.product.ProductCategory;
import com.team8.fooddelivery.repository.ClientRepository;
import com.team8.fooddelivery.repository.ShopRepository;
import com.team8.fooddelivery.repository.ProductRepository;
import com.team8.fooddelivery.service.DatabaseInitializerService;
import com.team8.fooddelivery.service.NotificationService;
import com.team8.fooddelivery.service.impl.CartServiceImpl;
import com.team8.fooddelivery.service.impl.ClientServiceImpl;
import com.team8.fooddelivery.service.impl.OrderServiceImpl;
import com.team8.fooddelivery.service.impl.ShopInfoServiceImpl;

import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class CartUserStory {
    public static void main(String[] args) {
        DatabaseInitializerService.resetDatabaseWithTestData();

        // =====================
        // 0. Инициализация сервисов
        // =====================
        CartServiceImpl cartService = new CartServiceImpl();
        ClientServiceImpl clientService = new ClientServiceImpl(new ClientRepository(), cartService);
        OrderServiceImpl orderService = new OrderServiceImpl(cartService);
        ShopInfoServiceImpl shopService = new ShopInfoServiceImpl();
        ProductRepository productRepository = new ProductRepository();
        NotificationService notificationService = NotificationService.getInstance();

        Shop shop = ensureApprovedShop(shopService);
        populateShopWithProducts(shop.getShopId());

        Client client = ensureClient(clientService);
        cartService.clear(client.getId());

        listShops(shopService);
        listCategoriesAndProducts(productRepository, shop.getShopId());

        addProductsToCart(cartService, client.getId());
        printCart(cartService.getCartForClient(client.getId()));

        Order order = orderService.placeOrder(
                client.getId(),
                Address.builder().country("Россия").city("Москва").street("Тверская").building("1").build(),
                PaymentMethodForOrder.CARD
        );

        System.out.println("Создан заказ: " + order.getId());
        System.out.println("Статус заказа: " + order.getStatus());
        System.out.println("Статус оплаты: " + order.getPaymentStatus());

        List<Notification> notifications = notificationService.getNotifications(client.getId());
        System.out.println("Уведомления клиента:");
        notifications.forEach(System.out::println);

        orderService.repeatOrder(client.getId(), order.getId());
        System.out.println("Повтор заказа добавил товары в корзину: " + cartService.getCartForClient(client.getId()).getItems());
    }

    private static Shop ensureApprovedShop(ShopInfoServiceImpl shopService) {
        ShopRepository shopRepository = new ShopRepository();
        try {
            Shop shop = shopRepository.findAll().stream()
                    .filter(s -> s.getStatus() == ShopStatus.APPROVED)
                    .findFirst()
                    .orElseGet(() -> {
                        Shop s = new Shop();
                        s.setNaming("Demo Bistro");
                        s.setPublicEmail("demo@shop.local");
                        s.setPublicPhone("+79000000000");
                        Shop registered = shopService.registerShop(s, "demo@shop.local", "Password123", "+79123456789");
                        shopService.approveShop(registered.getShopId());
                        return registered;
                    });
            System.out.println("Используем магазин: " + shop.getNaming());
            return shop;
        } catch (SQLException e) {
            throw new RuntimeException("Не удалось получить магазин", e);
        }
    }

    private static void populateShopWithProducts(Long shopId) {
        ProductRepository productRepository = new ProductRepository();
        try {
            if (!productRepository.findByShopId(shopId).isEmpty()) {
                return;
            }
            productRepository.saveForShop(shopId, Product.builder()
                    .name("Пицца Пепперони")
                    .description("Классическая пицца с пепперони")
                    .price(650.0)
                    .category(ProductCategory.MAIN_DISH)
                    .available(true)
                    .build());
            productRepository.saveForShop(shopId, Product.builder()
                    .name("Чизкейк")
                    .description("Нью-Йорк")
                    .price(320.0)
                    .category(ProductCategory.DESSERT)
                    .available(true)
                    .build());
        } catch (SQLException e) {
            throw new RuntimeException("Не удалось заполнить меню", e);
        }
    }

    private static Client ensureClient(ClientServiceImpl clientService) {
        Client existing = clientService.getByPhone("89991112233");
        if (existing != null) {
            return existing;
        }
        return clientService.register(
                "89991112256",
                "Ivan123!",
                "Иван Иванов",
                "ivan@example.com",
                Address.builder()
                        .country("Россия")
                        .city("Москва")
                        .street("Тверская")
                        .building("1")
                        .apartment("10")
                        .entrance("1")
                        .floor(3)
                        .latitude(55.7558)
                        .longitude(37.6173)
                        .addressNote("Тестовый адрес user story")
                        .district("ЦАО")
                        .build()
        );
    }

    private static void listShops(ShopInfoServiceImpl shopService) {
        System.out.println("Доступные магазины:");
        shopService.getAllShops().forEach(s -> System.out.println(" - " + s.getNaming() + " (статус=" + s.getStatus() + ")"));
    }

    private static void listCategoriesAndProducts(ProductRepository productRepository, Long shopId) {
        try {
            List<ProductCategory> categories = productRepository.findCategoriesByShopId(shopId);
            System.out.println("Категории магазина:");
            categories.forEach(c -> System.out.println(" * " + c));
            for (ProductCategory category : categories) {
                List<Product> products = productRepository.findByShopIdAndCategory(shopId, category);
                System.out.println("Продукты категории " + category + ":" + products);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Не удалось получить категории", e);
        }
    }

    private static void addProductsToCart(CartServiceImpl cartService, Long clientId) {
        cartService.addItem(clientId, CartItem.builder()
                .productId(5001L)
                .productName("Пицца Пепперони")
                .quantity(2)
                .price(650.0)
                .build());
        cartService.addItem(clientId, CartItem.builder()
                .productId(5016L)
                .productName("Чизкейк Нью-Йорк")
                .quantity(1)
                .price(270.0)
                .build());
    }

    private static void printCart(Cart cart) {
        System.out.println("Текущая корзина:");
        cart.getItems().forEach(item -> System.out.println(item.getProductName() + " x" + item.getQuantity() + " = " + item.getPrice()));
    }
}
