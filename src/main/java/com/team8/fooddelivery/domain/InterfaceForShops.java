package com.team8.fooddelivery.domain;

import java.util.List;

public interface InterfaceForShops { // ShopsInfoService, ShopsProductsService, ShopsOrderService + разделить на 3 интерфейса
  
  Shops registerShop(Shops infoAbout); // + набор полей

  void approveShop(Long shopId); // возвращать статус

  void rejectShop(String rejectionReason); // возвращать статус

  String changePassword(Long shopId, String emailForAU, String phoneForAU); // + new password

  String changeEmailForAU(Long shopId, String phoneForAU, String password); // + new email

  String changePhoneForAU(Long shopId, String emailForAU, String password); // + new phone

  Shops getShopById(Long shopId);

  Shops updateShopInfo(Long shopId); // + new Shop

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
