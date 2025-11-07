package com.team8.fooddelivery.domain;

import lombok.*;

import java.time.Instant;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Restaurant {
    private Long id;
    private String name;
    private String address;
    private String phone;
    private String email;
    private String description;
    private Instant createdAt = Instant.now();
    private Boolean isActive = true;
    private Boolean isOpen = true;
}
