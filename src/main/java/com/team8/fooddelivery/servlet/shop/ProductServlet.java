package com.team8.fooddelivery.servlet.shop;

import com.team8.fooddelivery.model.product.Product;
import com.team8.fooddelivery.model.product.ProductCategory;
import com.team8.fooddelivery.repository.ProductRepository;
import com.team8.fooddelivery.service.ShopProductService;
import com.team8.fooddelivery.service.impl.ShopProductServiceImpl;
import com.team8.fooddelivery.util.SessionManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Duration;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@WebServlet("/products/*")
public class ProductServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(ProductServlet.class);
    private final ShopProductService productService = new ShopProductServiceImpl();
    private final ProductRepository productRepository = new ProductRepository();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        log.debug("ProductServlet doGet: pathInfo={}, requestURI={}", pathInfo, request.getRequestURI());
        
        if (pathInfo == null || pathInfo.isEmpty() || "/".equals(pathInfo)) {
            // Если нет pathInfo, редиректим на список товаров
            response.sendRedirect(request.getContextPath() + "/products/list");
            return;
        }
        
        if ("/list".equals(pathInfo)) {
            handleProductList(request, response);
        } else if ("/add-form".equals(pathInfo)) {
            handleAddForm(request, response);
        } else if ("/edit-form".equals(pathInfo)) {
            handleEditForm(request, response);
        } else if ("/by-shop".equals(pathInfo)) {
            handleProductsByShop(request, response);
        } else if ("/categories".equals(pathInfo)) {
            handleShopCategories(request, response);
        } else {
            log.warn("ProductServlet: unknown pathInfo={}", pathInfo);
            response.sendError(404);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        try {
            if ("/add".equals(pathInfo)) {
                handleAddProduct(request, response);
            } else if ("/update".equals(pathInfo)) {
                handleUpdateProduct(request, response);
            } else if ("/delete".equals(pathInfo)) {
                handleDeleteProduct(request, response);
            } else if ("/toggle-availability".equals(pathInfo)) {
                handleToggleAvailability(request, response);
            } else {
                response.sendError(404);
            }
        } catch (Exception e) {
            log.error("Error", e);
            response.sendError(500);
        }
    }

    /**
     * Список товаров магазина
     */
    private void handleProductList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        Long shopId = SessionManager.getUserId(session);
        
        log.debug("handleProductList: shopId={}, session={}", shopId, session != null);
        
        if (shopId == null) {
            log.warn("handleProductList: shopId is null, redirecting to login");
            response.sendRedirect(request.getContextPath() + "/shop/login");
            return;
        }
        
        try {
            List<Product> products = productService.getShopProducts(shopId);
            log.debug("handleProductList: loaded {} products for shop {}", products.size(), shopId);
            request.setAttribute("products", products);
            request.setAttribute("shopId", shopId);
            request.getRequestDispatcher("/WEB-INF/jsp/shop/products-list.jsp").forward(request, response);
        } catch (Exception e) {
            log.error("Error loading products for shop {}", shopId, e);
            request.setAttribute("error", "Ошибка загрузки товаров: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/shop/products-list.jsp").forward(request, response);
        }
    }

    /**
     * Форма добавления товара
     */
    private void handleAddForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        Long shopId = SessionManager.getUserId(request.getSession());
        if (shopId == null) {
            response.sendRedirect(request.getContextPath() + "/shop/login");
            return;
        }
        
        request.setAttribute("categories", ProductCategory.values());
        request.getRequestDispatcher("/WEB-INF/jsp/shop/product-form.jsp").forward(request, response);
    }

    /**
     * Форма редактирования товара
     */
    private void handleEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        Long shopId = SessionManager.getUserId(request.getSession());
        if (shopId == null) {
            response.sendRedirect(request.getContextPath() + "/shop/login");
            return;
        }
        
        try {
            Long productId = Long.parseLong(request.getParameter("id"));
            
            // Получаем товар по ID через репозиторий (включая недоступные)
            Optional<Product> productOpt = productRepository.findById(productId);
            
            if (productOpt.isEmpty()) {
                log.warn("Product {} not found", productId);
                response.sendRedirect(request.getContextPath() + "/products/list?error=product_not_found");
                return;
            }
            
            Product product = productOpt.get();
            
            // Проверяем, что товар принадлежит магазину
            List<Product> shopProducts = productRepository.findByShopId(shopId);
            boolean belongsToShop = shopProducts.stream()
                    .anyMatch(p -> p.getProductId().equals(productId));
            
            if (!belongsToShop) {
                log.warn("Product {} does not belong to shop {}", productId, shopId);
                response.sendRedirect(request.getContextPath() + "/products/list?error=access_denied");
                return;
            }
            
            request.setAttribute("categories", ProductCategory.values());
            request.setAttribute("product", product);
            request.setAttribute("isEdit", true);
            request.getRequestDispatcher("/WEB-INF/jsp/shop/product-form.jsp").forward(request, response);
        } catch (SQLException e) {
            log.error("Database error loading product form", e);
            response.sendRedirect(request.getContextPath() + "/products/list?error=database_error");
        } catch (Exception e) {
            log.error("Error loading product form", e);
            response.sendRedirect(request.getContextPath() + "/products/list?error=load_failed");
        }
    }

    /**
     * Добавить новый товар
     */
    private void handleAddProduct(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        Long shopId = SessionManager.getUserId(request.getSession());
        if (shopId == null) {
            response.sendRedirect(request.getContextPath() + "/shop/login");
            return;
        }
        
        try {
            String name = request.getParameter("name");
            String descriptionOfProduct = request.getParameter("descriptionOfProduct");
            String description = descriptionOfProduct; // Используем descriptionOfProduct как описание
            
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Название товара обязательно");
            }
            
            double price;
            try {
                price = Double.parseDouble(request.getParameter("price"));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Некорректная цена");
            }
            
            String categoryStr = request.getParameter("category");
            if (categoryStr == null || categoryStr.trim().isEmpty()) {
                throw new IllegalArgumentException("Категория обязательна");
            }
            ProductCategory category;
            try {
                category = ProductCategory.valueOf(categoryStr);
            } catch (IllegalArgumentException e) {
                log.warn("Invalid category '{}', using OTHER", categoryStr);
                category = ProductCategory.OTHER;
            }
            
            boolean isAvailable = request.getParameter("isAvailable") != null;
            
            String cookingTimeStr = request.getParameter("cookingTimeMinutes");
            Duration cookingTime = null;
            if (cookingTimeStr != null && !cookingTimeStr.trim().isEmpty()) {
                try {
                    int minutes = Integer.parseInt(cookingTimeStr);
                    if (minutes > 0) {
                        cookingTime = Duration.ofMinutes(minutes);
                    }
                } catch (NumberFormatException e) {
                    // Игнорируем ошибку парсинга
                }
            }
            
            String weightStr = request.getParameter("weight");
            Double weight = null;
            if (weightStr != null && !weightStr.trim().isEmpty()) {
                try {
                    weight = Double.parseDouble(weightStr);
                } catch (NumberFormatException e) {
                    // Игнорируем ошибку парсинга
                }
            }

            Product product = Product.builder()
                    .name(name)
                    .description(description != null ? description : "")
                    .price(price)
                    .category(category)
                    .available(isAvailable)
                    .cookingTimeMinutes(cookingTime)
                    .weight(weight)
                    .build();
            
            Product savedProduct = productService.addProduct(shopId, product);
            log.info("Product added: {} for shop {}", savedProduct.getProductId(), shopId);
            
            response.sendRedirect(request.getContextPath() + "/products/list?added=true");
        } catch (Exception e) {
            log.error("Error adding product", e);
            request.setAttribute("error", "Ошибка при добавлении товара: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/shop/product-form.jsp").forward(request, response);
        }
    }

    /**
     * Обновить товар
     */
    private void handleUpdateProduct(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        Long shopId = SessionManager.getUserId(request.getSession());
        if (shopId == null) {
            response.sendRedirect(request.getContextPath() + "/shop/login");
            return;
        }
        
        try {
            Long productId = Long.parseLong(request.getParameter("productId"));
            String name = request.getParameter("name");
            String descriptionOfProduct = request.getParameter("descriptionOfProduct");
            String description = descriptionOfProduct;
            
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Название товара обязательно");
            }
            
            double price;
            try {
                price = Double.parseDouble(request.getParameter("price"));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Некорректная цена");
            }
            
            String categoryStr = request.getParameter("category");
            if (categoryStr == null || categoryStr.trim().isEmpty()) {
                throw new IllegalArgumentException("Категория обязательна");
            }
            ProductCategory category;
            try {
                category = ProductCategory.valueOf(categoryStr);
            } catch (IllegalArgumentException e) {
                log.warn("Invalid category '{}', using OTHER", categoryStr);
                category = ProductCategory.OTHER;
            }
            
            boolean isAvailable = request.getParameter("isAvailable") != null;
            
            String cookingTimeStr = request.getParameter("cookingTimeMinutes");
            Duration cookingTime = null;
            if (cookingTimeStr != null && !cookingTimeStr.trim().isEmpty()) {
                try {
                    int minutes = Integer.parseInt(cookingTimeStr);
                    if (minutes > 0) {
                        cookingTime = Duration.ofMinutes(minutes);
                    }
                } catch (NumberFormatException e) {
                    // Игнорируем ошибку парсинга
                }
            }
            
            String weightStr = request.getParameter("weight");
            Double weight = null;
            if (weightStr != null && !weightStr.trim().isEmpty()) {
                try {
                    weight = Double.parseDouble(weightStr);
                } catch (NumberFormatException e) {
                    // Игнорируем ошибку парсинга
                }
            }

            Product product = Product.builder()
                    .productId(productId)
                    .name(name)
                    .description(description != null ? description : "")
                    .price(price)
                    .category(category)
                    .available(isAvailable)
                    .cookingTimeMinutes(cookingTime)
                    .weight(weight)
                    .build();
            
            Product updatedProduct = productService.updateProduct(shopId, productId, product);
            log.info("Product updated: {} for shop {}", updatedProduct.getProductId(), shopId);
            
            response.sendRedirect(request.getContextPath() + "/products/list?updated=true");
        } catch (Exception e) {
            log.error("Error updating product", e);
            request.setAttribute("error", "Ошибка при обновлении товара: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/shop/product-form.jsp").forward(request, response);
        }
    }

    /**
     * Удалить товар
     */
    private void handleDeleteProduct(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        Long shopId = SessionManager.getUserId(request.getSession());
        if (shopId == null) {
            response.sendRedirect(request.getContextPath() + "/shop/login");
            return;
        }
        
        try {
            Long productId = Long.parseLong(request.getParameter("productId"));
            productService.deleteProduct(shopId, productId);
            log.info("Product deleted: {} from shop {}", productId, shopId);
            
            response.sendRedirect(request.getContextPath() + "/products/list?deleted=true");
        } catch (Exception e) {
            log.error("Error deleting product", e);
            // Передаём понятное сообщение об ошибке на страницу списка
            String reason = e.getMessage() != null ? e.getMessage() : "delete_failed";
            String encoded = URLEncoder.encode(reason, StandardCharsets.UTF_8);
            response.sendRedirect(request.getContextPath() + "/products/list?error=delete_failed&error_reason=" + encoded);
        }
    }

    /**
     * Переключить доступность товара
     */
    private void handleToggleAvailability(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        Long shopId = SessionManager.getUserId(request.getSession());
        if (shopId == null) {
            response.sendRedirect(request.getContextPath() + "/shop/login");
            return;
        }
        
        try {
            Long productId = Long.parseLong(request.getParameter("productId"));
            boolean isAvailable = Boolean.parseBoolean(request.getParameter("available"));
            
            productService.updateProductAvailability(shopId, productId, !isAvailable);
            log.info("Product availability toggled: {} for shop {}", productId, shopId);
            
            response.sendRedirect(request.getContextPath() + "/products/list?availability_changed=true");
        } catch (Exception e) {
            log.error("Error toggling availability", e);
            response.sendRedirect(request.getContextPath() + "/products/list?error=toggle_failed");
        }
    }

    private void handleProductsByShop(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Long shopId = Long.parseLong(request.getParameter("shopId"));
            String categoryParam = request.getParameter("category");

            List<Product> products;
            if (categoryParam != null && !categoryParam.isEmpty()) {
                ProductCategory category;
                try {
                    category = ProductCategory.valueOf(categoryParam);
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid category '{}' in request, returning empty list", categoryParam);
                    products = new ArrayList<>();
                    sendJson(response, productsToJson(products));
                    return;
                }
                // Для клиентов возвращаем только доступные товары
                products = productService.getProductsByCategory(shopId, category);
            } else {
                // Для клиентов возвращаем только доступные товары
                products = productService.getShopProducts(shopId).stream()
                    .filter(Product::getAvailable)
                    .toList();
            }
            log.debug("handleProductsByShop: shopId={}, category={}, products count={}", shopId, categoryParam, products.size());
            sendJson(response, productsToJson(products));
        } catch (Exception e) {
            log.error("Error loading products by shop", e);
            response.sendError(500);
        }
    }

    private void handleShopCategories(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Long shopId = Long.parseLong(request.getParameter("shopId"));
            List<ProductCategory> categories = productService.getShopCategories(shopId);
            sendJson(response, categoriesToJson(categories));
        } catch (Exception e) {
            log.error("Error loading categories", e);
            response.sendError(500);
        }
    }

    private void sendJson(HttpServletResponse response, String payload) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(payload);
    }

    private String productsToJson(List<Product> products) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            json.append("{")
                .append("\"productId\":").append(product.getProductId()).append(",")
                .append("\"name\":\"").append(escape(product.getName())).append("\",")
                .append("\"description\":\"").append(escape(product.getDescription())).append("\",")
                .append("\"price\":").append(product.getPrice()).append(",")
                .append("\"weight\":").append(product.getWeight() != null ? product.getWeight() : 0).append(",")
                .append("\"category\":\"").append(product.getCategory() != null ? product.getCategory().name() : "").append("\"")
                .append("}");
            if (i < products.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");
        return json.toString();
    }

    private String categoriesToJson(List<ProductCategory> categories) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < categories.size(); i++) {
            json.append("\"").append(categories.get(i).name()).append("\"");
            if (i < categories.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");
        return json.toString();
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
