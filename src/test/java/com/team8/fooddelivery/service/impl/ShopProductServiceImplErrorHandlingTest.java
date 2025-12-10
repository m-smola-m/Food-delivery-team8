package com.team8.fooddelivery.service.impl;

import com.team8.fooddelivery.model.product.Product;
import com.team8.fooddelivery.model.product.ProductCategory;
import com.team8.fooddelivery.model.shop.Shop;
import com.team8.fooddelivery.model.shop.ShopStatus;
import com.team8.fooddelivery.model.shop.ShopType;
import com.team8.fooddelivery.repository.ProductRepository;
import com.team8.fooddelivery.repository.ShopRepository;
import com.team8.fooddelivery.service.DatabaseConnectionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ShopProductServiceImplErrorHandlingTest {

    private ShopProductServiceImpl shopProductService;
    private ShopRepository shopRepository;
    private ProductRepository productRepository;
    private Long testShopId;
    private Long testProductId;

    private Shop buildTestShop() {
        long suffix = System.currentTimeMillis();
        String phoneSuffix = String.format("%07d", suffix % 10_000_000);
        Shop shop = new Shop();
        shop.setNaming("Test Shop " + suffix);
        shop.setDescription("Test Description " + suffix);
        shop.setPublicEmail("public_" + suffix + "@shop.com");
        shop.setEmailForAuth("auth_" + suffix + "@shop.com");
        shop.setPhoneForAuth("+7999" + phoneSuffix);
        shop.setPublicPhone("+7444" + phoneSuffix);
        shop.setStatus(ShopStatus.APPROVED);
        shop.setOwnerName("Owner " + suffix);
        shop.setOwnerContactPhone("+7555" + phoneSuffix);
        shop.setType(ShopType.RESTAURANT);
        shop.setPassword("test_password");
        return shop;
    }

    private Product buildTestProduct(String label) {
        return Product.builder()
                .name("Test Product " + label)
                .description("Description " + label)
                .weight(100.0)
                .price(10.0)
                .category(ProductCategory.MAIN_DISH)
                .available(true)
                .cookingTimeMinutes(Duration.ofMinutes(10))
                .build();
    }

    @BeforeEach
    void setUp() throws SQLException {
        String dbUrl = System.getProperty("db.url", "jdbc:postgresql://localhost:5432/food_delivery");
        String dbUser = System.getProperty("db.user", "postgres");
        String dbPassword = System.getProperty("db.password", "postgres");
        DatabaseConnectionService.setConnectionParams(dbUrl, dbUser, dbPassword);
        DatabaseConnectionService.initializeDatabase();

        shopProductService = new ShopProductServiceImpl();
        shopRepository = new ShopRepository();
        productRepository = new ProductRepository();
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (testProductId != null) {
            try (Connection conn = DatabaseConnectionService.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM products WHERE product_id = ?")) {
                stmt.setLong(1, testProductId);
                stmt.executeUpdate();
            }
        }
        if (testShopId != null) {
            try (Connection conn = DatabaseConnectionService.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM shops WHERE shop_id = ?")) {
                stmt.setLong(1, testShopId);
                stmt.executeUpdate();
            }
        }
        testProductId = null;
        testShopId = null;
    }

    @Test
    @DisplayName("getShopProducts: Should return empty list for non-existent shop")
    void testGetShopProducts_NotFound() {
        List<Product> result = shopProductService.getShopProducts(999999L);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getProductsByCategory: Should return empty list for non-existent shop")
    void testGetProductsByCategory_NotFound() {
        List<Product> result = shopProductService.getProductsByCategory(999999L, ProductCategory.MAIN_DISH);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("updateProductAvailability: Should handle when product not found")
    void testUpdateProductAvailability_ProductNotFound() {
        assertDoesNotThrow(() -> {
            shopProductService.updateProductAvailability(1L, 999999L, true);
        });
    }

    @Test
    @DisplayName("changeShopStatus: Should handle when shop not found")
    void testChangeShopStatus_ShopNotFound() {
        assertDoesNotThrow(() -> {
            shopProductService.changeShopStatus(999999L, ShopStatus.PENDING);
        });
    }

    @Test
    @DisplayName("getShopById: Should return null for non-existent shop")
    void testGetShopById_NotFound() {
        Shop result = shopProductService.getShopById(999999L);
        assertNull(result);
    }

    @Test
    @DisplayName("getShopById: Should return shop when exists")
    void testGetShopById_Success() throws SQLException {
        // Create test shop
        Shop shop = buildTestShop();

        testShopId = shopRepository.save(shop);
        shop.setShopId(testShopId);

        Shop result = shopProductService.getShopById(testShopId);
        assertNotNull(result);
        assertEquals(testShopId, result.getShopId());
    }

    @Test
    @DisplayName("updateShopInfo: Should update shop when exists")
    void testUpdateShopInfo_Success() throws SQLException {
        // Create test shop
        Shop shop = buildTestShop();

        testShopId = shopRepository.save(shop);
        shop.setShopId(testShopId);

        shop.setNaming("Updated Shop");

        Shop result = shopProductService.updateShopInfo(testShopId, shop);
        assertNotNull(result);
        assertEquals("Updated Shop", result.getNaming());
    }

    @Test
    @DisplayName("addProduct: Should add product when shop exists")
    void testAddProduct_Success() throws SQLException {
        // Create test shop
        Shop shop = buildTestShop();

        testShopId = shopRepository.save(shop);

        Product product = buildTestProduct("Add");

        Product result = shopProductService.addProduct(testShopId, product);
        assertNotNull(result);
        assertNotNull(result.getProductId());
        testProductId = result.getProductId();
    }

    @Test
    @DisplayName("updateProduct: Should update product when exists")
    void testUpdateProduct_Success() throws SQLException {
        // Create test shop and product
        Shop shop = buildTestShop();

        testShopId = shopRepository.save(shop);

        Product product = buildTestProduct("Update");

        Product savedProduct = shopProductService.addProduct(testShopId, product);
        testProductId = savedProduct.getProductId();

        Product updatedProduct = Product.builder()
                .productId(testProductId)
                .name("Updated Product")
                .description("Updated Description")
                .weight(150.0)
                .price(15.0)
                .category(ProductCategory.DESSERT)
                .available(false)
                .cookingTimeMinutes(Duration.ofMinutes(15))
                .build();

        Product result = shopProductService.updateProduct(testShopId, testProductId, updatedProduct);
        assertNotNull(result);
        assertEquals("Updated Product", result.getName());
    }

    @Test
    @DisplayName("deleteProduct: Should delete product when exists")
    void testDeleteProduct_Success() throws SQLException {
        // Create test shop and product
        Shop shop = buildTestShop();

        testShopId = shopRepository.save(shop);

        Product product = buildTestProduct("Delete");

        Product savedProduct = shopProductService.addProduct(testShopId, product);
        Long productId = savedProduct.getProductId();

        assertDoesNotThrow(() -> {
            shopProductService.deleteProduct(testShopId, productId);
        });

        // Verify product is deleted
        assertTrue(productRepository.findById(productId).isEmpty());
    }
}
