package com.team8.fooddelivery.model;

import lombok.*;
import java.time.Duration;

@Data
@AllArgsConstructor
public class Product {
  private Long productId; // Long
  private String name;
  private String description;
  private Double weight;
  private Double price;
  private ProductCategory category;
  private boolean isAvailable;
  private Duration cookingTimeMinutes;  // Duration
}
