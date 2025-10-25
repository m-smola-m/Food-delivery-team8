package com.team8.fooddelivery.service;
import com.team8.fooddelivery.model.Order;
import com.team8.fooddelivery.model.Product;
import com.team8.fooddelivery.model.Shop;

import java.util.List;

public interface ShopService { // ShopsInfoService, ShopsProductsService, ShopsOrderService + разделить на 3 интерфейса

  Shop getShopById(Long shopId);

  Shop updateShopInfo(Long shopId); // + new Shop

  Product addProduct(Long shopId); // + набор полей

  Product updateProduct(Long shopId, Integer productId);

  void deleteProduct(Long shopId, Integer productId);

  List<Product> getShopProducts(Long shopId);

  List<Product> getProductsByCategory(Long shopId, Product.ProductCategory category);

  void updateProductAvailability(Long shopId, Integer productId, boolean isAvailable);

  List<Order> getPendingOrders(Long shopId);

  void changeShopStatus(Long shopId);

  void changeOrderStatus(Long shopId, Long orderId);

  void rejectOrder(Long orderId, String reason);
}
