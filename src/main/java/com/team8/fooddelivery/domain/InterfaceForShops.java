package com.team8.fooddelivery.domain;

import java.util.List;

public interface InterfaceForShops {
  
  Shops registerShop(Shops infoAbout);

  void approveShop(Long shopId);

  void rejectShop(String rejectionReason);

  String changePassword(Long shopId, String emailForAU, String phoneForAU);

  String changeEmailForAU(Long shopId, String phoneForAU, String password);

  String changePhoneForAU(Long shopId, String emailForAU, String password);

  //Shops getShopById(Long shopId);

  Shops updateShopInfo(Long shopId);

  Product addProduct(Long shopId);

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
