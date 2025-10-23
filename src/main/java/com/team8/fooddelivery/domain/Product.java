package com.team8.fooddelivery.domain;

import jakarta.persistence.*;
import java.time.Instant;

public class Product {
  private Integer productId;
  private String name;
  private String description;
  private Double weight;
  private Double price;
  private ProductCategory category;
  private boolean isAvailable;
  private Integer cookingTimeMinutes;

  public Product(Integer productId, String name, String description, Double weight,
                 Double price, ProductCategory category, boolean isAvailable,
                 Integer cookingTimeMinutes) {
    this.productId = productId;
    this.name = name;
    this.description = description;
    this.weight = weight;
    this.price = price;
    this.category = category;
    this.isAvailable = isAvailable;
    this.cookingTimeMinutes = cookingTimeMinutes;
  }

  public enum ProductCategory {
    BAKERY,  MAIN_DISH, DESSERT, DRINK, OTHER
  }

  public Integer getId() { return productId; }
  public String getName() { return name; }
  public String getDescription() { return description; }
  public Double getWeight() { return weight; }
  public Double getPrice() { return price; }
  public ProductCategory getCategory() { return category; }
  public boolean isAvailable() { return isAvailable; }
  public Integer getCookingTimeMinutes() { return cookingTimeMinutes; }

  public void setId(Integer productId) { this.productId = productId; }
  public void setName(String name) { this.name = name; }
  public void setDescription(String description) { this.description = description; }
  public void setWeight(Double weight) { this.weight = weight; }
  public void setPrice(Double price) { this.price = price; }
  public void setCategory(ProductCategory category) { this.category = category; }
  public void setAvailable(boolean available) { isAvailable = available; }
  public void setCookingTimeMinutes(Integer cookingTimeMinutes) { this.cookingTimeMinutes = cookingTimeMinutes; }

}
