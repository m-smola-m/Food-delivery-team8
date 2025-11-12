package com.team8.fooddelivery.service.impl;

import com.team8.fooddelivery.model.Shop;
import com.team8.fooddelivery.model.ShopStatus;
import com.team8.fooddelivery.service.ShopInfoService;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static com.team8.fooddelivery.util.ValidationUtils.*;

public class ShopInfoServiceImpl implements ShopInfoService {

  private Map<Long, Shop> shopRepository = new HashMap<>();
  private Map<String, String> authRepository = new HashMap<>(); // email -> password
  private Map<String, Long> phoneToShopIdMap = new HashMap<>(); // phone -> shopId
  private AtomicLong idGenerator = new AtomicLong(1);

  @Override
  public Shop registerShop(Shop infoAbout, String emailForAuth, String password, String phoneForAuth) {
    if (infoAbout == null || emailForAuth == null || emailForAuth.trim().isEmpty() ||
        password == null || password.trim().isEmpty() || phoneForAuth == null || phoneForAuth.trim().isEmpty()) {
      throw new IllegalArgumentException("Все поля должны быть заполнены");
    }

    if (!isValidEmail(emailForAuth)) {
      throw new IllegalArgumentException("Некорректный формат email");
    }

    if (!isValidPhone(phoneForAuth)) {
      throw new IllegalArgumentException("Некорректный формат телефона");
    }

    if (!isValidPassword(password)) {
      throw new IllegalArgumentException("Некорректный формат пароля");
    }

    if (authRepository.containsKey(emailForAuth)) {
      throw new IllegalArgumentException("Email уже используется");
    }

    if (phoneToShopIdMap.containsKey(infoAbout.getPhoneForAuth())) {
      throw new IllegalArgumentException("Телефон уже используется");
    }

    Long shopId = generateId();
    infoAbout.setStatus(ShopStatus.PENDING);
    infoAbout.setShopId(shopId);
    infoAbout.setEmailForAuth(emailForAuth);
    infoAbout.setPhoneForAuth(phoneForAuth);
    infoAbout.setPassword(password);

    authRepository.put(emailForAuth, password);
    phoneToShopIdMap.put(infoAbout.getPhoneForAuth(), shopId);
    shopRepository.put(shopId, infoAbout);

    System.out.println("Магазин зарегистрирован с ID: " + shopId + ". Ожидает подтверждения.");
    return infoAbout;
  }

  @Override
  public boolean approveShop(Long shopId) {
    Shop shop = shopRepository.get(shopId);
    if (shop == null) {
      System.out.println("Магазин с ID " + shopId + " не найден");
      return false;
    }

    if (shop.getStatus() != ShopStatus.PENDING) {
      System.out.println("Магазин с ID " + shopId + " уже был обработан. Текущий статус: " + shop.getStatus());
      return false;
    }

    shop.setStatus(ShopStatus.APPROVED);
    shopRepository.put(shopId, shop);
    System.out.println("Магазин с ID " + shopId + " одобрен");
    return true;
  }

  @Override
  public boolean rejectShop(String rejectionReason, Long shopId) {
    Shop shop = shopRepository.get(shopId);
    if (shop == null) {
      System.out.println("Магазин с ID " + shopId + " не найден");
      return false;
    }

    if (shop.getStatus() != ShopStatus.PENDING) {
      System.out.println("Магазин с ID " + shopId + " уже был обработан. Текущий статус: " + shop.getStatus());
      return false;
    }

    shop.setStatus(ShopStatus.REJECTED);
    shopRepository.put(shopId, shop);
    System.out.println("Магазин с ID " + shopId + " отклонен. Причина: " + rejectionReason);
    return true;
  }

  @Override
  public String changePassword(Long shopId, String emailForAuth, String phoneForAuth, String newPassword, String password) {
    if (newPassword == null || newPassword.trim().isEmpty()) {
      throw new IllegalArgumentException("Новый пароль не может быть пустым");
    }

    if (password == null || password.trim().isEmpty()) {
      throw new IllegalArgumentException("Пароль не может быть пустым");
    }

    if (!isValidPassword(newPassword)) {
      throw new IllegalArgumentException("Новый пароль не корректный");
    }

    Shop shop = shopRepository.get(shopId);

    if (shop == null) {
      throw new IllegalArgumentException("Магазин с ID " + shopId + " не найден");
    }

    if (!shop.getEmailForAuth().equals(emailForAuth) || !shop.getPhoneForAuth().equals(phoneForAuth)) {
      throw new SecurityException("Неверные аутентификационные данные");
    }

    if (password.equals(authRepository.get(emailForAuth))) {
      authRepository.put(emailForAuth, newPassword);
      System.out.println("Пароль для магазина с ID " + shopId + " успешно изменен");
    }
    return "Пароль успешно изменен";
  }

  @Override
  public String changeEmailForAuth(Long shopId, String phoneForAuth, String password, String newEmailForAuth) {
    if (newEmailForAuth == null || newEmailForAuth.trim().isEmpty()) {
      throw new IllegalArgumentException("Новый email не может быть пустым");
    }

    if (!isValidEmail(newEmailForAuth)) {
      throw new IllegalArgumentException("Некорректный формат нового email");
    }

    Shop shop = shopRepository.get(shopId);
    if (shop == null) {
      throw new IllegalArgumentException("Магазин с ID " + shopId + " не найден");
    }

    if (!shop.getPhoneForAuth().equals(phoneForAuth)) {
      throw new SecurityException("Неверный телефон для аутентификации");
    }

    String currentPassword = authRepository.get(shop.getEmailForAuth());
    if (currentPassword == null || !currentPassword.equals(password)) {
      throw new SecurityException("Неверный пароль");
    }

    if (authRepository.containsKey(newEmailForAuth)) {
      throw new IllegalArgumentException("Новый email уже используется другим магазином");
    }

    String oldEmail = shop.getEmailForAuth();
    authRepository.remove(oldEmail);
    authRepository.put(newEmailForAuth, password);
    shop.setEmailForAuth(newEmailForAuth);
    shopRepository.put(shopId, shop);

    System.out.println("Email для магазина с ID " + shopId + " изменен с " + oldEmail + " на " + newEmailForAuth);
    return "Email для аутентификации успешно изменен";
  }

  @Override
  public String changePhoneForAuth(Long shopId, String emailForAuth, String password, String newPhoneForAuth) {
    if (newPhoneForAuth == null || newPhoneForAuth.trim().isEmpty()) {
      throw new IllegalArgumentException("Новый телефон не может быть пустым");
    }

    Shop shop = shopRepository.get(shopId);
    if (shop == null) {
      throw new IllegalArgumentException("Магазин с ID " + shopId + " не найден");
    }

    if (!shop.getEmailForAuth().equals(emailForAuth)) {
      throw new SecurityException("Неверный email для аутентификации");
    }

    String currentPassword = authRepository.get(emailForAuth);
    if (currentPassword == null || !currentPassword.equals(password)) {
      throw new SecurityException("Неверный пароль");
    }

    if (phoneToShopIdMap.containsKey(newPhoneForAuth)) {
      throw new IllegalArgumentException("Новый телефон уже используется другим магазином");
    }

    String oldPhone = shop.getPhoneForAuth();
    phoneToShopIdMap.remove(oldPhone);
    phoneToShopIdMap.put(newPhoneForAuth, shopId);
    shop.setPhoneForAuth(newPhoneForAuth);
    shopRepository.put(shopId, shop);

    return "Телефон для аутентификации успешно изменен";
  }

  private Long generateId() {
    return idGenerator.getAndIncrement();
  }
}