package com.team8.fooddelivery.domain;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

public class Courier {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private Instant createdAt = Instant.now();
    private Boolean isActive = true;
    private Boolean isAvailable = true;
    private Long totalEarnings = 0L;
    private Long totalPenalties = 0L;
}
