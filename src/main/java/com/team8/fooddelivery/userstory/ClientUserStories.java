package com.team8.fooddelivery.userstory;

import com.team8.fooddelivery.model.Shop;
import com.team8.fooddelivery.service.impl.ShopInfoServiceImpl;

public class ClientUserStories {
  public static void main(String[] args) {
    ShopInfoServiceImpl service = new ShopInfoServiceImpl();

    Shop shop = new Shop();
    shop.setNaming("Test Shop");
    shop.setPhoneForAuth("+79123456789");
    Shop registered = service.registerShop(shop, "Test@mail.com", "Password123!", "+79123456789");
    System.out.println("Магазин зарегистрирован: " + registered.getShopId() + ", статус: " + registered.getStatus());

    boolean approved = service.approveShop(registered.getShopId());
    System.out.println("Магазин одобрен: " + approved);

    System.out.println(shop);

    String passResult = service.changePassword(registered.getShopId(), "Test@mail.com", "+79123456789", "NewPassword123!", "Password123!");
    System.out.println("Смена пароля: " + passResult);

    String emailResult = service.changeEmailForAuth(registered.getShopId(), "+79123456789", "NewPassword123!", "new@mail.com");
    System.out.println("Смена email: " + emailResult);

    String phoneResult = service.changePhoneForAuth(registered.getShopId(), "new@mail.com", "NewPassword123!", "+79099999999");
    System.out.println("Смена телефона: " + phoneResult);

  }
}