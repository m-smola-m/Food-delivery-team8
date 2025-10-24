package com.team8.fooddelivery.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import java.util.List;

public interface InterfaceForShops {

  void approveStore(UUID shopId); // одобрение регистрации

  void rejectStore(String rejectionReason); // отклонение регистрации

  String changePassword(UUID shopId, String emailForAU, String phoneForAU);

  String changeEmailForAU(UUID shopId, String phoneForAU, String password);

  String changePhoneForAU(UUID shopId, String emailForAU, String password);

  Shops getStoreById(UUID shopId); // поиск реторана по id

  Shops updateStoreInfo(UUID shopId);

  Product addProduct(UUID shopId);

  Product updateProduct(UUID shopId, Integer productId);

  void deleteProduct(UUID shopId, Integer productId);

  List<Product> getStoreProducts(UUID shopId);

  List<Product> getProductsByCategory(UUID shopId, Product.ProductCategory category);

  void updateProductAvailability(UUID shopId, Integer productId, boolean isAvailable);

  List<OrderForStore> getPendingOrders(UUID storeId);
}
