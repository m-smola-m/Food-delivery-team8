package com.team8.fooddelivery.model;

import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
  private Long notificationId;
  private Long userId;
  private String title;
  private String message;
  private Instant createdAt = Instant.now();
  private Boolean read = false;
}