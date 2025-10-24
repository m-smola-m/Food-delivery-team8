package com.team8.fooddelivery.domain;

import lombok.*;

@Data
@AllArgsConstructor
public class Product {
  private Integer productId; // Long
  private String name;
  private String description;
  private Double weight;
  private Double price;
  private ProductCategory category;
  private boolean isAvailable;
  private Integer cookingTimeMinutes;  // Duration

  public enum ProductCategory {
    BAKERY,  MAIN_DISH, DESSERT, DRINK, OTHER
  }
}
