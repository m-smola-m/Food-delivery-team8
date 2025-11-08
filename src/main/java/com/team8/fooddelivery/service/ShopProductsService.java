package com.team8.fooddelivery.service;

import com.team8.fooddelivery.model.Product;
import com.team8.fooddelivery.model.Shop;
import com.team8.fooddelivery.model.ProductCategory;

import java.util.List;

public interface ShopProductsService { // ShopsInfoService, ShopsProductsService, ShopsOrderService + разделить на 3 интерфейса

  Shop getShopById(Long shopId);

  Shop updateShopInfo(Long shopId); // + new Shop

  Product addProduct(Long shopId); // + набор полей

  Product updateProduct(Long shopId, Integer productId);

  void deleteProduct(Long shopId, Integer productId);

  List<Product> getShopProducts(Long shopId);

  List<Product> getProductsByCategory(Long shopId, ProductCategory category);

  void updateProductAvailability(Long shopId, Integer productId, boolean isAvailable);

  void changeShopStatus(Long shopId);

}
