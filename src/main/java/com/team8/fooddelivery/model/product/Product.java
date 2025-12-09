package com.team8.fooddelivery.model.product;

import lombok.*;
import java.time.Duration;

@Data
@Builder
@NoArgsConstructor
public class Product {
  private Long productId;
  private String name;
  private String description;
  private Double weight;
  private Double price;
  private ProductCategory category;
  @Builder.Default
  private Boolean available = true;
  private Duration cookingTimeMinutes;
  private String photoUrl;

  // Constructor without photoUrl for backward compatibility
  public Product(Long productId, String name, String description, double weight, double price, ProductCategory category, boolean available, Duration cookingTimeMinutes) {
    this(productId, name, description, (Double) weight, price, category, available, cookingTimeMinutes, null);
  }

  // Full constructor
  public Product(Long productId, String name, String description, Double weight, Double price, ProductCategory category, Boolean available, Duration cookingTimeMinutes, String photoUrl) {
    this.productId = productId;
    this.name = name;
    this.description = description;
    this.weight = weight;
    this.price = price;
    this.category = category;
    this.available = available;
    this.cookingTimeMinutes = cookingTimeMinutes;
    this.photoUrl = photoUrl;
  }
}