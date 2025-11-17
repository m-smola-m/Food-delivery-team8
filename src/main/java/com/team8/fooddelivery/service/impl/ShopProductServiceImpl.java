package com.team8.fooddelivery.service.impl;

import com.team8.fooddelivery.model.Product;
import com.team8.fooddelivery.model.ProductCategory;
import com.team8.fooddelivery.model.Shop;
import com.team8.fooddelivery.model.ShopStatus;
import com.team8.fooddelivery.service.ShopProductService;
import java.util.*;

public class ShopProductServiceImpl implements ShopProductService {

  private Map<Long, Shop> shops = new HashMap<>();
  private Map<Long, List<Product>> shopProducts = new HashMap<>();

  @Override
  public List<Product> getProductsByCategory(Long shopId, ProductCategory   category) {
    List<Product> result = new ArrayList<>();
    List<Product> products = shopProducts.get(shopId);

    if (products != null) {
      for (Product product : products) {
        if (product.getCategory().equals(category)) {
          result.add(product);
        }
      }
    }
    return result;
  }

  @Override
  public void updateProductAvailability(Long shopId, Long productId, boolean isAvailable) {
    List<Product> products = shopProducts.get(shopId);
    if (products != null) {
      for (Product product : products) {
        if (product.getProductId().equals(productId)) {
          product.setAvailable(isAvailable);
          return;
        }
      }
    }
    System.out.println("Продукт не найден");
  }

  @Override
  public void changeShopStatus(Long shopId, ShopStatus status) {
    Shop shop = shops.get(shopId);
    if (shop != null) {
      try {
        shop.setStatus(status);
      } catch (Exception e) {
        System.out.println("Такого статуса нет");
      }
    } else {
      System.out.println("Магазин не найден");
    }
  }

  @Override
  public Shop getShopById(Long shopId) {
    return shops.get(shopId);
  }

  @Override
  public Shop updateShopInfo(Long shopId, Shop shop) {
    shops.put(shopId, shop);
    return shop;
  }

  @Override
  public Product addProduct(Long shopId, Product product) {
    if (!shopProducts.containsKey(shopId)) {
      shopProducts.put(shopId, new ArrayList<>());
    }
    shopProducts.get(shopId).add(product);
    return product;
  }

  @Override
  public Product updateProduct(Long shopId, Long productId, Product product) {
    List<Product> products = shopProducts.get(shopId);
    if (products != null) {
      for (int i = 0; i < products.size(); i++) {
        if (products.get(i).getProductId().equals(productId)) {
          products.set(i, product);
          return product;
        }
      }
    }
    return product;
  }

  @Override
  public void deleteProduct(Long shopId, Long productId) {
    List<Product> products = shopProducts.get(shopId);
    if (products != null) {
      products.removeIf(p -> p.getProductId().equals(productId));
    }
  }

  @Override
  public List<Product> getShopProducts(Long shopId) {
    return shopProducts.getOrDefault(shopId, new ArrayList<>());
  }
}